package org.yanex.vika.gui.screen;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;
import org.yanex.vika.Configuration;
import org.yanex.vika.gui.util.Fonts;
import org.yanex.vika.gui.util.R;
import org.yanex.vika.gui.util.RoundedBackground;
import org.yanex.vika.gui.widget.VkCompactTitleField;
import org.yanex.vika.gui.widget.VkSettingsButtonField;
import org.yanex.vika.gui.widget.VkSettingsLabelField;
import org.yanex.vika.gui.widget.VkSettingsTitleField;
import org.yanex.vika.gui.widget.base.CustomLabelField;
import org.yanex.vika.local.VikaResource;
import org.yanex.vika.util.HappyDate;
import org.yanex.vika.util.fun.Action1;
import org.yanex.vika.util.fun.Array;

public class OptionsScreenGui extends ScreenGui {

  private static final Background LIGHT_BG =
      BackgroundFactory.createBitmapBackground(R.instance.getBitmap("LightBg.png"));

  private static final Background SettingsGroupBackground =
      new RoundedBackground(0xefefef, 6, true, 0xcccccc);

  private final VerticalFieldManager settingsList;

  private VerticalFieldManager silenceActive, silenceInactive;
  private CustomLabelField status;
  private VerticalFieldManager vfNotifications;
  public VkSettingsButtonField about, log, logout, exit,
      connectionType, enableNotifications,
      disableForHour, disableFor8Hours;

  public OptionsScreenGui(final VkMainScreen screen) {
    screen.setFont(Fonts.defaultFont);
    Field topTitle = new VkCompactTitleField(tr(VikaResource.Settings));
    screen.setBanner(topTitle);

    screen.getMainManager().setBackground(LIGHT_BG);

    settingsList = new VerticalFieldManager();
    settingsList.setBackground(LIGHT_BG);
    settingsList.setPadding(DP2, 0, 0, 0);

    initAccount();
    initConnection();
    initNotifications();
    initHelp();

    final Array activeFields = new Array(new Field[]{
        disableForHour, disableFor8Hours, enableNotifications,
        logout, exit, about, log, connectionType
    });

    activeFields.each(new Action1() {
      public void run(Object it) {
        if (it != null) {
          ((Field) it).setChangeListener(screen);
        }
      }
    });

    screen.add(settingsList);
  }

  private void initAccount() {
    Manager manager = category(VikaResource.Account);
    manager.add(logout = button(VikaResource.Logout));
    manager.add(exit = button(VikaResource.Exit));
  }

  private void initNotifications() {
    vfNotifications = category(VikaResource.Notifications);

    status = new VkSettingsLabelField(tr(VikaResource.You_can_search));
    status.setMargin(DP4, DP5, DP4, DP5);
    status.setFont(Fonts.narrow(7));
    vfNotifications.add(status);

    silenceActive = new VerticalFieldManager(Field.USE_ALL_WIDTH);
    silenceActive.add(enableNotifications = button(VikaResource.enabledn));

    silenceInactive = new VerticalFieldManager(Field.USE_ALL_WIDTH);
    silenceInactive.add(disableForHour = button(VikaResource.dn1));
    silenceInactive.add(disableFor8Hours = button(VikaResource.dn8));
  }

  private void initConnection() {
    Manager manager = category(VikaResource.Connection);
    manager.add(connectionType = button(VikaResource.Connection_type));
  }

  private void initHelp() {
    Manager manager = category(VikaResource.Help);
    manager.add(about = button(VikaResource.About));
    if (Configuration.DEBUG) {
      manager.add(log = button("Logs"));
    }
  }

  public void update(long silenceUntil) {
    vfNotifications.deleteAll();
    vfNotifications.add(status);

    if (silenceUntil <= System.currentTimeMillis()) {
      status.setText(tr(VikaResource.disoff));
      vfNotifications.add(silenceInactive);
    } else {
      HappyDate d = new HappyDate(silenceUntil);
      status.setText(tr(VikaResource.dison) + d.hour2() + ":" + d.minute2());
      vfNotifications.add(silenceActive);
    }
  }

  private VkSettingsButtonField button(String text) {
    return new VkSettingsButtonField(text);
  }

  private VkSettingsButtonField button(int textResource) {
    return button(tr(textResource));
  }

  private VerticalFieldManager category(int titleResource) {
    VerticalFieldManager outer = new VerticalFieldManager(Field.FIELD_HCENTER);
    outer.setBackground(SettingsGroupBackground);
    outer.setMargin(DP3, DP8, DP3, DP8);

    VerticalFieldManager inner = new VerticalFieldManager();
    inner.setPadding(DP2, DP2, DP2, DP2);
    outer.add(inner);

    CustomLabelField l = new VkSettingsTitleField(tr(titleResource));

    settingsList.add(l);
    settingsList.add(outer);

    return inner;
  }

}
