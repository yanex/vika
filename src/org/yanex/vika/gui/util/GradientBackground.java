package org.yanex.vika.gui.util;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.decor.Background;

public class GradientBackground extends Background {

    private int startColor, endColor;
    private int topBorderHeight = 0, topBorderColor = 0;
    private int bottomBorderHeight = 0, bottomBorderColor = 0;

    public GradientBackground(int startColor, int endColor) {
        this.startColor = startColor;
        this.endColor = endColor;
    }

    public GradientBackground(int startColor, int endColor, int topBorderHeight,
                              int topBorderColor, int bottomBorderHeight, int bottomBorderColor) {
        this(startColor, endColor);
        setTopBorder(topBorderHeight, topBorderColor);
        setBottomBorder(bottomBorderHeight, bottomBorderColor);
    }

    public void draw(Graphics g, XYRect rect) {
        int[] path = {startColor, startColor, endColor, endColor};

        int[] xes = {0x0, rect.width, rect.width, 0};
        int[] yes = {0x0, 0x0, rect.height, rect.height};

        g.translate(rect.x, rect.y);
        g.drawShadedFilledPath(xes, yes, null, path, null);
        g.translate(-rect.x, -rect.y);

        int oldColor = g.getColor();

        if (topBorderHeight > 0) {
            g.setColor(topBorderColor);
            g.fillRect(rect.x, rect.y, rect.width, topBorderHeight);
        }

        if (bottomBorderHeight > 0) {
            g.setColor(bottomBorderColor);
            g.fillRect(rect.x, rect.y - bottomBorderHeight, rect.width, bottomBorderHeight);
        }

        g.setColor(oldColor);
    }

    public boolean isTransparent() {
        return false;
    }

    private void setBottomBorder(int height, int color) {
        bottomBorderColor = color;
        bottomBorderHeight = height;
    }

    private void setTopBorder(int height, int color) {
        topBorderColor = color;
        topBorderHeight = height;
    }

}
