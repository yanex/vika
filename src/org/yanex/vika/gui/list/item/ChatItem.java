package org.yanex.vika.gui.list.item;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.decor.Background;
import org.yanex.vika.api.item.Chat;
import org.yanex.vika.gui.util.Fonts;
import org.yanex.vika.gui.util.GradientBackground;
import org.yanex.vika.gui.util.R;
import org.yanex.vika.gui.util.Theme;
import org.yanex.vika.gui.widget.base.AbstractBitmapField;
import org.yanex.vika.util.StringUtils;
import org.yanex.vika.util.network.ImageLoader;
import org.yanex.vika.util.network.SeveralImageLoaderCallback;
import org.yanex.vika.util.network.State;

public class ChatItem extends ListItem implements SeveralImageLoaderCallback {

  private static final Background BACKGROUND_FOCUS =
      new GradientBackground(0x59a0e8, 0x1c65be);

  private final AbstractBitmapField PHOTO_DEFAULT = new AbstractBitmapField(
      R.instance.getBitmap("camera.png"), PHOTO_SIZE, true, true);

  private static final Theme THEME = new Theme()
      .setPrimaryColor(0x000000)
      .setSecondaryFontColor(0xFFFFFF)
      .setPaddingEdges(DP2, DP2, DP2, DP2)
      .setBackground(null, BACKGROUND_FOCUS, BACKGROUND_FOCUS, null);

  private static final XYDimension PHOTO_SIZE = new XYDimension(DP14, DP14);

  private final Chat chat;
  private Bitmap photo = null;
  private State photoState = State.None;

  public ChatItem(Chat chat) {
    super(ChatItem.THEME);
    this.chat = chat;
  }

  public boolean filter(String filter) {
    return filter == null || filter.length() == 0
        || StringUtils.nonRegistryContains(chat.getTitle(), filter);
  }

  public Chat getChat() {
    return chat;
  }

  public int getPreferredHeight() {
    return PHOTO_SIZE.height;
  }

  public int getPreferredWidth() {
    return Integer.MAX_VALUE;
  }

  private void loadPhoto() {
    String urls[] = new String[chat.getActiveUsers().size()];
    for (int i = 0; i < urls.length; ++i) {
      urls[i] = chat.getActiveUsers().get(i).getPhotoURL();
    }

    photoState = State.Loading;
    ImageLoader.instance.load(urls, "user", PHOTO_SIZE, this);
  }

  public void onError(String[] url, String tag) {
    photoState = State.Error;
  }

  public void onLoad(String[] url, String tag, Bitmap bmp) {
    this.photo = bmp;
    photoState = State.Complete;
    invalidate();
  }

  protected void paint(Graphics g, XYRect rect) {
    int oldColor = g.getColor(), oldAlpha = g.getGlobalAlpha(), height = rect.height;

    int color = (isActive() || isFocused()) ?
        getTheme().getSecondaryFontColor() : getTheme().getPrimaryColor();

    try {
      g.setBackgroundColor(0);
      g.setColor(0xCCCCCC);
      if (photo != null) {
        g.drawBitmap(DP1 + rect.x, rect.y, height, height, photo, 0, 0);
      } else {
        PHOTO_DEFAULT.draw(g, DP1 + rect.x, rect.y, height, height);
        if (photoState == State.None) {
          loadPhoto();
        }
      }

      Font font = Fonts.bold(DP7, Ui.UNITS_px);
      int dx = height + DP2, dy = (height - font.getHeight()) / 2;

      g.setGlobalAlpha(255);
      g.setFont(font);
      g.setColor(color);
      g.drawText(chat.getTitle(), dx + rect.x, dy + rect.y);
    } finally {
      g.setColor(oldColor);
      g.setGlobalAlpha(oldAlpha);
    }
  }

}
