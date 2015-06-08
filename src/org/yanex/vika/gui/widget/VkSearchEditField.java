package org.yanex.vika.gui.widget;

import com.mobiata.bb.ui.decor.NinePatchBackground;
import net.rim.device.api.system.Bitmap;
import org.yanex.vika.gui.util.*;
import org.yanex.vika.gui.widget.base.EditTextField;

public class VkSearchEditField extends EditTextField implements GuiItem {

    private static final NinePatchBackground BACKGROUND = new NinePatchBackground(
            Files.LISTS_CONTACTS_SEARCHLINEINPUT);
    private static Bitmap SEARCH = R.instance
            .getBitmap("Lists/Contacts/SearchLineIcon.png");

    private static final int PADDING = DP2 * 3 / 4;
    private static final Theme THEME = new Theme()
            .setPrimaryColor(0x000000)
            .setSecondaryFontColor(0x666666)
            .setBackground(BACKGROUND, null, null, null)
            .setBorderEdges(PADDING, PADDING, PADDING, PADDING)
            .setPaddingEdges(BACKGROUND.getNinePatch().getPadding());

    public VkSearchEditField(long style) {
        super(style, VkSearchEditField.THEME);
        setFont(Fonts.narrow(7));
        setBitmaps(VkSearchEditField.SEARCH, null);
    }

}
