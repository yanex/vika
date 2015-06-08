package org.yanex.vika.gui.widget.base;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.decor.Background;
import org.yanex.vika.gui.util.GuiItem;
import org.yanex.vika.gui.util.Theme;

public class CustomLabelField extends LabelField implements GuiItem {
    protected static int px(int pt) {
        return Ui.convertSize(pt, Ui.UNITS_pt, Ui.UNITS_px);
    }

    private Theme theme = null;

    private XYRect borderRect = new XYRect();

    public CustomLabelField(Object text, long style, Theme theme) {
        super(text, style);
        this.theme = theme;
        setPadding(theme.getPaddingEdges());
    }

    public Theme getTheme() {
        return theme;
    }

    protected void layout(int width, int height) {
        super.layout(width, height);

        borderRect.set(0, 0, getWidth(), getHeight());
    }

    protected void paint(Graphics g) {
        int oldColor = g.getColor();

        if (theme != null) {
            g.setColor(theme.getPrimaryColor());
        } else {
            g.setColor(0);
        }

        super.paint(g);
        g.setColor(oldColor);
    }

    protected void paintBackground(Graphics g) {
        if (theme != null) {
            Background currentBackground = theme.getDefaultBackground();
            if (currentBackground != null) {
                currentBackground.draw(g, borderRect);
            }
        }
    }
}