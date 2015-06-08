package org.yanex.vika.gui.list.item;

import org.yanex.vika.gui.util.GuiItem;

public interface AbstractListItem extends GuiItem {

    static interface ItemListener {
        public void itemClick(int id, AbstractListItem item);

        public void specialPaint(int id, AbstractListItem item);
    }

    static interface ItemPaintListener {
        public void onPaint();
    }

    public boolean filter(String filter);

    public void setId(int id);

    public int getId();

    public ItemListener getItemListener();

    public ItemPaintListener getItemPaintListener();

    public void setItemListener(ItemListener itemListener);

    public void setItemPaintListener(ItemPaintListener itemPaintListener);

}
