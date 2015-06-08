package org.yanex.vika.gui.widget;

import com.mobiata.bb.ui.decor.NinePatchBackground;
import net.rim.device.api.ui.decor.Background;
import org.yanex.vika.gui.util.Files;
import org.yanex.vika.gui.util.GuiItem;
import org.yanex.vika.gui.util.Theme;
import org.yanex.vika.gui.widget.base.ButtonField;

public class VkButtonField extends ButtonField implements GuiItem {

    private static final Background BACKGROUND =
            new NinePatchBackground(Files.DARK_BUTTON);
    private static final Background BACKGROUND_FOCUS =
            new NinePatchBackground(Files.DARK_BUTTON_FOCUS);
    private static final Background BACKGROUND_ACTIVE =
            new NinePatchBackground(Files.DARK_BUTTON_FOCUS_PUSHED);

    private static final Theme THEME = new Theme()
            .setPrimaryColor(0x000000)
            .setSecondaryFontColor(0xFFFFFF)
            .setPaddingEdges(DP1 * 2 / 3, DP1, DP1 * 2 / 3, DP1)
            .setBackground(BACKGROUND, BACKGROUND_FOCUS, BACKGROUND_ACTIVE, null);

    public VkButtonField(String label, long style) {
        super(label, style, VkButtonField.THEME);
    }

}
