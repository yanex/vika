package org.yanex.vika.gui.util;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;

public class TextDrawHelper {

    private static final String ELLIPSIZE = "...";

    public static void drawEllipsizedString(String text, Graphics g, int x, int y, int maxWidth) {
        Font f = g.getFont();
        String t = text;
        if (f.getAdvance(t) <= maxWidth) {
            g.drawText(text, x, y);
            return;
        }
        while (f.getAdvance(t + ELLIPSIZE) > maxWidth) {
            t = t.substring(0, t.length() - 1);
        }
        g.drawText(t + ELLIPSIZE, x, y);
    }

    public static String calcEllipsize(String text, Graphics g, int maxWidth) {
        String t = text;
        Font f = g.getFont();
        if (f.getAdvance(t) <= maxWidth) {
            return t;
        }
        while (f.getAdvance(t + ELLIPSIZE) > maxWidth) {
            t = t.substring(0, t.length() - 1);
        }
        return t + ELLIPSIZE;
    }

    public static String calcTrim(String text, Font f, int maxWidth) {
        String t = text;
        if (f.getAdvance(t) <= maxWidth) {
            return t;
        }
        while (f.getAdvance(t) > maxWidth) {
            t = t.substring(0, t.length() - 1);
        }
        return t;
    }

    public static void drawAlphaGradientString(
            String text, Graphics g, int x, int y, boolean toRight) {
        int oldAlpha = g.getGlobalAlpha();
        try {
            int dx = 0, currentAlpha = toRight ? 255 : 55;
            int step = (toRight ? -1 : 1) * 200 / text.length();
            for (int i = 0; i < text.length(); ++i) {
                char c = text.charAt(i);
                g.setGlobalAlpha(currentAlpha);
                g.drawText("" + c, x + dx, y);
                dx += g.getFont().getAdvance(c);
                currentAlpha += step;
            }
        } finally {
            g.setGlobalAlpha(oldAlpha);
        }
    }

}
