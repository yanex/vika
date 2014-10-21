package org.yanex.vika.gui.list;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.VerticalFieldManager;
import org.yanex.vika.RootScreen;
import org.yanex.vika.gui.screen.VkMainScreen;
import org.yanex.vika.gui.list.item.AbstractListItem;
import org.yanex.vika.gui.list.item.AbstractListItem.ItemListener;
import org.yanex.vika.gui.list.item.AbstractListItem.ItemPaintListener;
import org.yanex.vika.gui.list.item.ListItem;
import org.yanex.vika.gui.widget.base.SeparatorField;

import java.util.Vector;

public class InvertedList extends VerticalFieldManager implements FieldChangeListener,
    ItemPaintListener, ItemListener {

  public static interface ListListener {
    public void itemClick(int id, AbstractListItem item);

    public void loadNextPage(int already);

    public void specialPaint(int id, AbstractListItem item);
  }

  public static final int MODE_NORMAL = 0;

  public static final int MODE_INVERT = 1;

  private int mode = InvertedList.MODE_NORMAL;

  private int ITEMS_PER_PAGE = 40;
  private int lastVecticalScrollPosition = 0;
  private int lastContentHeight = 0;
  private int lastSelectedField = -1;

  private int lastFieldsCount = 0;
  private ListFeedback feedback;
  private int separatorColor = 0xd8dde3;

  private int separatorHeight = 1;
  private Vector allItems = null;

  private String filter = "";
  private Vector remainingItems = null;
  private boolean lastPage = false;
  private boolean nextPageLoading = false;

  private int lastNextPageCount = -1;

  private AbstractListItem listenPaintFrom = null;

  private int itemsCount = 0;

  private ListListener listener;

  private VkMainScreen owner;

  public InvertedList() {

  }

  public InvertedList(int mode) {
    this.mode = mode;
    if (mode == InvertedList.MODE_INVERT) {
      ITEMS_PER_PAGE = Integer.MAX_VALUE;
    }
  }

  public InvertedList(int mode, long style) {
    super(style);
    this.mode = mode;
    if (mode == InvertedList.MODE_INVERT) {
      ITEMS_PER_PAGE = Integer.MAX_VALUE;
    }
  }

  public InvertedList(long style) {
    super(style);
  }

  protected void addAll(Vector fields) {
    Field[] a = new Field[fields.size()];
    fields.copyInto(a);
    addAll(a);
  }

  public void appendItems(Vector items) {
    if (allItems == null) {
      allItems = new Vector();
    }

    for (int i = 0; i < items.size(); ++i) {
      allItems.addElement(items.elementAt(i));
    }
    appendItems(items, -1);
  }

  protected void appendItems(Vector items, int _addCount) {
    int addCount = ITEMS_PER_PAGE, i;

    Vector willBeAdded = new Vector(); // Math.max(_addCount,
    // ITEMS_PER_PAGE));

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

        if (mode == InvertedList.MODE_NORMAL && i >= s - 8 && !listenerSet && filter.length() == 0) {
          item.setItemPaintListener(this);
          listenPaintFrom = item;
          listenerSet = true;
        }
      }
    }

    addAll(willBeAdded);

    if (mode == InvertedList.MODE_INVERT) {
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

  public VkMainScreen getOwner() {
    return owner;
  }

  public int getRealItemId(int id) {
    return id * 2;
  }

  public int getSeparatorColor() {
    return separatorColor;
  }

  public int getSeparatorHeight() {
    return separatorHeight;
  }

  public void itemClick(int id, AbstractListItem item) {
    if (listener != null) {
      listener.itemClick(id, item);
    }
  }

  protected void loadNextPage() {
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

  public void nextPageLoaded() {
    if (mode == InvertedList.MODE_INVERT && owner != null) {
      try {
        // Dialog.alert(lastVecticalScrollPosition+" "+getContentHeight()+" "+lastContentHeight+" "+owner.getVerticalScroll());
        owner.getMainManager().setVerticalScroll(
            lastVecticalScrollPosition + getContentHeight() - lastContentHeight);
        if (lastSelectedField >= 0) {
          Field f = getField(lastSelectedField + getFieldCount() - lastFieldsCount);
          if (f.isFocusable() && !f.isFocus()) {
            f.setFocus();
          }
        }
      } catch (Exception e) {
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
          Field f = getFieldWithFocus();
          if (f instanceof AbstractListItem) {
            lastSelectedField = getRealItemId(((AbstractListItem) f).getId());
          } else {
            lastSelectedField = -1;
          }
          loadNextPage();
        }
      }
    }
  }

  public void removeItem(int id) {
    if (id < 0 || id > itemsCount) {
      return;
    }

    int realId = getRealItemId(id);
    if (id < itemsCount - 1) {
      Field f = getField(realId);
      f.setChangeListener(null);
      delete(f);
    } else {
      Field f = getField(realId);
      f.setChangeListener(null);
      deleteRange(realId, 2);
    }
  }

  public void replaceChild(AbstractListItem l1, AbstractListItem l2) {
    Field f1 = (Field) l1;
    Field f2 = (Field) l2;

    l2.setId(l1.getId());
    l2.setItemListener(l1.getItemListener());
    l2.setItemPaintListener(l1.getItemPaintListener());

    replace(f1, f2);
  }

  public void scrollToBottom() {
    Manager m = owner.getMainManager();
    if (m.getVirtualHeight() > m.getVisibleHeight()) {
      try {
        m.setVerticalScroll(m.getVirtualHeight() - m.getVisibleHeight());
      } catch (Exception e) {
      }
    }
  }

  public void scrollToTop() {
    try {
      owner.getMainManager().setVerticalScroll(0);
    } catch (Exception e) {
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

  public void setFeedback(ListFeedback feedback) {
    this.feedback = feedback;
  }

  public void setFocus() {
    if (getFieldCount() > 0) {
      getField(0).setFocus();
    } else {
      super.setFocus();
    }
  }

  public void setInertial(boolean isInertial) {
    setScrollingInertial(isInertial);
  }

  private void setInvertNextPageListener() {
    if (mode == InvertedList.MODE_INVERT && filter.length() == 0) {
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
    allItems = items;
    setItemsInternal(items);
  }

  public void setItemsInternal(Vector items) {
    itemsCount = 0;

    int addCount = ITEMS_PER_PAGE, i;

    if (listenPaintFrom != null) {
      listenPaintFrom.setItemPaintListener(null);
    }

    int s = Math.min(addCount, items.size());
    boolean listenerSet = false;

    Vector willBeAdded = new Vector(s);

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
        itemsCount++;
        added++;

        if (i < s - 1) {
          SeparatorField sep = new SeparatorField(separatorHeight, separatorColor);
          willBeAdded.addElement(sep);
        }

        if (mode == InvertedList.MODE_NORMAL && i >= s - 8 && !listenerSet && filter.length() == 0) {
          item.setItemPaintListener(this);
          listenPaintFrom = item;
          listenerSet = true;
        }
      }
    }

    this.deleteAll();
    addAll(willBeAdded);

    if (mode == InvertedList.MODE_INVERT) {
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
