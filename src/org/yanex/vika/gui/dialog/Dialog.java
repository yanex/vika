package org.yanex.vika.gui.dialog;

import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import org.yanex.vika.gui.util.GuiItem;
import org.yanex.vika.local.Local;

public abstract class Dialog extends Screen implements GuiItem {

  private static final char CODE_BACK = 27;

  public Dialog(Manager delegate) {
    super(delegate);
  }

  public static String tr(int key) {
    return Local.tr(key);
  }

  protected void dismiss() {
    if (isVisible()) {
      this.close();
    }
  }

  protected boolean keyChar(char c, int status, int time) {
    if (c == CODE_BACK) {
      dismiss();
      return true;
    } else return super.keyChar(c, status, time);
  }

  public void show() {
    if (!isVisible()) {
      UiApplication.getUiApplication().pushModalScreen(this);
    }
  }

}
