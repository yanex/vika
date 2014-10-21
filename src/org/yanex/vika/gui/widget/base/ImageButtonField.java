package org.yanex.vika.gui.widget.base;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYDimension;
import net.rim.device.api.ui.XYEdges;
import org.yanex.vika.gui.util.Theme;

public class ImageButtonField extends FocusableField {

  private AbstractBitmapField bitmap;

  private int imageWidth;
  private int imageHeight;

  private int fullWidth;
  private int fullHeight;

  private boolean scale;

  public ImageButtonField(Bitmap bmp, int width, int height, long style, Theme theme, boolean scale) {
    super(style, theme);

    XYEdges padding = theme.getPaddingEdges();

    fullWidth = width;
    imageWidth = fullWidth - padding.left - padding.right;
    fullHeight = height;
    imageHeight = fullHeight - padding.top - padding.bottom;

    this.scale = scale;

    this.bitmap = new AbstractBitmapField(bmp, new XYDimension(imageWidth, imageHeight), scale);
  }

  public int getPreferredHeight() {
    XYEdges b = getTheme().getBorderEdges();

    return b.top + fullHeight + b.bottom;
  }

  public int getPreferredWidth() {
    XYEdges b = getTheme().getBorderEdges();

    if (isStyle(Field.USE_ALL_WIDTH)) {
      return Integer.MAX_VALUE;
    } else {
      return b.left + fullWidth + b.right;
    }
  }

  protected void paint(Graphics g) {
    if (bitmap != null) {
      bitmap.draw(g, 0, 0, getContentWidth(), getContentHeight());
    }
  }

  public void setBitmap(Bitmap b) {
    XYEdges padding = getTheme().getPaddingEdges();
    imageWidth = fullWidth - padding.left - padding.right;
    imageHeight = fullHeight - padding.top - padding.bottom;
    bitmap = new AbstractBitmapField(b, new XYDimension(imageWidth, imageHeight), scale);
    invalidate();
  }
}
