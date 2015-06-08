package org.yanex.vika.gui.list.item;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.decor.Background;
import org.yanex.vika.gui.util.GradientBackground;
import org.yanex.vika.gui.util.Theme;
import org.yanex.vika.gui.widget.manager.FocusableHFM;
import org.yanex.vika.local.Local;

public class ComplexListItem extends FocusableHFM implements AbstractListItem {

    private static final Background BACKGROUND_FOCUS =
            new GradientBackground(0x59a0e8, 0x1c65be);

    private static Theme THEME = new Theme()
            .setPrimaryColor(0x000000)
            .setSecondaryFontColor(0xFFFFFF)
            .setBackground(null, BACKGROUND_FOCUS, BACKGROUND_FOCUS, null);

    private int id;
    private ItemPaintListener itemPaintListener;
    private ItemListener itemListener;

    public ComplexListItem() {
        super(Field.USE_ALL_WIDTH | Field.FOCUSABLE, ComplexListItem.THEME);
    }

    public void fieldChanged(Field f, int i) {
        if (itemListener != null) {
            itemListener.itemClick(id, this);
        }
    }

    public boolean filter(String filter) {
        return true;
    }

    public int getId() {
        return id;
    }

    public ItemListener getItemListener() {
        return itemListener;
    }

    public ItemPaintListener getItemPaintListener() {
        return itemPaintListener;
    }

    public boolean isFocusable() {
        return true;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setItemListener(ItemListener itemListener) {
        this.itemListener = itemListener;
    }

    public void setItemPaintListener(ItemPaintListener itemPaintListener) {
        this.itemPaintListener = itemPaintListener;
    }

    public static String tr(int key) {
        return Local.tr(key);
    }
}
