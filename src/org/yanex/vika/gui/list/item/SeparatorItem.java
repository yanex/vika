package org.yanex.vika.gui.list.item;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.decor.Background;
import org.yanex.vika.gui.util.Fonts;
import org.yanex.vika.gui.util.GradientBackground;
import org.yanex.vika.gui.util.Theme;

public class SeparatorItem extends ListItem {

    private static final Font FONT = Fonts.bold(DP6, Ui.UNITS_px);

    private static final Background BACKGROUND_FOCUS =
            new GradientBackground(0xe5e5e5, 0xd5d5d5, 1, 0xd0d0d0, 1, 0xc6c6c6);

    private static final Theme THEME = new Theme()
            .setPrimaryColor(0x555555)
            .setSecondaryFontColor(0xFFFFFF)
            .setPaddingEdges(DP2, DP2, DP1, DP2)
            .setBackground(BACKGROUND_FOCUS, BACKGROUND_FOCUS, BACKGROUND_FOCUS, null);

    private final String text;

    public SeparatorItem(String text) {
        super(SeparatorItem.THEME);
        this.text = text;
    }

    public boolean filter(String filter) {
        if (text.length() < 2) {
            return filter == null || filter.length() == 0;
        } else {
            return filter == null || filter.length() == 0 || text.indexOf(filter) >= 0;
        }
    }

    public int getPreferredHeight() {
        return DP7;
    }

    public int getPreferredWidth() {
        return Integer.MAX_VALUE;
    }

    public boolean isFocusable() {
        return false;
    }

    protected void paint(Graphics g, XYRect rect) {
        int y = (getContentHeight() - FONT.getHeight()) / 2;
        g.setFont(FONT);
        g.setColor(SeparatorItem.THEME.getPrimaryColor());
        g.drawText(text, DP1, y);
    }

}
