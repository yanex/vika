package org.yanex.vika.gui.util;

import net.rim.device.api.ui.Ui;

final class GuiUtils {

  static int px(int pt) {
    return Ui.convertSize(pt, Ui.UNITS_pt, Ui.UNITS_px);
  }

}
