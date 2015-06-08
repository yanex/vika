package org.yanex.vika.gui.widget.base;

import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYEdges;
import org.yanex.vika.gui.util.Theme;

public class ButtonField extends FocusableField {

    private String text;

    private int fixedWidth = -1;

    public ButtonField(String text, long style, Theme theme) {
        super(style, theme);

        this.text = text;
    }

    public int getPreferredHeight() {
        XYEdges b = getTheme().getBorderEdges();

        return b.top + getFont().getHeight() + b.bottom + getPaddingBottom() + getPaddingTop();
    }

    public int getPreferredWidth() {
        if (fixedWidth > 0) {
            return fixedWidth;
        }

        XYEdges b = getTheme().getBorderEdges();

        if (isStyle(Field.USE_ALL_WIDTH)) {
            return Integer.MAX_VALUE;
        } else {
            return b.left + getFont().getBounds(text) + b.right + getPaddingLeft() + getPaddingRight();
        }
    }

    public String getText() {
        return text;
    }

    protected void paint(Graphics g) {
        int oldColor = g.getColor();
        try {
            if (isFocused() || isActive()) {
                g.setColor(getTheme().getSecondaryFontColor());
            } else {
                g.setColor(getTheme().getPrimaryColor());
            }

            int dy = (getContentHeight() - g.getFont().getHeight()) / 2;
            int dx = (getContentWidth() - g.getFont().getAdvance(text)) / 2;
            g.drawText(text, dx, dy, DrawStyle.TOP | DrawStyle.LEFT, Integer.MAX_VALUE);
        } finally {
            g.setColor(oldColor);
        }
    }

    public void setFixedWidth(int width) {
        fixedWidth = width;
        updateLayout();
    }

    public void setText(String text) {
        this.text = text;
        updateLayout();
    }
}
