package org.yanex.vika;

import com.mobiata.bb.ui.decor.NinePatchBackground;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;
import org.yanex.vika.api.APIException;
import org.yanex.vika.api.Authentication;
import org.yanex.vika.api.Authentication.Token;
import org.yanex.vika.api.ErrorCodes;
import org.yanex.vika.api.http.LinkHelper;
import org.yanex.vika.api.util.APIHelper;
import org.yanex.vika.gui.screen.LoginScreenGui;
import org.yanex.vika.gui.screen.VkMainScreen;
import org.yanex.vika.gui.dialog.WaitingDialog;
import org.yanex.vika.gui.util.*;
import org.yanex.vika.gui.widget.VkButtonField;
import org.yanex.vika.gui.widget.VkEditTextField;
import org.yanex.vika.gui.widget.VkPasswordEditField;
import org.yanex.vika.gui.widget.base.*;
import org.yanex.vika.gui.widget.manager.VerticalCenterFieldManager;
import org.yanex.vika.local.VikaResource;
import org.yanex.vika.storage.OptionsStorage;

public class LoginScreen extends VkMainScreen implements FieldChangeListener {

  public LoginScreen() {
    super(Manager.NO_VERTICAL_SCROLL);

    setFont(Fonts.defaultFont);
    setTitle((Field) null);

    new LoginScreenGui(this);
    addMenuItem(new LinkHelper.SelectConnectionTypeItem());
  }

  public void _register() {
    String regSid = OptionsStorage.instance.getString("register.sid", null);
    String regTimeStamp = OptionsStorage.instance.getString("register.timestamp", null);
    long ts = 0;
    if (regTimeStamp != null) {
      try {
        ts = Long.parseLong(regTimeStamp);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    // valid for an hour
    if (regSid != null && ts > 0 && System.currentTimeMillis() - ts < 1000 * 3600) {
      String[] choices = {tr(VikaResource.Continue),
        tr(VikaResource.New_registration)};

      if (Dialog.ask(tr(VikaResource.Uncompleted_registration), choices, 0) == 0) {
        new ConfirmationCodeScreen().show();
      } else {
        OptionsStorage.instance.delete("register.phone");
        OptionsStorage.instance.delete("register.firstName");
        OptionsStorage.instance.delete("register.lastName");
        OptionsStorage.instance.delete("register.sid");
        OptionsStorage.instance.delete("register.timestamp");
        new RegisterScreen().show();
      }
    } else {
      new RegisterScreen().show();
    }
  }

  public void _login(final String login, final String password) {
    if (login.length() == 0 || password.length() == 0) {
      return;
    }

    final WaitingDialog dialog = new WaitingDialog(tr(VikaResource.Signingin));
    final APIHelper helper = new APIHelper() {

      public void after(Object obj) {
        dialog.dismiss();
        Token t = (Token) obj;

        OptionsStorage.instance.set("account.access_token", t.getToken());
        OptionsStorage.instance.set("account.user_id", Long.toString(t.getUserId()));
        OptionsStorage.instance.set("account.secret", "");

        Vika.createAPI(t.getToken(), t.getUserId());

        Vika.api().longpoll.start();
        new RootScreen().show();
        close();
      }

      public void error(int error) {
        dialog.dismiss();

        if (error == ErrorCodes.UNAUTHORIZED) {
          Dialog.alert(tr(VikaResource.Wrong_password));
        } else if (error == ErrorCodes.NETWORK_ERROR || error == ErrorCodes.NO_NETWORK) {
          Dialog.alert(tr(VikaResource.Network_is_not_available));
        }
      }

      public Object task() throws APIException {
        return Authentication.getToken(login, password);
      }
    };

    dialog.setCancellable(true);
    dialog.setListener(new WaitingDialog.WaitingDialogListener() {

      public void onCancel() {
        helper.interrupt();
      }
    });

    helper.start();
    dialog.show();
  }

  public boolean isDirty() {
    return false;
  }

}
