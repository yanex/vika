package org.yanex.vika.gui.widget.base;

import net.rim.device.api.ui.*;
import org.yanex.vika.gui.util.Fonts;
import org.yanex.vika.gui.util.Theme;

public class MultiFontButtonField extends FocusableField {

  private int fixedWidth = -1;

  private Font font1 = Fonts.defaultFont, font2 = Fonts.defaultFont;
  private String text1, text2;

  public MultiFontButtonField(String text1, String text2, long style, Theme theme) {
    super(style, theme);

    this.text1 = text1;
    this.text2 = text2;
  }

  public Font getFont1() {
    return font1;
  }

  public Font getFont2() {
    return font2;
  }

  public int getPreferredHeight() {
    XYEdges b = getTheme().getBorderEdges();

    return b.top + Math.max(font1.getHeight(), font2.getHeight()) + b.bottom + getPaddingBottom()
        + getPaddingTop();
  }

  public int getPreferredWidth() {
    if (fixedWidth > 0) {
      return fixedWidth;
    }

    XYEdges b = getTheme().getBorderEdges();

    if (isStyle(Field.USE_ALL_WIDTH)) {
      return Integer.MAX_VALUE;
    } else {
      return b.left + font1.getBounds(text1) + font2.getBounds(text2) + b.right + getPaddingLeft()
          + getPaddingRight();
    }
  }

  public String getText1() {
    return text1;
  }

  public String getText2() {
    return text2;
  }

  protected void paint(Graphics g) {
    int oldColor = g.getColor();
    try {
      if (isFocused() || isActive()) {
        g.setColor(getTheme().getSecondaryFontColor());
      } else {
        g.setColor(getTheme().getPrimaryColor());
      }

      int w1 = font1.getAdvance(text1);
      int y1 = (getContentHeight() - font1.getHeight()) / 2;
      int y2 = (getContentHeight() - font2.getHeight()) / 2;

      int w = w1 + font2.getAdvance(text2);

      int x = (getContentWidth() - w) / 2;

      g.setFont(font1);
      g.drawText(text1, x, y1, DrawStyle.LEFT + DrawStyle.TOP);

      g.setFont(font2);
      g.drawText(text2, x + w1, y2, DrawStyle.LEFT + DrawStyle.TOP);
    } finally {
      g.setColor(oldColor);
    }
  }

  public void setFixedWidth(int width) {
    fixedWidth = width;
    updateLayout();
  }

  public void setFont1(Font font1) {
    this.font1 = font1;
    updateLayout();
  }

  public void setFont2(Font font2) {
    this.font2 = font2;
    updateLayout();
  }

  public void setText1(String text1) {
    this.text1 = text1;
    updateLayout();
  }

  public void setText2(String text2) {
    this.text2 = text2;
    updateLayout();
  }
}
