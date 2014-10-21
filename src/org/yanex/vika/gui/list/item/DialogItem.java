package org.yanex.vika.gui.list.item;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.decor.Background;
import org.yanex.vika.api.item.Message;
import org.yanex.vika.gui.util.*;
import org.yanex.vika.gui.widget.base.AbstractBitmapField;
import org.yanex.vika.gui.widget.base.TextField;
import org.yanex.vika.local.CountHelper;
import org.yanex.vika.local.VikaResource;
import org.yanex.vika.util.HappyDate;
import org.yanex.vika.util.network.ImageLoader;
import org.yanex.vika.util.network.ImageLoaderCallback;
import org.yanex.vika.util.network.SeveralImageLoaderCallback;
import org.yanex.vika.util.network.State;

public class DialogItem extends ListItem implements AbstractListItem, ImageLoaderCallback,
    SeveralImageLoaderCallback, GuiItem {

  private static int HEIGHT = DP16;
  private static int PADDING = DP2 * 3 / 4;

  private static final Background BACKGROUND_FOCUS = new GradientBackground(0x59a0e8, 0x1c65be);
  private static final Background BACKGROUND_NOTREAD_DEFAULT = new RoundedBackground(0xecf0f5);
  private static final Background BACKGROUND_NOTREAD_FOCUSED = new RoundedBackground(0x1c65be);

  private static final XYDimension MAIN_PHOTO_SIZE = new XYDimension(DP16, DP16);
  private static final XYDimension MY_PHOTO_SIZE = new XYDimension(DP10, DP10);

  private static final AbstractBitmapField ABITMAP_MAIN_PHOTO = new AbstractBitmapField(
      R.instance.getBitmap("camera.png"), MAIN_PHOTO_SIZE, true, true);
  private static final AbstractBitmapField ABITMAP_MY_PHOTO = new AbstractBitmapField(
      R.instance.getBitmap("camera.png"), MY_PHOTO_SIZE, true, true);

  private static final Theme BLACK_THEME = new Theme().setPrimaryColor(0);
  private static final Theme BLUE_THEME = new Theme().setPrimaryColor(0x4f7ca3);

  private static Theme BACKGROUND_THEME = new Theme()
      .setPrimaryColor(0x000000)
      .setSecondaryFontColor(0xFFFFFF)
      .setPaddingEdges(PADDING, PADDING, PADDING, PADDING)
      .setBackground(null, BACKGROUND_FOCUS, BACKGROUND_FOCUS, null);

  private final TextField text;

  private State mainPhotoState = State.None;
  private State authorPhotoState = State.None;
  private AbstractBitmapField mainPhoto = null;
  private AbstractBitmapField authorPhoto = null;

  private Message message;

  private final String date;
  private final String title;
  private boolean showMyPhoto = false;
  private int id;

  public DialogItem(Message message) {
    super(Field.USE_ALL_WIDTH, DialogItem.BACKGROUND_THEME);
    this.message = message;
    setFont(Fonts.narrow(DP7 - DP1 / 2, Ui.UNITS_px));

    date = HappyDate.getStringDate(message.getDate());
    if (message.isFromChat()) {
      title = message.getTitle();
    } else if (message.getUser() != null) {
      title = message.getUser().getFullName();
    } else title = "";

    int textColor = (message.getBody().length() > 0) ?
        BLACK_THEME.getPrimaryColor() : BLUE_THEME.getPrimaryColor();

    text = new TextField();
    text.setFont(Fonts.narrow(DP7 - DP1 / 2, Ui.UNITS_px));
    text.setText(getContentString());
    text.setColor(textColor);

    showMyPhoto =
        (message.isOut()) ||
            (message.getChatActiveUsers() != null && message.getUser() != null);
  }

  private String getContentString() {
    if (message.getBody().length() > 0) {
      String content = message.getBody();
      int nlIndex = content.indexOf("\n");
      return nlIndex > 0 ? content.substring(0, nlIndex).trim() : content;
    } else {
      int attachmentSize = message.getAttachments().size();
      int forwardedSize = message.getForwardedMessages().size();

      if (attachmentSize > 0) {
        return CountHelper.attachmentsString(attachmentSize);
      } else if (forwardedSize > 0) {
        return CountHelper.forwardedMessagesString(forwardedSize);
      } else if (message.getGeo() != null) {
        return tr(VikaResource.Location);
      }
    }
    return "";
  }

  public boolean equals(Object obj) {
    if (obj instanceof DialogItem) {
      return ((DialogItem) obj).getMessage().equals(message);
    } else return false;
  }

  public boolean filter(String filter) {
    return true;
  }

  public int getId() {
    return id;
  }

  public Message getMessage() {
    return message;
  }

  public int getPreferredHeight() {
    return DialogItem.HEIGHT;
  }

  public int getPreferredWidth() {
    return Integer.MAX_VALUE;
  }

  // uiThread
  private void loadMainPhoto() {
    if (mainPhotoState != State.None) {
      return;
    }

    if (message.isFromChat()) {
      String urls[] = new String[message.getChatActiveUsers().size()];
      for (int i = 0; i < urls.length; ++i) {
        urls[i] = message.getChatActiveUsers().get(i).getPhotoURL();
      }
      ImageLoader.instance.load(urls, "user", MAIN_PHOTO_SIZE, this);
    } else {
      String url = message.getUser().getPhotoURL();
      ImageLoader.instance.load(url, "user", MAIN_PHOTO_SIZE, this);
    }
  }

  // uiThread
  private void loadMyPhoto() {
    String url = getMyPhotoUrl();
    if (url == null || authorPhotoState != State.None) {
      return;
    }

    authorPhotoState = State.Loading;
    ImageLoader.instance.load(url, "author", MY_PHOTO_SIZE, this);
  }

  private String getMyPhotoUrl() {
    if (message.getChatActiveUsers() != null && message.getUser() != null) {
      return message.getUser().getPhotoURL();
    } else if (message.getMyUser() != null) {
      return message.getMyUser().getPhotoURL();
    } else return null;
  }

  public void onError(String url, String tag) {
    if (tag.equals("user")) {
      mainPhotoState = State.Error;
    } else if (tag.equals("author")) {
      authorPhotoState = State.Error;
    }
  }

  public void onError(String[] url, String tag) {
    mainPhotoState = State.Error;
  }

  public void onLoad(String url, String tag, Bitmap bmp) {
    if (tag.equals("user")) {
      mainPhoto = new AbstractBitmapField(bmp, MAIN_PHOTO_SIZE, false);
      mainPhotoState = State.Complete;
    } else if (tag.equals("author")) {
      authorPhoto = new AbstractBitmapField(bmp, MY_PHOTO_SIZE, false);
      authorPhotoState = State.Complete;
    }
    invalidate();
  }

  public void onLoad(String[] url, String tag, Bitmap bmp) {
    mainPhoto = new AbstractBitmapField(bmp, MAIN_PHOTO_SIZE, false);
    mainPhotoState = State.Complete;
    invalidate();
  }

  protected void paint(Graphics g, XYRect rect) {
    int oldColor = g.getColor();
    try {
      paintPhotos(g, rect);
      Font f = g.getFont();

      int dateWidth = f.getAdvance(date);
      boolean myUnread = message.isOut() && !message.isRead();

      g.setColor(0);
      g.drawText(date,
          rect.x + rect.width - dateWidth,
          rect.y + (rect.height - MY_PHOTO_SIZE.height - f.getHeight()) / 2);

      TextDrawHelper.drawEllipsizedString(title, g,
          rect.x + MAIN_PHOTO_SIZE.width + PADDING,
          rect.y + (rect.height - MY_PHOTO_SIZE.height - f.getHeight()) / 2,
          rect.width - dateWidth - MAIN_PHOTO_SIZE.width - PADDING * 3);

      int textX = rect.x + MAIN_PHOTO_SIZE.width + PADDING
          + (showMyPhoto ? MY_PHOTO_SIZE.width + PADDING : 0)
          + (myUnread ? PADDING : 0);
      int textY = rect.y + rect.height - MY_PHOTO_SIZE.height
          + (MY_PHOTO_SIZE.height - f.getHeight()) / 2;

      int textWidth = rect.width - MAIN_PHOTO_SIZE.width - PADDING
          - (showMyPhoto ? MY_PHOTO_SIZE.width + PADDING : 0)
          - (myUnread ? PADDING * 2 : 0);

      g.translate(textX, textY);
      text.sublayout(textWidth, Integer.MAX_VALUE);

      if (message.isOut() && !message.isRead()) {
        XYRect firstLineRect = text.getFirstLineRect();
        firstLineRect.width = text.paintFirstLine(g, textWidth);
        XYRect notReadRect = new XYRect(
            firstLineRect.x - PADDING, firstLineRect.y - PADDING,
            firstLineRect.width + PADDING * 2, firstLineRect.height + PADDING * 2);
        Background b = isFocused() ?
            BACKGROUND_NOTREAD_FOCUSED : BACKGROUND_NOTREAD_DEFAULT;
        b.draw(g, notReadRect);
      }
      text.paintFirstLine(g, textWidth);
      g.translate(-textX, -textY);

    } finally {
      g.setColor(oldColor);
    }
  }

  private static XYRect xyRect(int x, int y, XYDimension dimension) {
    return new XYRect(x, y, dimension.width, dimension.height);
  }

  private void paintPhotos(Graphics g, XYRect rect) {
    if (mainPhoto == null) {
      DialogItem.ABITMAP_MAIN_PHOTO.draw(g, xyRect(rect.x, rect.y, MAIN_PHOTO_SIZE));
      if (mainPhotoState == State.None) {
        loadMainPhoto();
      }
    } else {
      mainPhoto.draw(g, xyRect(rect.x, rect.y, MAIN_PHOTO_SIZE));
    }

    if (showMyPhoto) {
      int x = rect.x + DialogItem.MAIN_PHOTO_SIZE.width + DialogItem.PADDING, y = rect.y
          + rect.height
          - MY_PHOTO_SIZE.height;
      if (authorPhoto == null) {
        DialogItem.ABITMAP_MY_PHOTO.draw(g, xyRect(x, y, MY_PHOTO_SIZE));
        if (authorPhotoState == State.None) {
          loadMyPhoto();
        }
      } else {
        authorPhoto.draw(g, xyRect(x, y, MY_PHOTO_SIZE));
      }
    }
  }

  protected void paintBackground(Graphics g) {
    if (!isActive() && !isFocused()) {
      if (!message.isRead() && !message.isOut()) {
        g.setColor(0xecf0f5);
        g.fillRect(0, 0, getBackgroundRect().width, getBackgroundRect().height);
      }
    } else super.paintBackground(g);
  }

}
