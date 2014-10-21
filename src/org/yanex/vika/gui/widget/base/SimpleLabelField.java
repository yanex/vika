package org.yanex.vika.gui.widget.base;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYEdges;
import org.yanex.vika.gui.util.Fonts;
import org.yanex.vika.gui.util.TextDrawHelper;
import org.yanex.vika.gui.util.Theme;

public class SimpleLabelField extends Field {

  private String text = "";
  private final Theme theme;

  private int preferredWidth = -1, preferredHeight = -1;

  public SimpleLabelField(String text, long style, Font font, Theme theme) {
    super(style);
    this.text = text;
    this.theme = theme;
    setPadding(theme.getPaddingEdges());
    setFont(font);
  }

  public SimpleLabelField(String text, long style, Theme theme) {
    this(text, style, Fonts.defaultFont, theme);
  }

  public SimpleLabelField(String text, Theme theme) {
    this(text, 0, Fonts.defaultFont, theme);
  }

  public int getPreferredHeight() {
    if (preferredHeight < 0) {
      preferredHeight = getFont().getHeight() + getPaddingTop() + getPaddingBottom();
    }
    return preferredHeight;
  }

  public int getPreferredWidth() {
    if (preferredWidth < 0) {
      preferredWidth = getFont().getAdvance(text) + getPaddingLeft() + getPaddingRight();
    }
    return preferredWidth;
  }

  public String getText() {
    return text;
  }

  public Theme getTheme() {
    return theme;
  }

  protected void layout(int width, int height) {
    setExtent(Math.min(width, getPreferredWidth()), Math.min(height, getPreferredHeight()));
  }

  protected void paint(Graphics g) {
    int oldColor = g.getColor();
    try {
      g.setColor(theme.getPrimaryColor());
      TextDrawHelper.drawEllipsizedString(text, g, 0, 0, getWidth());
    } finally {
      g.setColor(oldColor);
    }
  }

  public void setPadding(int top, int right, int bottom, int left) {
    super.setPadding(top, right, bottom, left);
    preferredHeight = preferredWidth = -1;
  }

  public void setPadding(XYEdges padding) {
    super.setPadding(padding);
    preferredHeight = preferredWidth = -1;
  }

  public void setText(String text) {
    this.text = text;
    preferredHeight = preferredWidth = -1;
    updateLayout();
  }

}
