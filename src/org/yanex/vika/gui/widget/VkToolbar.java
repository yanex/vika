package org.yanex.vika.gui.widget;

import com.mobiata.bb.ui.decor.NinePatchBackground;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Background;
import org.yanex.vika.gui.util.Files;
import org.yanex.vika.gui.util.GradientBackground;
import org.yanex.vika.gui.util.R;
import org.yanex.vika.gui.util.Theme;
import org.yanex.vika.gui.widget.base.ImageSelectorField;

public class VkToolbar extends VerticalFieldManager implements FocusChangeListener,
    FieldChangeListener {

  public static interface ToolbarListener {
    public boolean toolbarClicked(int current);
  }

  private static Theme buttonTheme;

  private Field lastActiveField;

  private int lastWidth = -1;

  private int buttonWidth;
  private ImageSelectorField messages, friends, search, settings, write;

  private ImageSelectorField current;

  private ToolbarListener listener;

  static {
    VkToolbar.buttonTheme = new Theme();

    Background focusBackground = new NinePatchBackground(Files.TABS_FOCUS_BG);
    Background activeBackground = new NinePatchBackground(Files.TABS_ACTIVE_BG);

    VkToolbar.buttonTheme.setPaddingEdges(0, 0, 0, 0);

    VkToolbar.buttonTheme.setBackground(null, focusBackground, activeBackground,
        activeBackground);
  }

  public VkToolbar() {
    super(Field.USE_ALL_WIDTH);

    Background bg = new GradientBackground(0x181818, 0x0f0f0f, 1, 0x000000, 0, 0x000000);
    // Background bg = new NinePatchBackground(Files.TABS_TABBAR_BG);
    setBackground(bg);

    int px1 = px(1);

    int width = Display.getWidth();

    buttonWidth = (width - px1 * 2) / 5;
    px1 += (width - buttonWidth * 5) / 2;

    setPadding(px(2) * 2 / 3, px1, px(1), px1);

    setFocusListener(this);
  }

  public void fieldChanged(Field f, int arg1) {
    int id = -1;

    ImageSelectorField old = current;

    if (f == messages) {
      current = messages;
      id = 0;
    } else if (f == friends) {
      current = friends;
      id = 1;
    } else if (f == search) {
      current = search;
      id = 2;
    } else if (f == settings) {
      current = settings;
      id = 3;
    } else if (f == write) {
      current = write;
      id = 4;
    }

    if (listener != null && id >= 0) {
      if (!listener.toolbarClicked(id)) {
        return;
      }
    }

    if (old != null) {
      old.unselect();
    }

    if (id < 4) {
      current.select();
    }

  }

  public void focusChanged(Field field, int eventType) {
    if (eventType == FocusChangeListener.FOCUS_GAINED) {
      if (field instanceof VkToolbar) {
        // current.setFocus();
      }
    }
  }

  public ToolbarListener getListener() {
    return listener;
  }

  private void loadButtons() {
    int width = Display.getWidth();
    /*
     * if (Device.isBold()) { width = Display.getWidth(); } else { width =
     * Math.min(Display.getWidth(), Display.getHeight()); }
     */
    width -= getPaddingLeft() + getPaddingRight();

    Bitmap messagesDefault = R.instance.getBitmap(Files.TABS_MESSAGES_NOTACTIVE);
    Bitmap messagesHover = R.instance.getBitmap(Files.TABS_MESSAGES_HOVER);
    Bitmap messagesActive = R.instance.getBitmap(Files.TABS_MESSAGES_ACTIVE);

    Bitmap frendsDefault = R.instance.getBitmap(Files.TABS_FRIENDS_NOTACTIVE);
    Bitmap frendsHover = R.instance.getBitmap(Files.TABS_FRIENDS_HOVER);
    Bitmap frendsActive = R.instance.getBitmap(Files.TABS_FRIENDS_ACTIVE);

    Bitmap searchDefault = R.instance.getBitmap(Files.TABS_SEARCH_NOTACTIVE);
    Bitmap searchHover = R.instance.getBitmap(Files.TABS_SEARCH_HOVER);
    Bitmap searchActive = R.instance.getBitmap(Files.TABS_SEARCH_ACTIVE);

    Bitmap settingsDefault = R.instance.getBitmap(Files.TABS_SETTINGS_NOTACTIVE);
    Bitmap settingsHover = R.instance.getBitmap(Files.TABS_SETTINGS_HOVER);
    Bitmap settingsActive = R.instance.getBitmap(Files.TABS_SETTINGS_ACTIVE);

    Bitmap writeDefault = R.instance.getBitmap(Files.TABS_WRITE_NOTACTIVE);
    Bitmap writeHover = R.instance.getBitmap(Files.TABS_WRITE_HOVER);
    Bitmap writeActive = R.instance.getBitmap(Files.TABS_WRITE_ACTIVE);

    int buttonHeight = px(12);
    int buttonWidth = width / 5;

    messages = new ImageSelectorField(messagesDefault, messagesHover, messagesActive,
        messagesActive, buttonWidth, buttonHeight, 0, VkToolbar.buttonTheme, false);
    current = messages;
    current.select();
    messages.setFocusListener(this);
    messages.setChangeListener(this);

    friends = new ImageSelectorField(frendsDefault, frendsHover, frendsActive, frendsActive,
        buttonWidth, buttonHeight, 0, VkToolbar.buttonTheme, false);
    friends.setFocusListener(this);
    friends.setChangeListener(this);

    search = new ImageSelectorField(searchDefault, searchHover, searchActive, searchActive,
        buttonWidth, buttonHeight, 0, VkToolbar.buttonTheme, false);
    search.setFocusListener(this);
    search.setChangeListener(this);

    settings = new ImageSelectorField(settingsDefault, settingsHover, settingsActive,
        settingsActive, buttonWidth, buttonHeight, 0, VkToolbar.buttonTheme, false);
    settings.setFocusListener(this);
    settings.setChangeListener(this);

    write = new ImageSelectorField(writeDefault, writeHover, writeActive, writeActive, buttonWidth,
        buttonHeight, 0, VkToolbar.buttonTheme, false);
    write.setFocusListener(this);
    write.setChangeListener(this);

    HorizontalFieldManager hfm = new HorizontalFieldManager(Field.FIELD_HCENTER);
    hfm.add(messages);
    hfm.add(friends);
    hfm.add(search);
    hfm.add(settings);
    hfm.add(write);

    add(hfm);
  }

  protected int px(int pt) {
    return Ui.convertSize(pt, Ui.UNITS_pt, Ui.UNITS_px);
  }

  public void setListener(ToolbarListener listener) {
    this.listener = listener;
  }

  protected void sublayout(int maxWidth, int maxHeight) {
    if (lastWidth != maxWidth) {
      lastWidth = maxWidth;
      deleteAll();
      loadButtons();
    }

    super.sublayout(maxWidth, maxHeight);
  }
}
