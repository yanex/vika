package org.yanex.vika.gui.list.item;

import me.regexp.RE;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.ActiveRichTextField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;
import org.yanex.vika.Vika;
import org.yanex.vika.api.item.*;
import org.yanex.vika.gui.util.*;
import org.yanex.vika.gui.widget.VkAudioAttachmentField;
import org.yanex.vika.gui.widget.VkDocumentAttachmentField;
import org.yanex.vika.gui.widget.VkPhotoAttachmentField;
import org.yanex.vika.gui.widget.VkPhotoAttachmentField.PhotoAttachmentFieldListener;
import org.yanex.vika.gui.widget.base.*;
import org.yanex.vika.gui.widget.manager.BalloonFieldManager;
import org.yanex.vika.gui.widget.manager.FocusableHFM;
import org.yanex.vika.gui.widget.manager.MaxWidthVerticalFieldManager;
import org.yanex.vika.local.Local;
import org.yanex.vika.local.VikaResource;
import org.yanex.vika.util.HappyDate;
import org.yanex.vika.util.StringUtils;
import org.yanex.vika.util.bb.Blackberry;
import org.yanex.vika.util.fun.RichVector;

import java.util.Vector;

public class MessageItem extends FocusableHFM implements AbstractListItem,
    PhotoAttachmentFieldListener {

  private class CopyMenuItem extends MenuItem {
    public CopyMenuItem() {
      super(Local.tr(VikaResource.Copy), 0, 2);
    }

    public void run() {
      String text = null;
      if (message.getBody() != null && message.getBody().length() > 0) {
        text = message.getBody();
      }
      if (text != null) {
        try {
          net.rim.device.api.system.Clipboard.getClipboard().put(text);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  private class LinkMenuItem extends MenuItem {
    public final String link;

    public LinkMenuItem(String link, int ordinal) {
      super(link, ordinal, ordinal);
      this.link = link;
    }

    public void run() {
      Blackberry.launch(link);
    }
  }

  private class V {

    private HorizontalFieldManager root;
    private VerticalFieldManager roof;

    private HorizontalFieldManager hfmLeft;

    private MaxWidthVerticalFieldManager balloonOutside;
    private BalloonFieldManager balloon;

    private HorizontalFieldManager hfmRight;

    private TextField text;
    private ActiveRichTextField activeText;

    private CustomLabelField topTextLabel;

    private SimpleLabelField date;
    private ImageField delivered;

    private AutoLoadingBitmapField userIcon;

    public V() {
      root = new HorizontalFieldManager(message.isOut() ? Field.FIELD_RIGHT : 0);
      roof = new VerticalFieldManager();

      MessageItem.this.add(roof);

      hfmLeft = new HorizontalFieldManager(Field.FIELD_BOTTOM);
      balloonOutside = new MaxWidthVerticalFieldManager();
      balloon = new BalloonFieldManager();
      hfmRight = new HorizontalFieldManager(Field.FIELD_BOTTOM);

      balloonOutside.add(balloon);

      activeText = new ActiveRichTextField("", RichTextField.USE_TEXT_WIDTH) {

        protected boolean invokeAction(int action) {
          switch (action) {
            case ACTION_INVOKE: {
              launch();
              return true;
            }
          }

          return super.invokeAction(action);
        }

        protected boolean keyChar(char character, int status, int time) {
          if (character == Characters.ENTER) {
            launch();
            return true;
          }

          return super.keyChar(character, status, time);
        }

        private void launch() {
          String url = getRegionText();
          if (url != null & url.length() > 0) {
            Blackberry.launch(url);
          }
        }

        protected boolean navigationUnclick(int status, int time) {
          launch();

          return true;
        }

        protected boolean touchEvent(TouchEvent message) {
          boolean isOutOfBounds = touchEventOutOfBounds(message);

          if (message.getEvent() == TouchEvent.UNCLICK && !isOutOfBounds) {
            launch();
            return true;
          }

          return super.touchEvent(message);
        }

        private boolean touchEventOutOfBounds(TouchEvent message) {
          int x = message.getX(1);
          int y = message.getY(1);
          return x < 0 || y < 0 || x > getWidth() || y > getHeight();
        }
      };

      text = new TextField();

      date = new SimpleLabelField("", Field.FIELD_VCENTER, MessageItem.DATE_THEME);

      topTextLabel = new CustomLabelField("ABC", Field.USE_ALL_WIDTH | DrawStyle.HCENTER,
          MessageItem.DATE_THEME);
      topTextLabel.setMargin(MessageItem.px(1), 0, MessageItem.px(1), 0);
      topTextLabel.setFont(Fonts.defaultFont);

      delivered = new ImageField(null, MessageItem.px(8), MessageItem.px(8), Field.FIELD_VCENTER);

      userIcon = new AutoLoadingBitmapField(new XYDimension(DP10, DP10),
          Field.FIELD_VCENTER, true);
    }
  }

  private static Theme THEME, DATE_THEME;

  private final Theme BLACK_THEME, GRAY_THEME, BLUE_THEME;

  private static final String LINK_REGEXP_RAW = "(http|https|ftp)\\://[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,4}(:[a-zA-Z0-9]*)?/?([a-zA-Z0-9\\-\\._\\?\\,\\'/\\\\\\+&amp;%\\$#\\=~])*"; // [^
  // \\.\\,\\)\\(\\s]";
  private static final RE LINK_REGEXP = new RE(MessageItem.LINK_REGEXP_RAW);

  private static Background UNREAD = BackgroundFactory.createSolidTransparentBackground(0xcbd5e3,
      200);
  private static BalloonBackground leftDefaultBackground = new BalloonBackground(0xf5f5f5, true);

  private static BalloonBackground leftFocusBackground = new BalloonBackground(0x318be1, true);

  private static BalloonBackground rightDefaultBackground = new BalloonBackground(0xf0f4f9, false);

  private static BalloonBackground rightFocusBackground = new BalloonBackground(0x318be1, false);

  static {
    MessageItem.THEME = new Theme();
    MessageItem.THEME.setPrimaryColor(0x000000);
    MessageItem.THEME.setSecondaryFontColor(0xFFFFFF);

    MessageItem.DATE_THEME = new Theme();
    MessageItem.DATE_THEME.setPrimaryColor(0x91a4b6);
  }

  public static Vector fromMessages(Vector m) {
    Vector ret = new Vector();

    for (int i = 0; i < m.size(); ++i) {
      ret.addElement(new MessageItem((Message) m.elementAt(i)));
    }

    return ret;
  }

  public static RichVector insertDateMarkers(Vector m) {
    RichVector ret = new RichVector(m.size() + 1);

    HappyDate prev = null;
    MessageItem pref = null;

    for (int i = m.size() - 1; i >= 0; --i) {
      if (m.elementAt(i) instanceof MessageItem) {
        MessageItem message = (MessageItem) m.elementAt(i);
        ret.insertElementAt(message, 0);
        HappyDate d = new HappyDate(message.getMessage().getDate() * 1000);

        if (message.getTopText() != null) {
          message.setTopText(null);
        }

        if (prev != null && pref != null) {
          if (d.day != prev.day) {
            pref.setTopText(prev.day2() + "." + prev.month2() + "." + prev.year2());
          }
        }

        prev = d;
        pref = message;
      }
    }

    if (pref != null && prev != null) {
      pref.setTopText(prev.day2() + "." + prev.month2() + "." + prev.year2());
    }

    return ret;
  }

  protected static int px(int pt) {
    return Ui.convertSize(pt, Ui.UNITS_pt, Ui.UNITS_px);
  }

  private V v;

  private int id;

  private ItemPaintListener itemPaintListener;
  private ItemListener itemListener;

  private Message message;

  private RoundedBackground defaultBackground;

  // private SubMenu submenu = null;

  private RoundedBackground focusedBackground;

  private String topText = null;

  private boolean selected;

  private boolean sending = false;

  private Vector forwardedItems = new Vector();

  private MenuItem[] items;

  public MessageItem(Message message) {
    this(message, false);
  }

  public MessageItem(Message message, boolean sending) {
    super(Field.USE_ALL_WIDTH, MessageItem.THEME);

    BLACK_THEME = new Theme();
    BLACK_THEME.setPrimaryColor(0);

    GRAY_THEME = new Theme();
    GRAY_THEME.setPrimaryColor(0x91a4b6);

    BLUE_THEME = new Theme();
    BLUE_THEME.setPrimaryColor(0x4f7ca3);

    this.message = message;

    v = new V();

    if (message.isOut()) {
      defaultBackground = MessageItem.rightDefaultBackground;
      focusedBackground = MessageItem.rightFocusBackground;
    } else {
      defaultBackground = MessageItem.leftDefaultBackground;
      focusedBackground = MessageItem.leftFocusBackground;
    }

    v.balloonOutside.setBackground(defaultBackground);

    int PD = MessageItem.px(1);
    XYEdges _padding = defaultBackground.getPadding();
    XYEdges padding = new XYEdges(_padding.top + PD, _padding.right + PD, _padding.bottom + PD,
        _padding.left + PD);

    v.balloon.setPadding(padding.top, padding.right, padding.bottom, padding.left);

    v.date.setText(HappyDate.getSimpleStringDate(message.getDate() * 1000));

    {
      if (getMessage().getUser() != null
          && getMessage().getUser().getId() == (Vika.api().getToken().getUserId())) {
        if (message.isOut()) {
          v.hfmLeft.add(v.date);
        } else {
          v.hfmRight.add(v.date);
        }
        if (sending) {
          v.hfmRight.add(v.delivered);
        }
      } else {
        if (sending) {
          v.hfmLeft.add(v.delivered);
        }
        if (message.isOut()) {
          v.hfmLeft.add(v.date);
        } else {
          v.hfmRight.add(v.date);
        }
      }
    }

    setRead(message.isRead());

    if (message.getBody() != null && message.getBody().length() > 0) {

      if (testActive(message.getBody())) {

        int[] offsets = {0, message.getBody().length()};
        Font[] fonts = {Fonts.defaultFont};
        byte[] attributes = {0};
        int[] foregroundColors = {0};
        int[] backgroundColors = {-1};

        v.activeText.setText(message.getBody(), offsets, attributes, fonts,
            foregroundColors, backgroundColors);
        v.balloon.add(v.activeText);
      } else {
        v.text.setText(message.getBody());
        v.balloon.add(v.text);
      }
    }

    int i;

    Vector documentItems = new Vector();
    Vector photoItems = new Vector();
    Vector audioItems = new Vector();
    Vector videoItems = new Vector();

    if (message.getAttachments() != null) {
      for (i = 0; i < message.getAttachments().size(); ++i) {
        Attachment a = (Attachment) message.getAttachments().getObject(i);
        if (a instanceof PhotoAttachment) {
          PhotoAttachment pa = (PhotoAttachment) a;
          if (pa.getSrc() != null && pa.getSrc().length() > 0) {
            VkPhotoAttachmentField item = new VkPhotoAttachmentField(pa);
            item.setAttachmentListener(this);
            photoItems.addElement(item);
          }
        } else if (a instanceof DocumentAttachment) {
          DocumentAttachment da = (DocumentAttachment) a;
          if (da.getSize() < 1000 * 1000
              && (da.getExt().toLowerCase().equals("png")
              || da.getExt().toLowerCase().equals("jpg") || da.getExt()
              .toLowerCase().equals("gif"))) {
            VkPhotoAttachmentField item = new VkPhotoAttachmentField(da);
            item.setAttachmentListener(this);
            documentItems.addElement(item);
          } else {
            VkDocumentAttachmentField item = new VkDocumentAttachmentField(da);
            documentItems.addElement(item);
          }
        } else if (a instanceof VideoAttachment) {
          VideoAttachment va = (VideoAttachment) a;
          VkPhotoAttachmentField item = new VkPhotoAttachmentField(va);
          item.setAttachmentListener(this);
          videoItems.addElement(item);
        } else if (a instanceof AudioAttachment) {
          AudioAttachment aa = (AudioAttachment) a;
          VkAudioAttachmentField item = new VkAudioAttachmentField(aa, BLACK_THEME);
          audioItems.addElement(item);
        }

      }
    }

    v.balloon.addAll(photoItems);
    v.balloon.addAll(documentItems);
    v.balloon.addAll(videoItems);
    v.balloon.addAll(audioItems);

    if (message.getGeo() != null) {
      VkPhotoAttachmentField item = new VkPhotoAttachmentField(new GeoAttachment(message));
      item.setAttachmentListener(this);
      v.balloon.add(item);
    }

    if (message.getForwardedMessages() != null) {
      for (i = 0; i < message.getForwardedMessages().size(); ++i) {
        Message fm = (Message) message.getForwardedMessages().getObject(i);
        ForwardedMessageItem fmi = new ForwardedMessageItem(fm);
        forwardedItems.addElement(fmi);
        v.balloon.add(fmi);
      }
    }

    if (!message.isOut() && message.isFromChat()) {
      v.hfmLeft.add(v.userIcon);
      v.userIcon.setURL(message.getUser().getPhotoURL());
    }

    v.balloonOutside.setMaxWidth(Display.getWidth() - v.hfmLeft.getPreferredWidth()
        - v.hfmRight.getPreferredWidth() - MessageItem.px(1) * 4);

    v.hfmLeft.setPadding(padding.top, MessageItem.px(1), padding.top, MessageItem.px(1));
    v.hfmRight.setPadding(padding.top, MessageItem.px(1), padding.top, MessageItem.px(1));

    v.root.add(v.hfmLeft);
    v.root.add(v.balloonOutside);
    v.root.add(v.hfmRight);

    add(v.root);

    if (message.getBody() != null && message.getBody().length() > 0) {
      String[] links = StringUtils.matches(message.getBody(), MessageItem.LINK_REGEXP_RAW);

      if (links.length > 0) {
        MenuItem[] items = new MenuItem[links.length];

        for (i = 0; i < items.length; ++i) {
          items[i] = new LinkMenuItem(links[i], i);
        }

        this.items = items;
      }
    }

    addingCompleted();
  }

  public void delivered() {
    v.delivered.setBitmap(R.instance.getBitmap("Convs/Sent.png"));
  }

  public void fieldChanged(Field f, int arg1) {
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

  public Message getMessage() {
    return message;
  }

  public String getTopText() {
    return topText;
  }

  public boolean isSelected() {
    return selected;
  }

  protected void makeMenu(Menu menu, int instance) {
    super.makeMenu(menu, instance);
    if (message.getBody() != null && message.getBody().length() > 0) {
      menu.add(new CopyMenuItem());

      if (this.items != null) {
        for (int i = 0; i < this.items.length; ++i) {
          menu.add(items[i]);
        }
      }
    }
  }

  protected void onLayoutFocus() {
    if (!selected) {
      v.balloonOutside.setBackground(focusedBackground);

      // ABC
      // v.text.getTheme().setFontColor(0xFFFFFF);

      BLUE_THEME.setPrimaryColor(0xffffff);
      BLACK_THEME.setPrimaryColor(0xffffff);
      GRAY_THEME.setPrimaryColor(0xffffff);

      if (v.text.getManager() != null) {
        v.text.setColor(0xFFFFFF);
      }

      for (int i = 0; i < forwardedItems.size(); ++i) {
        ForwardedMessageItem fmi = (ForwardedMessageItem) forwardedItems.elementAt(i);
        fmi.drawFocus();
      }

      updateActiveBackground(true);
    }
  }

  protected void onLayoutUnfocus() {
    if (!selected) {
      v.balloonOutside.setBackground(defaultBackground);

      // ABC
      // v.text.getTheme().setFontColor(0x000000);

      BLUE_THEME.setPrimaryColor(0x4f7ca3);
      BLACK_THEME.setPrimaryColor(0);
      GRAY_THEME.setPrimaryColor(0x91a4b6);

      if (v.text.getManager() != null) {
        v.text.setColor(0x000000);
      }

      for (int i = 0; i < forwardedItems.size(); ++i) {
        ForwardedMessageItem fmi = (ForwardedMessageItem) forwardedItems.elementAt(i);
        fmi.drawUnfocus();
      }

      updateActiveBackground(false);
    }
  }

  public void onSizeChange(int newWidth, int newHeight) {
    updateLayout();
  }

  protected void paint(Graphics g) {
    super.paint(g);

    if (itemPaintListener != null) {
      itemPaintListener.onPaint();
    }

    if (!message.isRead() && !message.isOut()) {
      if (itemListener != null) {
        itemListener.specialPaint(id, this);
      }
      /*
       * Manager m = getManager(); if (m instanceof List) { List l = (List)m;
       * 
       * if (getTop() < (l.getOwner().getMainManager().getVerticalScroll()+ l.getVisibleHeight()) &&
       * (getTop()+getHeight()) > l.getOwner().getMainManager().getVerticalScroll()) { if
       * (itemListener!=null) { itemListener.specialPaint(id, this); } } }
       */
    }
  }

  protected void paintBackground(Graphics g) {
    /*
     * if (!message.isRead() || selected) { g.setColor(0xecf0f5); g.fillRect(0, 0,
     * getContentWidth(), getContentHeight()); } if (!isActive() && !isFocused()) {
     * 
     * } else { if (getBackground()!=null) getBackground().draw(g, getBackgroundRect()); }
     */
    if (getBackground() != null) {
      getBackground().draw(g, getBackgroundRect());
    }
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

  public void setRead(boolean read) {
    message = message.edit().setRead(read).build();
    if (!selected) {
      if (message.isRead()) {
        setBackground(null);
      } else {
        setBackground(MessageItem.UNREAD);
      }
    }
  }

  public void setSelected(boolean selected) {
    this.selected = selected;

    if (selected) {
      v.balloonOutside.setBackground(focusedBackground);

      // ABC
      // v.text.getTheme().setFontColor(0xFFFFFF);

      BLUE_THEME.setPrimaryColor(0xffffff);
      BLACK_THEME.setPrimaryColor(0xffffff);
      GRAY_THEME.setPrimaryColor(0xffffff);

      for (int i = 0; i < forwardedItems.size(); ++i) {
        ForwardedMessageItem fmi = (ForwardedMessageItem) forwardedItems.elementAt(i);
        fmi.drawFocus();
      }
    } else {
      if (isFocused()) {
        onLayoutFocus();
      } else {
        onLayoutUnfocus();
      }
      if (message.isRead()) {
        setBackground(null);
      } else {
        setBackground(MessageItem.UNREAD);
      }
    }
  }

  public void setTopText(String topText) {
    this.topText = topText;

    if (topText == null) {
      if (v.roof.getFieldCount() > 0) {
        v.roof.deleteAll();
      }
    } else {
      v.topTextLabel.setText(topText);
      if (v.roof.getFieldCount() == 0) {
        v.roof.add(v.topTextLabel);
      }
    }
  }

  private boolean testActive(String text) {
    if (true) {
      return false;
    }

    try {
      return MessageItem.LINK_REGEXP.match(text);
    } catch (Exception e) {
      return false;
    }
  }

  private void updateActiveBackground(boolean focused) {
    if (message.getBody() != null && message.getBody().length() > 0) {
      if (testActive(message.getBody())) {

        int[] offsets = {0, message.getBody().length()};
        Font[] fonts = {Fonts.defaultFont};
        byte[] attributes = {0};
        int[] foregroundColors = {focused ? 0xffffff : 0};
        int[] backgroundColors = {-1};

        v.activeText.setText(message.getBody(), offsets, attributes, fonts,
            foregroundColors, backgroundColors);
      }
    }
  }

  public void updateFlags(long newFlags) {
    long oldFlags = message.getFlags();
    message = message.edit().setFlags(newFlags).build();

    if ((oldFlags & 128) != (newFlags & 128)) {
      updateLayout();
    }

    setRead(message.isRead());
  }

  public String toString() {
    return "MessageItem [message=" + message + "]";
  }
}
