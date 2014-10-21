package org.yanex.vika.gui.dialog;

import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import org.yanex.vika.gui.util.GuiItem;
import org.yanex.vika.local.Local;

public abstract class VkScreen extends Screen implements GuiItem {

  public VkScreen(Manager manager) {
    super(manager);
  }

  public VkScreen(Manager manager, long l) {
    super(manager, l);
  }

  public static String tr(int key) {
    return Local.tr(key);
  }

}
