package org.yanex.vika.gui.util;

import net.rim.device.api.system.Bitmap;

public class RoundAngles {

  private static final int E3 = 256 * 256 * 256;
  private static final int E2 = 256 * 256;
  private static final int E1 = 256;

  private static int[][][] maps = {{}, {},
      { // 2
          {0x5f, 0xe1},
          {0xe1, 0xff}
      },
      { // 3
          {0x01, 0x94, 0xec},
          {0x94, 0xff, 0xff},
          {0xec, 0xff, 0xff}},
      { // 4
          {0x01, 0x2f, 0xaf, 0xf1},
          {0x2f, 0xef, 0xff, 0xff},
          {0xaf, 0xff, 0xff, 0xff},
          {0xf1, 0xff, 0xff, 0xff}
      }, { // 5
      {0x01, 0x01, 0x59, 0xbf, 0xf4},
      {0x01, 0x8c, 0xff, 0xff, 0xff},
      {0x59, 0xff, 0xff, 0xff, 0xff},
      {0xbf, 0xff, 0xff, 0xff, 0xff},
      {0xf4, 0xff, 0xff, 0xff, 0xff}
  }, { // 6
      {0x01, 0x01, 0x01, 0x74, 0xca, 0xf5},
      {0x01, 0x1f, 0xc9, 0xff, 0xff, 0xff},
      {0x01, 0xc9, 0xff, 0xff, 0xff, 0xff},
      {0x74, 0xff, 0xff, 0xff, 0xff, 0xff},
      {0xca, 0xff, 0xff, 0xff, 0xff, 0xff},
      {0xf5, 0xff, 0xff, 0xff, 0xff, 0xff}
  }, { // 7
      {0x01, 0x01, 0x01, 0x1b, 0x88, 0xd1, 0xf7},
      {0x01, 0x01, 0x64, 0xff, 0xff, 0xff, 0xff},
      {0x01, 0x64, 0xff, 0xff, 0xff, 0xff, 0xff},
      {0x1b, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff},
      {0x88, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff},
      {0xd1, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff},
      {0xf7, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff}
  }
  };

  /*
   * m0 m1 m2 m3
   */
  private static void drawPixels(Bitmap src, int[][] m, int destX, int destY) {
    int[] data = new int[m.length * m.length];
    src.getARGB(data, 0, m.length, destX, destY, m.length, m.length);

    for (int y = destY; y < destY + m.length; ++y) {
      for (int x = destX; x < destX + m[y - destY].length; ++x) {
        int pos = m.length * (y - destY) + x - destX;
        int b = data[pos] % RoundAngles.E1;
        int g = (data[pos] - b) / 256 % RoundAngles.E1;
        int r = (data[pos] - b - RoundAngles.E1 * g) / RoundAngles.E2 % RoundAngles.E1;
        data[pos] = m[y - destY][x - destX] * RoundAngles.E3 + b + g * RoundAngles.E1 + r
            * RoundAngles.E2;
      }
    }

    src.setARGB(data, 0, m.length, destX, destY, m.length, m.length);
  }

  public static void roundAngles(Bitmap src, int radius) {
    if (radius < 2) {
      radius = 2;
    }
    if (radius > maps.length) {
      radius = maps.length;
    }

    int[][] m0 = RoundAngles.maps[radius];
    int[][] m1 = RoundAngles.swapHorizontal(RoundAngles.maps[radius]);
    int[][] m2 = RoundAngles.swapVertical(RoundAngles.maps[radius]);
    int[][] m3 = RoundAngles.swapHorizontal(m2);

    RoundAngles.drawPixels(src, m0, 0, 0);
    RoundAngles.drawPixels(src, m1, src.getWidth() - radius, 0);
    RoundAngles.drawPixels(src, m2, 0, src.getHeight() - radius);
    RoundAngles.drawPixels(src, m3, src.getWidth() - radius, src.getHeight() - radius);
  }

  private static int[][] swapHorizontal(int[][] src) {
    int[][] m = new int[src.length][];
    for (int i = 0; i < m.length; ++i) {
      m[i] = new int[src[i].length];
      for (int j = 0; j < src[i].length; ++j) {
        m[i][j] = src[i][src[i].length - j - 1];
      }
    }
    return m;
  }

  private static int[][] swapVertical(int[][] src) {
    int[][] m = new int[src.length][];
    for (int i = 0; i < m.length; ++i) {
      m[i] = new int[src[i].length];
      for (int j = 0; j < src[i].length; ++j) {
        m[i][j] = src[m.length - i - 1][j];
      }
    }
    return m;
  }
}
