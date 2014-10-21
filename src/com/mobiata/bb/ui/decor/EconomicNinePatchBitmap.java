package com.mobiata.bb.ui.decor;

import net.rim.device.api.system.Bitmap;

public class EconomicNinePatchBitmap extends NinePatchBitmap {

  public EconomicNinePatchBitmap(Bitmap bmp, int fromColor, int toColor) {
    super(bmp);
    init(fromColor, toColor);
  }

  public EconomicNinePatchBitmap(Bitmap bmp, int options, int fromColor, int toColor) {
    super(bmp, options);
    init(fromColor, toColor);
  }

  private void init(int fromColor, int toColor) {
    transparentMiddle = false;
    filter = Bitmap.FILTER_BOX;
    if (fromColor != Integer.MIN_VALUE && toColor != Integer.MIN_VALUE) {
      centerGradient = true;
      gradientFromColor = fromColor;
      gradientToColor = toColor;
    }
  }

}
