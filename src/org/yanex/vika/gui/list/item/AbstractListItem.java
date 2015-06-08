package org.yanex.vika.gui.list.item;

import org.yanex.vika.gui.util.GuiItem;

public interface AbstractListItem extends GuiItem {

    interface ItemListener {
        void itemClick(int id, AbstractListItem item);

        void specialPaint(int id, AbstractListItem item);
    }

    interface ItemPaintListener {
        void onPaint();
    }

    boolean filter(String filter);

    void setId(int id);

    int getId();

    ItemListener getItemListener();

    ItemPaintListener getItemPaintListener();

    void setItemListener(ItemListener itemListener);

    void setItemPaintListener(ItemPaintListener itemPaintListener);

}
