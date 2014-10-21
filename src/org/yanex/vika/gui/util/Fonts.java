package org.yanex.vika.gui.util;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Ui;

public class Fonts {

  public static final Font defaultFont = Fonts.narrow(Fonts.px(7), Ui.UNITS_px);
  public static final Font defaultBold = Fonts.bold(Fonts.px(7), Ui.UNITS_px);

  public static Font bold(int size) {
    return Fonts.bold(size, Ui.UNITS_pt);
  }

  public static Font bold(int size, int units) {
    try {
      FontFamily ff = FontFamily.forName("BBAlpha Sans Condensed");
      return ff.getFont(Font.BOLD, size, units);
    } catch (ClassNotFoundException e) {
      return Font.getDefault().derive(Font.BOLD, size, units);
    }

  }

  public static Font narrow(int size) {
    return Fonts.narrow(size, Ui.UNITS_pt);
  }

  public static Font narrow(int size, int units) {
    try {
      FontFamily ff = FontFamily.forName("BBAlpha Sans Condensed");
      return ff.getFont(Font.PLAIN, size, units);
    } catch (ClassNotFoundException e) {
      return Font.getDefault().derive(Font.PLAIN, size, units);
    }
  }

  private static int px(int pt) {
    return R.px(pt);
  }

}
