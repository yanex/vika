package org.yanex.vika.gui.list.item;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.decor.Background;
import org.yanex.vika.api.item.Audio;
import org.yanex.vika.gui.util.*;

public class SongItem extends ListItem implements GuiItem {

    private static final Background BACKGROUND_FOCUS = new GradientBackground(0x59a0e8, 0x1c65be);

    private static final Theme THEME = new Theme()
            .setPrimaryColor(0x000000)
            .setSecondaryFontColor(0xFFFFFF)
            .setPaddingEdges(DP2, DP2, DP2, DP2)
            .setBackground(null, BACKGROUND_FOCUS, BACKGROUND_FOCUS, null);

    private static final Font FONT = Fonts.defaultFont;

    public final Audio audio;
    private final String text;

    public SongItem(Audio audio) {
        super(SongItem.THEME);
        this.audio = audio;
        this.text = audio.getArtist() + " - " + audio.getTitle();
    }

    public boolean filter(String filter) {
        return filter == null || filter.length() == 0;
    }

    public int getPreferredHeight() {
        return DP2 + Fonts.defaultBold.getHeight();
    }

    public int getPreferredWidth() {
        return Integer.MAX_VALUE;
    }

    protected void paint(Graphics g, XYRect rect) {
        int oldColor = g.getColor(), dy = (rect.height - FONT.getHeight()) / 2, color = (isActive() || isFocused()) ?
                getTheme().getSecondaryFontColor()
                : getTheme().getPrimaryColor();
        try {
            g.setFont(FONT);
            g.setColor(color);
            TextDrawHelper.drawEllipsizedString(text, g, rect.x, rect.y + dy, rect.width);
        } finally {
            g.setColor(oldColor);
        }
    }

}
