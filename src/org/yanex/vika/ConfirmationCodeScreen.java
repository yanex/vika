package org.yanex.vika;

import me.regexp.RE;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;
import org.yanex.vika.api.APIException;
import org.yanex.vika.api.AuthAPI;
import org.yanex.vika.api.ErrorCodes;
import org.yanex.vika.api.http.LinkHelper;
import org.yanex.vika.api.util.APIHelper;
import org.yanex.vika.gui.screen.VkMainScreen;
import org.yanex.vika.gui.util.*;
import org.yanex.vika.gui.widget.VkButtonField;
import org.yanex.vika.gui.widget.VkEditTextField;
import org.yanex.vika.gui.widget.VkPasswordEditField;
import org.yanex.vika.gui.widget.base.*;
import org.yanex.vika.gui.widget.manager.VerticalCenterFieldManager;
import org.yanex.vika.local.VikaResource;
import org.yanex.vika.storage.OptionsStorage;

class ConfirmationCodeScreen extends VkMainScreen implements FieldChangeListener,
  FocusChangeListener {

  private static final Background DARK_BG =
    BackgroundFactory.createBitmapBackground(R.instance.getBitmap(Files.DARK_BG));

  private static final Bitmap REGISTER_VK = R.instance.getBitmap(Files.REGISTERFORM_VK),
    REGISTER_ERROR = R.instance.getBitmap(Files.REGISTERFORM_ERROR);

  private static final RE CONFIRMATION_CODE_RE = new RE("^[0-9]{4,4}$"),
    PASSWORD_RE = new RE("^.{6,}$");

  private EditTextField confirmationCode;
  private CustomPasswordEditField password;
  private ButtonField register;

  public ConfirmationCodeScreen() {
    super(Manager.NO_VERTICAL_SCROLL);

    setFont(Fonts.defaultFont);
    setTitle((Field) null);

    addMenuItem(new LinkHelper.SelectConnectionTypeItem());

    initGui();
  }

  public void fieldChanged(Field field, int context) {
    if (field == register) {
      final String code = confirmationCode.getText();
      final String password = this.password.getText();
      final String phone = OptionsStorage.instance.getString("register.phone", "");

      if (!ConfirmationCodeScreen.CONFIRMATION_CODE_RE.match(code)) {
        Dialog.alert(tr(VikaResource.Confirmation_code_is));
        return;
      }

      // Check password for simplicity
      if (!ConfirmationCodeScreen.PASSWORD_RE.match(password)) {
        Dialog.alert(tr(VikaResource.Password_must_be));
        return;
      }

      // Do not allow empty phone number
      if (phone.length() == 0) {
        Dialog.alert(tr(VikaResource.Registration_error));
        return;
      }

      confirmRegister(code, password, phone);
    }
  }

  public void focusChanged(Field field, int eventType) {
    if (eventType == FocusChangeListener.FOCUS_GAINED) {
      if (field instanceof EditTextField) {
        if (field == confirmationCode) {
          ((EditTextField) field).setBitmaps(null, null);
        }
      } else if (field instanceof CustomPasswordEditField) {
        if (field == password) {
          ((CustomPasswordEditField) field).setBitmaps(null, null);
        }
      }
    } else if (eventType == FocusChangeListener.FOCUS_LOST) {
      if (field instanceof EditTextField) {
        final EditTextField editText = (EditTextField) field;
        final String text = editText.getText();

        if (text.length() > 0) {
          if (editText == confirmationCode) {
            final boolean correctCode = CONFIRMATION_CODE_RE.match(text);
            editText.setBitmaps(null, correctCode ? REGISTER_VK : REGISTER_ERROR);
          }
        }
      } else if (field instanceof CustomPasswordEditField) {
        final CustomPasswordEditField t = (CustomPasswordEditField) field;
        final String text = t.getText();

        if (text.length() > 0) {
          if (t == password) {
            final boolean correctPassword = PASSWORD_RE.match(text);
            t.setBitmaps(null, correctPassword ? REGISTER_VK : REGISTER_ERROR);
          }
        }
      }
    }
  }

  private void initGui() {
    boolean isTall = Display.isTall();

    final VerticalCenterFieldManager root = new VerticalCenterFieldManager(
      Field.USE_ALL_HEIGHT | Field.USE_ALL_WIDTH | Manager.VERTICAL_SCROLL);
    root.setBackground(DARK_BG);
    root.setPadding(DP1, 0, DP1, 0);

    final BitmapField logo = new FocusableBitmapField(REGISTER_VK, Field.FIELD_HCENTER);
    logo.setMargin(0, 0, DP4, 0);

    final Theme labelTheme = new Theme();
    labelTheme.setPrimaryColor(0xFFFFFF);
    labelTheme.setPaddingEdges(isTall ?
      new XYEdges(DP5, DP5, DP5, DP5) : new XYEdges(DP2, DP10, DP3, DP10));

    final String inputCode = tr(VikaResource.Input_confirmation_code);
    CustomLabelField label = new CustomLabelField(inputCode, Field.FIELD_HCENTER
      | DrawStyle.HCENTER,
      labelTheme);

    confirmationCode = new VkEditTextField(TextField.NO_NEWLINE);
    confirmationCode.setFocusListener(ConfirmationCodeScreen.this);
    confirmationCode.setMargin(-DP1, DP10, -DP1, DP10);
    confirmationCode.setHint(tr(VikaResource.Confirmation_code));

    password = new VkPasswordEditField();
    password.setFocusListener(ConfirmationCodeScreen.this);
    password.setHint(tr(VikaResource.Password));
    password.setMargin(-DP1, DP10, -DP1, DP10);

    final String finishRegistration = tr(VikaResource.Finish_registration);
    register = new VkButtonField(finishRegistration, Field.FIELD_HCENTER);
    register.setChangeListener(ConfirmationCodeScreen.this);
    register.setFont(Fonts.bold(DP6 + DP1 / 2, Ui.UNITS_px));

    root.add(logo);
    root.add(label);
    root.add(confirmationCode);
    root.add(password);
    root.add(new VerticalSpacerField(isTall ? DP5 : DP2));
    root.add(register);
    root.add(new VerticalSpacerField(isTall ? DP5 : DP2));

    final VerticalFieldManager scroller = new VerticalFieldManager(Manager.VERTICAL_SCROLL);
    scroller.add(root);
    ConfirmationCodeScreen.this.add(scroller);
  }

  private void confirmRegister(final String code, final String password, final String phone) {
    new APIHelper() {

      public void after(Object obj) {
        Dialog.alert(tr(VikaResource.Registration_successful));
        clearSidData();
        close();
      }

      public void error(int error) {
        if (error == ErrorCodes.PARAMETER_MISSING_OR_INVALID) {
          Dialog.alert(tr(VikaResource.Wrong_confirmation_code));
        } else if (error == ErrorCodes.NETWORK_ERROR || error == ErrorCodes.NO_NETWORK) {
          Dialog.alert(tr(VikaResource.Network_is_not_available));
        }
      }

      public Object task() throws APIException {
        AuthAPI.instance.confirm(captcha(), phone, code, password);
        return null;
      }
    }.start();
  }

  public boolean isDirty() {
    return false;
  }

  private void clearSidData() {
    OptionsStorage.instance.delete("register.phone");
    OptionsStorage.instance.delete("register.firstName");
    OptionsStorage.instance.delete("register.lastName");
    OptionsStorage.instance.delete("register.sid");
    OptionsStorage.instance.delete("register.timestamp");
  }

}
