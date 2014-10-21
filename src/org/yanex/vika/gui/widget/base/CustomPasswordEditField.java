package org.yanex.vika.gui.widget.base;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.decor.Background;
import org.yanex.vika.gui.util.Theme;

public class CustomPasswordEditField extends PasswordEditField {

  private Theme theme;
  private boolean isFocused = false;

  private Bitmap leftBitmap;
  private Bitmap rightBitmap;

  private int leftBitmapWidth = 0;
  private int rightBitmapWidth = 0;

  private String hint = "";
  private String text = "";
  private boolean displayingHint = false;

  private XYRect borderRect = new XYRect();

  public CustomPasswordEditField(Theme theme) {
    super();
    this.theme = theme;
    setPadding(theme.getPaddingEdges());
    setMargin(theme.getBorderEdges());
  }

  private Background getCurrentBackground() {
    if (isFocused) {
      return theme.getFocusBackground();
    } else {
      return theme.getDefaultBackground();
    }
  }

  public String getHint() {
    return hint;
  }

  public String getRealText() {
    return text;
  }

  public Theme getTheme() {
    return theme;
  }

  private void initBitmaps() {
    int left = getPaddingLeft() - leftBitmapWidth, top = getPaddingTop(), right = getPaddingRight()
        - rightBitmapWidth, bottom = getPaddingBottom();

    if (leftBitmap == null) {
      leftBitmapWidth = 0;
    } else {
      leftBitmapWidth = leftBitmap.getWidth() + left;
    }

    if (rightBitmap == null) {
      rightBitmapWidth = 0;
    } else {
      rightBitmapWidth = rightBitmap.getWidth() + left;
    }

    left += leftBitmapWidth;
    right += rightBitmapWidth;

    setPadding(top, right, bottom, left);
  }

  protected void layout(int width, int height) {
    super.layout(width, height);

    borderRect.set(0, 0, getWidth(), getHeight());
  }

  protected void onFocus(int direction) {
    super.onFocus(direction);
    isFocused = true;

    if (displayingHint) {
      // setText("");
      displayingHint = false;
    }

    invalidate();
  }

  protected void onUnfocus() {
    super.onUnfocus();
    isFocused = false;

    text = getText();
    if (text == null || text.length() == 0) {
      displayingHint = true;
      // setText(hint);
    } else {
      displayingHint = false;
    }

    invalidate();
  }

  protected void paint(Graphics g) {
    int oldColor = g.getColor();

    if (displayingHint) {
      g.setColor(theme.getSecondaryFontColor());
      g.drawText(hint, 0, 0);
    } else {
      g.setColor(theme.getPrimaryColor());
      super.paint(g);
    }

    g.setColor(oldColor);
  }

  protected void paintBackground(Graphics g) {
    Background currentBackground = getCurrentBackground();

    if (currentBackground != null) {
      currentBackground.draw(g, borderRect);
    }

    XYEdges padding = getTheme().getPaddingEdges();
    int height = getContentHeight();
    int width = getWidth() - getPaddingRight();

    if (leftBitmap != null) {
      int y = padding.top + (height - leftBitmap.getHeight()) / 2;
      g.drawBitmap(padding.left, y, leftBitmap.getWidth(), leftBitmap.getHeight(),
          leftBitmap, 0, 0);
    }

    if (rightBitmap != null) {
      int y = padding.top + (height - rightBitmap.getHeight()) / 2;
      int x = width - rightBitmap.getWidth();
      g.drawBitmap(x, y, getContentWidth(), getContentHeight(), rightBitmap, 0, 0);
    }
  }

  public void setBitmaps(Bitmap leftBitmap, Bitmap rightBitmap) {
    if (this.leftBitmap == leftBitmap && this.rightBitmap == rightBitmap) {
      return;
    }

    this.leftBitmap = leftBitmap;
    this.rightBitmap = rightBitmap;

    initBitmaps();
    invalidate();
  }

  public void setHint(String hint) {
    this.hint = hint;

    if (text == null || text.length() == 0) {
      displayingHint = true;
      // setText(hint);
    } else {
      displayingHint = false;
    }
  }
}
