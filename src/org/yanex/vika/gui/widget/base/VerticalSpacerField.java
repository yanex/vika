package org.yanex.vika.gui.widget.base;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;

public class VerticalSpacerField extends Field {

    private final int height;

    public VerticalSpacerField(int height) {
        this.height = height;
    }

    public int getPreferredHeight() {
        return height;
    }

    public int getPreferredWidth() {
        return isStyle(Field.USE_ALL_WIDTH) ? Integer.MAX_VALUE : 1;
    }

    protected void layout(int width, int height) {
        int w = Math.min(width, getPreferredWidth());
        int h = Math.min(height, getPreferredHeight());
        setExtent(w, h);
    }

    protected void paint(Graphics g) {
        // empty
    }

    protected void paintBackground(Graphics g) {
        // empty
    }

}
