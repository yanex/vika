package org.yanex.vika.gui.list;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.VerticalFieldManager;
import org.yanex.vika.RootScreen;
import org.yanex.vika.gui.list.item.AbstractListItem;
import org.yanex.vika.gui.list.item.AbstractListItem.ItemListener;
import org.yanex.vika.gui.list.item.AbstractListItem.ItemPaintListener;
import org.yanex.vika.gui.list.item.ListItem;
import org.yanex.vika.gui.screen.VkMainScreen;
import org.yanex.vika.gui.widget.base.SeparatorField;

import java.util.Vector;

public class List extends VerticalFieldManager implements FieldChangeListener, ItemPaintListener, ItemListener {

    public interface ListListener {
        void itemClick(int id, AbstractListItem item);
        void loadNextPage(int already);
        void specialPaint(int id, AbstractListItem item);
    }

    public static final int MODE_NORMAL = 0;

    public static final int MODE_INVERT = 1;

    private int mode = List.MODE_NORMAL;

    private int ITEMS_PER_PAGE = Integer.MAX_VALUE; // 30;
    private int lastVecticalScrollPosition = 0;
    private int lastContentHeight = 0;
    private int lastSelectedField = -1;

    private int lastFieldsCount = 0;
    private ListFeedback feedback;
    private int separatorColor = 0xd8dde3;

    private int separatorHeight = 1;
    private Vector allItems = null;

    private String filter = "";
    private Vector displayingItems = new Vector();
    private Vector remainingItems = null;
    private boolean lastPage = false;
    private boolean nextPageLoading = false;

    private int lastNextPageCount = -1;

    private AbstractListItem listenPaintFrom = null;

    private int itemsCount = 0;

    private ListListener listener;

    private VkMainScreen owner;

    public List() {

    }

    public List(int mode) {
        this.mode = mode;
    }

    protected void addAll(Vector fields) {
        Field[] a = new Field[fields.size()];
        fields.copyInto(a);
        addAll(a);
    }

    public void appendItems(Vector items) {
        if (allItems == null) {
            return;
        }

        for (int i = 0; i < items.size(); ++i) {
            allItems.addElement(items.elementAt(i));
        }
        appendItems(items, -1);
    }

    protected void appendItems(Vector items, int _addCount) {
        int addCount = ITEMS_PER_PAGE, i;

        Vector willBeAdded = new Vector();

        if (itemsCount > 0 && items.size() > 0) {
            SeparatorField sep = new SeparatorField(separatorHeight, separatorColor);
            willBeAdded.addElement(sep);

            if (remainingItems != null && remainingItems.size() > 0) {
                addCount = 0;
            }
        }

        if (_addCount > 0) {
            addCount = _addCount;
        }

        if (mode == List.MODE_INVERT) {
            addCount = Integer.MAX_VALUE;
        }

        if (listenPaintFrom != null && addCount > 0) {
            listenPaintFrom.setItemPaintListener(null);
        }

        int s = Math.min(addCount, items.size());
        boolean listenerSet = false;

        int added = 0;
        for (i = 0; i < items.size() && added < addCount; ++i) {
            AbstractListItem item = (AbstractListItem) items.elementAt(i);
            if (item.filter(filter)) {
                Field f = (Field) item;
                item.setId(itemsCount);
                item.setItemListener(this);
                if (f.getChangeListener() != null) {
                    f.setChangeListener(null);
                }
                f.setChangeListener(this);
                willBeAdded.addElement(f);
                itemsCount++;
                added++;

                if (i < s - 1) {
                    SeparatorField sep = new SeparatorField(separatorHeight, separatorColor);
                    willBeAdded.addElement(sep);
                }

                if (mode == List.MODE_NORMAL && i >= s - 8 && !listenerSet && filter.length() == 0) {
                    item.setItemPaintListener(this);
                    listenPaintFrom = item;
                    listenerSet = true;
                }
            }
        }

        addAll(willBeAdded);

        if (mode == List.MODE_INVERT) {
            setInvertNextPageListener();
        }

        if (_addCount <= 0) {
            if (remainingItems == null) {
                remainingItems = new Vector();
            }
            for (i = addCount; i < items.size(); ++i) {
                AbstractListItem item = (AbstractListItem) items.elementAt(i);
                if (item.filter(filter)) {
                    remainingItems.addElement(item);
                }
            }
        }

        invalidate();
    }

    public void appendItemsBeginningInvert(Vector items, int _addCount) {
        if (mode != List.MODE_INVERT) {
            return;
        }

        int addCount = ITEMS_PER_PAGE, i;

        Vector willBeAdded = new Vector();

        if (itemsCount > 0 && items.size() > 0) {
            SeparatorField sep = new SeparatorField(separatorHeight, separatorColor);
            willBeAdded.addElement(sep);

            if (remainingItems != null && remainingItems.size() > 0) {
                addCount = 0;
            }
        }

        if (_addCount > 0) {
            addCount = _addCount;
        }

        if (listenPaintFrom != null && addCount > 0) {
            listenPaintFrom.setItemPaintListener(null);
        }

        int s = Math.min(addCount, items.size());
        boolean listenerSet = false;

        int added = 0;
        for (i = items.size() - 1; i >= 0 && added < addCount; --i) {
            AbstractListItem item = (AbstractListItem) items.elementAt(i);
            if (item.filter(filter)) {
                Field f = (Field) item;
                item.setId(itemsCount);
                item.setItemListener(this);
                if (f.getChangeListener() != null) {
                    f.setChangeListener(null);
                }
                f.setChangeListener(this);
                willBeAdded.insertElementAt(f, 0);
                itemsCount++;
                added++;

                if (added < s - 1) {
                    SeparatorField sep = new SeparatorField(separatorHeight, separatorColor);
                    willBeAdded.insertElementAt(sep, 0);
                }

                if (mode == List.MODE_NORMAL && i >= s - 8 && !listenerSet && filter.length() == 0) {
                    item.setItemPaintListener(this);
                    listenPaintFrom = item;
                    listenerSet = true;
                }
            }
        }

        displayingItems.removeAllElements();
        int k, kk = 0;
        for (k = 0; k < willBeAdded.size(); ++k) {
            displayingItems.addElement(willBeAdded.elementAt(k));
            if (willBeAdded.elementAt(k) instanceof AbstractListItem) {
                ((AbstractListItem) willBeAdded.elementAt(k)).setId(kk++);
            }
        }
        for (k = 0; k < getFieldCount(); ++k) {
            displayingItems.addElement(getField(k));
            if (getField(k) instanceof AbstractListItem) {
                ((AbstractListItem) getField(k)).setId(kk++);
            }
        }
        deleteAll();
        addAll(displayingItems);

        if (mode == List.MODE_INVERT) {
            setInvertNextPageListener();
        }

        if (_addCount <= 0) {
            if (remainingItems == null) {
                remainingItems = new Vector();
            }

            for (i = items.size() - 1 - added; i >= 0; --i) {
                AbstractListItem item = (AbstractListItem) items.elementAt(i);
                if (item.filter(filter)) {
                    remainingItems.insertElementAt(item, 0);
                }
            }
        }

        invalidate();
    }

    public void fieldChanged(Field field, int arg1) {
        if (feedback != null) {
            ListItem item = (ListItem) field;
            feedback.onClick(item.getId());
        }
    }

    public void filter(String filter) {
        if (filter == null) {
            filter = "";
        }

        filter = filter.trim().toUpperCase();
        if (this.filter != null && filter.equals(this.filter)) {
            return;
        }
        this.filter = filter;
        setItemsInternal(allItems);
    }

    public int getItemsCount() {
        return itemsCount;
    }

    public int getRealItemId(int id) {
        return id * 2;
    }

    public void itemClick(int id, AbstractListItem item) {
        if (listener != null) {
            listener.itemClick(id, item);
        }
    }

    protected boolean keyChar(char ch, int status, int time) {
        if (ch == 'T' || ch == 't' || ch == 'Н' || ch == 'н') {
            selectFirst();
        } else if (ch == 'B' || ch == 'b' || ch == 'т' || ch == 'Т') {
            selectLast();
        } else if (ch == ' ') {
            scrollOnePageDown();
        }

        return super.keyChar(ch, status, time);
    }

    protected void loadNextPage() {
        if (mode == List.MODE_INVERT) {
            loadNextPageInvert();
            return;
        }

        if (remainingItems != null && remainingItems.size() > 0) {
            if (nextPageLoading) {
                return;
            }
            nextPageLoading = true;

            int s = Math.min(ITEMS_PER_PAGE, remainingItems.size());

            appendItems(remainingItems, Math.min(remainingItems.size(), s));

            for (int i = 0; i < s; ++i) {
                remainingItems.removeElementAt(0);
            }

            nextPageLoading = false;
        } else if (!lastPage && listener != null) {
            if (nextPageLoading || lastNextPageCount == itemsCount) {
                return;
            }
            nextPageLoading = true;
            lastNextPageCount = itemsCount;
            listener.loadNextPage(itemsCount);
        }
    }

    protected void loadNextPageInvert() {
        if (remainingItems != null && remainingItems.size() > 0) {
            if (nextPageLoading) {
                return;
            }
            nextPageLoading = true;

            int s = Math.min(ITEMS_PER_PAGE, remainingItems.size());

            // lastNextPageCount = itemsCount;

            appendItemsBeginningInvert(remainingItems, Math.min(remainingItems.size(), s));

            owner.getMainManager().setVerticalScroll(
                    lastVecticalScrollPosition + getContentHeight() - lastContentHeight);

            if (lastSelectedField >= 0) {
                int index = lastSelectedField + getFieldCount() - lastFieldsCount;
                if (index >= 0) {
                    Field f = getField(index);
                    if (f.isFocusable() && !f.isFocus()) {
                        f.setFocus();
                    } else if (index < getFieldCount() - 1) {
                        f = getField(index + 1);
                        if (f.isFocusable() && !f.isFocus()) {
                            f.setFocus();
                        } else if (index > 0) {
                            f = getField(index - 1);
                            if (f.isFocusable() && !f.isFocus()) {
                                f.setFocus();
                            }

                        }
                    }
                }
            }

            int c = 0;
            for (int i = remainingItems.size() - 1; i >= 0 && c < s; --i) {
                remainingItems.removeElementAt(i);
                ++c;
            }

            nextPageLoading = false;
        } else if (!lastPage && listener != null) {
            if (nextPageLoading || lastNextPageCount == itemsCount) {
                return;
            }
            nextPageLoading = true;
            lastNextPageCount = itemsCount;
            listener.loadNextPage(itemsCount);
        }
    }

    public void nextPageForceLoading() {
        nextPageLoading = true;
    }

    public void nextPageLoaded() {
        if (mode == List.MODE_INVERT && owner != null) {
            try {
                if (lastContentHeight > 0) {
                    owner.getMainManager().setVerticalScroll(getContentHeight() - lastContentHeight);

                    if (lastSelectedField >= 0) {
                        int index = getFieldCount() - lastFieldsCount;
                        if (index >= 0) {
                            Field f = getField(index);
                            if (f.isFocusable() && !f.isFocus()) {
                                f.setFocus();
                            } else if (index < getFieldCount() - 1) {
                                f = getField(index + 1);
                                if (f.isFocusable() && !f.isFocus()) {
                                    f.setFocus();
                                } else if (index > 0) {
                                    f = getField(index - 1);
                                    if (f.isFocusable() && !f.isFocus()) {
                                        f.setFocus();
                                    }

                                }
                            }
                        }
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        nextPageLoading = false;
    }

    public void onPaint() {
        if (listenPaintFrom != null) {
            XYRect excent = ((Field) listenPaintFrom).getExtent();
            VkMainScreen owner = this.owner == null ? RootScreen.getLastInstance() : this.owner;
            Manager m = owner.getMainManager();

            if (excent.y <= m.getVerticalScroll() + getVisibleHeight()) {
                if (!nextPageLoading) {
                    lastVecticalScrollPosition = owner.getMainManager().getVerticalScroll();
                    lastContentHeight = getContentHeight();
                    lastFieldsCount = getFieldCount();
                    lastSelectedField = getFieldWithFocusIndex();
                    loadNextPage();
                }
            }
        }
    }

    public void scrollOnePageDown() {
        owner.scrollLayout(Manager.DOWNWARD);
    }

    public void scrollToBottom() {
        Manager m = owner.getMainManager();
        if (m.getVirtualHeight() > m.getVisibleHeight()) {
            try {
                m.setVerticalScroll(m.getVirtualHeight() - m.getVisibleHeight());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void selectFirst() {
        setScrollingInertial(false);
        for (int i = 0; i < getFieldCount(); ++i) {
            Field f = getField(i);
            if (f instanceof AbstractListItem) {
                f.setFocus();
                break;
            }
        }
        setScrollingInertial(true);
    }

    public void selectLast() {
        setScrollingInertial(false);
        for (int i = getFieldCount() - 1; i >= 0; --i) {
            Field f = getField(i);
            if (f instanceof AbstractListItem) {
                f.setFocus();
                break;
            }
        }
        setScrollingInertial(true);
    }

    public void setFocus() {
        if (getFieldCount() > 0) {
            getField(0).setFocus();
        } else {
            super.setFocus();
        }
    }

    private void setInvertNextPageListener() {
        if (mode == List.MODE_INVERT && filter.length() == 0) {
            for (int i = Math.min(1, getFieldCount() - 1); i >= 0; --i) {
                Field f = getField(i);
                if (f instanceof AbstractListItem) {
                    AbstractListItem item = (AbstractListItem) f;
                    item.setItemPaintListener(this);
                    listenPaintFrom = item;
                    break;
                }
            }
        }
    }

    public void setItems(Vector items) {
        if (items == null) {
            return;
        }

        allItems = items;
        setItemsInternal(items);
    }

    public void setItemsInternal(Vector items) {
        if (mode == List.MODE_INVERT) {
            setItemsInternalInvert(items);
            return;
        }

        itemsCount = 0;

        int addCount = ITEMS_PER_PAGE, i;

        if (listenPaintFrom != null) {
            listenPaintFrom.setItemPaintListener(null);
        }

        int s = Math.min(addCount, items.size());
        boolean listenerSet = false;

        Vector willBeAdded = new Vector(s);
        displayingItems.removeAllElements();

        int added = 0;
        for (i = 0; i < items.size() && added < addCount; ++i) {
            AbstractListItem item = (AbstractListItem) items.elementAt(i);
            if (item.filter(filter)) {
                item.setId(i);
                Field f = (Field) item;
                item.setItemListener(this);
                if (f.getChangeListener() != null) {
                    f.setChangeListener(null);
                }
                f.setChangeListener(this);
                willBeAdded.addElement(f);
                displayingItems.addElement(f);
                itemsCount++;
                added++;

                if (i < s - 1) {
                    SeparatorField sep = new SeparatorField(separatorHeight, separatorColor);
                    willBeAdded.addElement(sep);
                }

                if (mode == List.MODE_NORMAL && i >= s - 8 && !listenerSet && filter.length() == 0) {
                    item.setItemPaintListener(this);
                    listenPaintFrom = item;
                    listenerSet = true;
                }
            }
        }

        this.deleteAll();
        addAll(willBeAdded);

        if (mode == List.MODE_INVERT) {
            setInvertNextPageListener();
        }

        if (remainingItems == null) {
            remainingItems = new Vector();
        } else {
            remainingItems.removeAllElements();
        }

        for (; i < items.size(); ++i) {
            AbstractListItem item = (AbstractListItem) items.elementAt(i);
            if (item.filter(filter)) {
                remainingItems.addElement(item);
            }
        }
    }

    public void setItemsInternalInvert(Vector items) {
        itemsCount = 0;

        int addCount = ITEMS_PER_PAGE, i;

        if (listenPaintFrom != null) {
            listenPaintFrom.setItemPaintListener(null);
        }

        int s = Math.min(addCount, items.size());
        boolean listenerSet = false;

        Vector willBeAdded = new Vector(s);
        displayingItems.removeAllElements();

        int added = 0;
        for (i = items.size() - 1; i >= 0 && added < addCount; --i) {
            AbstractListItem item = (AbstractListItem) items.elementAt(i);
            if (item.filter(filter)) {
                item.setId(i);
                Field f = (Field) item;
                item.setItemListener(this);
                if (f.getChangeListener() != null) {
                    f.setChangeListener(null);
                }
                f.setChangeListener(this);
                willBeAdded.insertElementAt(f, 0);
                displayingItems.addElement(f);
                itemsCount++;
                added++;

                if (items.size() - i - 1 < s - 1) {
                    SeparatorField sep = new SeparatorField(separatorHeight, separatorColor);
                    willBeAdded.insertElementAt(sep, 0);
                }

                if (mode == List.MODE_NORMAL && items.size() - i - 1 >= s - 8 && !listenerSet
                        && filter.length() == 0) {
                    item.setItemPaintListener(this);
                    listenPaintFrom = item;
                    listenerSet = true;
                }
            }
        }

        int kk = 0;
        for (int k = 0; k < willBeAdded.size(); ++k) {
            Field f = (Field) willBeAdded.elementAt(k);
            if (f instanceof AbstractListItem) {
                ((AbstractListItem) willBeAdded.elementAt(k)).setId(kk++);
            }
        }

        this.deleteAll();
        addAll(willBeAdded);

        if (mode == List.MODE_INVERT) {
            setInvertNextPageListener();
        }

        if (remainingItems == null) {
            remainingItems = new Vector();
        } else {
            remainingItems.removeAllElements();
        }

        for (; i >= 0; --i) {
            AbstractListItem item = (AbstractListItem) items.elementAt(i);
            if (item.filter(filter)) {
                remainingItems.insertElementAt(item, 0);
            }
        }
    }

    public void setLastPage(boolean value) {
        lastPage = value;
    }

    public void setListener(ListListener listener) {
        this.listener = listener;
    }

    public void setOwner(VkMainScreen owner) {
        this.owner = owner;
    }

    public void setSeparatorColor(int separatorColor) {
        this.separatorColor = separatorColor;
    }

    public void setSeparatorHeight(int separatorHeight) {
        this.separatorHeight = separatorHeight;
    }

    public void specialPaint(int id, AbstractListItem item) {
        if (listener != null) {
            listener.specialPaint(id, item);
        }
    }

    protected boolean touchEvent(TouchEvent message) {
        if (message.getEvent() == TouchEvent.CLICK) {
            int f = getFieldAtLocation(message.getX(1), message.getY(1));
            if (f >= 0) {
                getField(f).setFocus();
            }
            return super.touchEvent(message);
        } else {
            return super.touchEvent(message);
        }
    }

    public boolean touchEvent(TouchEvent message, int dy) {
        try {
            if (message.getEvent() == TouchEvent.CLICK) {
                int f = getFieldAtLocation(message.getX(1), message.getY(1) - dy);
                if (f >= 0) {
                    getField(f).setFocus();
                }
                return super.touchEvent(message);
            } else {
                return touchEvent(message);
            }
        } catch (Exception e) {
            return false;
        }
    }

}
