package org.yanex.vika.gui.widget;

import org.yanex.vika.gui.util.Theme;
import org.yanex.vika.gui.widget.base.CustomLabelField;

public class VkLabelField extends CustomLabelField {

    private static Theme THEME = new Theme().setPrimaryColor(0x000000);

    public VkLabelField(Object text, long style) {
        super(text, style, VkLabelField.THEME);
    }

}
