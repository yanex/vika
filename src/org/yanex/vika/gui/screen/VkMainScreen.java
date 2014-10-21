package org.yanex.vika.gui.screen;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.MainScreen;
import org.yanex.vika.gui.util.GuiItem;
import org.yanex.vika.local.Local;
import org.yanex.vika.util.media.AudioPlayer;

public abstract class VkMainScreen extends MainScreen implements GuiItem, FieldChangeListener {

  private Field banner = null, title = null;

  public VkMainScreen() {
    super();
  }

  public VkMainScreen(long style) {
    super(style);
  }

  public static String tr(int key) {
    return Local.tr(key);
  }

  public Field getBanner() {
    return banner;
  }

  public Field getTitle() {
    return title;
  }

  public void fieldChanged(Field field, int context) {
  }

  protected boolean keyControl(char c, int status, int time) {
    if (c == Characters.CONTROL_VOLUME_UP) {
      AudioPlayer.instance.incVolume();
      return true;
    } else if (c == Characters.CONTROL_VOLUME_DOWN) {
      AudioPlayer.instance.decVolume();
      return true;
    } else {
      return super.keyControl(c, status, time);
    }
  }

  public boolean isDirty() {
    return false;
  }

  public void scrollLayout(int direction) {
    scroll(direction);
  }

  public void setBanner(Field banner) {
    super.setBanner(banner);
    this.banner = banner;
  }

  public void setTitle(Field title) {
    super.setTitle(title);
    this.title = title;
  }

  public void setTitle(ResourceBundleFamily family, int id) {
    super.setTitle(family, id);
    this.title = null;
  }

  public void setTitle(String title) {
    super.setTitle(title);
    this.title = null;
  }

  public VkMainScreen show() {
    UiApplication.getUiApplication().pushScreen(this);
    return this;
  }
}
