package org.yanex.vika.gui.screen;

import net.rim.device.api.ui.Field;
import org.yanex.vika.gui.util.GuiItem;
import org.yanex.vika.local.Local;

public abstract class ScreenGui implements GuiItem {

  protected static final long
      FIELD_BOTTOM = Field.FIELD_BOTTOM,
      FIELD_HCENTER = Field.FIELD_HCENTER,
      FIELD_LEFT = Field.FIELD_LEFT,
      FIELD_RIGHT = Field.FIELD_RIGHT,
      FIELD_TOP = Field.FIELD_TOP,
      FIELD_VCENTER = Field.FIELD_VCENTER,
      USE_ALL_HEIGHT = Field.USE_ALL_HEIGHT,
      USE_ALL_WIDTH = Field.USE_ALL_WIDTH;

  public static String tr(int key) {
    return Local.tr(key);
  }

}
