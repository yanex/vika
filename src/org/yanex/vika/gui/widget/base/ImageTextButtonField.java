package org.yanex.vika.gui.widget.base;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYEdges;
import org.yanex.vika.gui.util.TextDrawHelper;
import org.yanex.vika.gui.util.Theme;

public class ImageTextButtonField extends FocusableField {

    private static final int INTERVAL = DP1;

    private String text;
    private Bitmap bmp;

    private int fixedWidth = -1, fixedHeight = -1;

    public ImageTextButtonField(String text, Bitmap bmp, long style, Theme theme) {
        super(style, theme);

        this.text = text;
        this.bmp = bmp;
    }

    public ImageTextButtonField(String text, Bitmap bmp, long style, Theme theme, int fixedWidth,
                                int fixedHeight) {
        super(style, theme);

        this.text = text;
        this.bmp = bmp;
        this.fixedHeight = fixedHeight;
        this.fixedWidth = fixedWidth;
    }

    public int getFixedHeight() {
        return fixedHeight;
    }

    public int getFixedWidth() {
        return fixedWidth;
    }

    public int getPreferredHeight() {
        if (fixedHeight > 0) {
            return fixedHeight;
        }

        XYEdges b = getTheme().getBorderEdges();

        return b.top + Math.max(getFont().getHeight(), bmp.getHeight()) + b.bottom;
    }

    public int getPreferredWidth() {
        XYEdges b = getTheme().getBorderEdges();

        if (isStyle(Field.USE_ALL_WIDTH)) {
            return Integer.MAX_VALUE;
        } else {
            return fixedWidth + b.left + bmp.getWidth() + ImageTextButtonField.INTERVAL
                    + getFont().getBounds(text) + b.right;
        }
    }

    protected void paint(Graphics g) {
        int oldColor = g.getColor();
        try {
            int maxWidth = getContentWidth() - bmp.getWidth() - ImageTextButtonField.INTERVAL;
            String cutted = TextDrawHelper.calcEllipsize(text, g, maxWidth);

            int y = (getContentHeight() - bmp.getHeight()) / 2;
            int x = (getContentWidth() - bmp.getWidth() - ImageTextButtonField.INTERVAL - getFont()
                    .getAdvance(cutted)) / 2;

            g.drawBitmap(x, y, bmp.getWidth(), bmp.getHeight(), bmp, 0, 0);

            if (isFocused() || isActive()) {
                g.setColor(getTheme().getSecondaryFontColor());
            } else {
                g.setColor(getTheme().getPrimaryColor());
            }

            if (g.getFont().getAdvance(text) > maxWidth) {
                TextDrawHelper.drawEllipsizedString(text, g,
                        x + bmp.getWidth() + ImageTextButtonField.INTERVAL,
                        (getContentHeight() - g.getFont().getHeight()) / 2, maxWidth);
            } else {
                g.drawText(text, x + bmp.getWidth() + ImageTextButtonField.INTERVAL,
                        getContentHeight() / 2,
                        DrawStyle.VCENTER, getContentWidth());
            }
        } finally {
            g.setColor(oldColor);
        }
    }

    public void setFixedHeight(int fixedHeight) {
        this.fixedHeight = fixedHeight;
    }

    public void setFixedWidth(int fixedWidth) {
        this.fixedWidth = fixedWidth;
    }

}
