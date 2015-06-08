package org.yanex.vika.gui.widget;

import com.mobiata.bb.ui.decor.NinePatchBackground;
import net.rim.device.api.ui.decor.Background;
import org.yanex.vika.gui.util.Files;
import org.yanex.vika.gui.util.GuiItem;
import org.yanex.vika.gui.util.Theme;
import org.yanex.vika.gui.widget.base.EditTextField;

public class VkEditTextField extends EditTextField implements GuiItem {

    private static final Background BACKGROUND =
            new NinePatchBackground(Files.DARK_INPUT);
    private static final Background BACKGROUND_FOCUS =
            new NinePatchBackground(Files.DARK_INPUT_FOCUS);

    private static final Theme THEME = new Theme()
            .setPrimaryColor(0x000000)
            .setSecondaryFontColor(0x666666)
            .setBackground(BACKGROUND, BACKGROUND_FOCUS, null, null)
            .setPaddingEdges(DP7, DP7, DP7, DP7);

    public VkEditTextField(long style) {
        super(style, VkEditTextField.THEME);
    }

}
