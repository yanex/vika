package org.yanex.vika.gui.screen;

import com.mobiata.bb.ui.decor.NinePatchBackground;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;
import org.yanex.vika.LoginScreen;
import org.yanex.vika.gui.util.*;
import org.yanex.vika.gui.widget.VkButtonField;
import org.yanex.vika.gui.widget.VkEditTextField;
import org.yanex.vika.gui.widget.VkPasswordEditField;
import org.yanex.vika.gui.widget.base.*;
import org.yanex.vika.gui.widget.manager.VerticalCenterFieldManager;
import org.yanex.vika.local.VikaResource;

public class LoginScreenGui extends ScreenGui implements FieldChangeListener {

  private static final Background BACKGROUND =
    BackgroundFactory.createBitmapBackground(R.instance.getBitmap(Files.DARK_BG));

  private final LoginScreen screen;

  private final EditTextField username;
  private final CustomPasswordEditField password;
  private final ButtonField login;
  private final ButtonField register;

  public LoginScreenGui(LoginScreen screen) {
    this.screen = screen;

    VerticalCenterFieldManager root = new VerticalCenterFieldManager(Field.USE_ALL_HEIGHT | Field.USE_ALL_WIDTH);
    root.setBackground(BACKGROUND);
    root.setPadding(DP1, 0, DP1, 0);

    BitmapField logo = new FocusableBitmapField(R.instance.getBitmap(Files.REGISTERFORM_VK), Field.FIELD_HCENTER);
    logo.setMargin(0, 0, DP4, 0);

    username = new VkEditTextField(TextField.NO_NEWLINE) {

      protected boolean keyChar(char key, int status, int time) {
        if (key == ' ') {
          return super.keyChar('.', status, time);
        } else {
          return super.keyChar(key, status, time);
        }
      }

    };
    username.setMargin(-DP1, DP10, -DP1, DP10);
    username.setHint(tr(VikaResource.Phone_number_or_email));

    password = new VkPasswordEditField();
    password.setHint(tr(VikaResource.Password));
    password.setMargin(-DP1, DP10, -DP1, DP10);

    login = new VkButtonField(tr(VikaResource.Login), Field.FIELD_HCENTER);
    login.setChangeListener(this);
    login.setPadding(0, DP2, 0, DP2);
    login.setFont(Fonts.bold(DP6 + DP1 / 2, Ui.UNITS_px));

    Theme registerButtonTheme = new Theme();

    registerButtonTheme.setPrimaryColor(0xEEEEEE);
    registerButtonTheme.setSecondaryFontColor(0xFFFFFF);

    Background focusBackground = new NinePatchBackground(Files.DARK_BUTTON_FOCUS);
    Background activeBackground = new NinePatchBackground(Files.DARK_BUTTON_FOCUS_PUSHED);

    registerButtonTheme.setBackground(null, focusBackground, activeBackground, null);
    registerButtonTheme.setPaddingEdges(0, DP1, 0, DP1);

    register = new ButtonField(tr(VikaResource.Signup), Field.FIELD_HCENTER,
      registerButtonTheme);
    register.setChangeListener(this);
    register.setFont(Fonts.bold(DP6 + DP1 / 2, Ui.UNITS_px).derive(
      Font.UNDERLINED));
    register.setMargin(DP1, 0, 0, 0);

    boolean isTall = Display.isTall();

    root.add(logo);
    root.add(username);
    root.add(password);
    root.add(new VerticalSpacerField(isTall ? DP5 : DP2));
    root.add(login);
    root.add(register);
    root.add(new VerticalSpacerField(isTall ? DP5 : DP2));

    VerticalFieldManager vfm = new VerticalFieldManager(Manager.VERTICAL_SCROLL);
    vfm.add(root);
    screen.add(vfm);
  }

  public void fieldChanged(Field field, int context) {
    if (field == register) {
      screen._register();
    } else if (field == login) {
      String login = this.username.getText();
      String password = this.password.getText();
      screen._login(login, password);
    }
  }
}
