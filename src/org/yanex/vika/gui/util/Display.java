package org.yanex.vika.gui.util;

public class Display {

  private static volatile int h = -1, w = -1;

  public static int getMaxDimention() {
    return Math.max(getHeight(), getWidth());
  }

  public static int getMinDimention() {
    return Math.min(getHeight(), getWidth());
  }

  public static int getHeight() {
    if (h == -1) {
      h = net.rim.device.api.system.Display.getHeight();
    }
    return h;
  }

  public static int getWidth() {
    if (w == -1) {
      w = net.rim.device.api.system.Display.getWidth();
    }
    return w;
  }

  public static boolean isTall() {
    return getHeight() > getWidth();
  }
}
