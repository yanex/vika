package org.yanex.vika.gui.list.item;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import org.yanex.vika.gui.util.Theme;
import org.yanex.vika.gui.widget.base.FocusableField;
import org.yanex.vika.local.Local;

public abstract class ListItem extends FocusableField implements AbstractListItem {

    private ItemPaintListener itemPaintListener;
    private ItemListener itemListener;

    public static String tr(int key) {
        return Local.tr(key);
    }

    public ListItem(long style, Theme theme) {
        super(style, theme);
    }

    public ListItem(Theme theme) {
        super(Field.USE_ALL_WIDTH, theme);
    }

    protected void fieldChangeNotify(int context) {
        super.fieldChangeNotify(context);
        if (itemListener != null) {
            itemListener.itemClick(getId(), this);
        }
    }

    public ItemListener getItemListener() {
        return itemListener;
    }

    public ItemPaintListener getItemPaintListener() {
        return itemPaintListener;
    }

    protected void paint(Graphics g) {
        paint(g, new XYRect(0, 0, getContentWidth(), getContentHeight()));
        if (itemPaintListener != null) {
            itemPaintListener.onPaint();
        }
    }

    protected abstract void paint(Graphics g, XYRect rect);

    public void setItemListener(ItemListener itemListener) {
        this.itemListener = itemListener;
    }

    public void setItemPaintListener(ItemPaintListener itemPaintListener) {
        this.itemPaintListener = itemPaintListener;
    }
}
