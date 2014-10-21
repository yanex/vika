package org.yanex.vika.gui.util;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.decor.Background;

public class RoundedBackground extends Background {

  private int color;
  private int borderColor;
  private int arc;
  private boolean drawBorder = false;
  private XYEdges padding = new XYEdges();

  public RoundedBackground(int color) {
    this(color, 3);
  }

  public RoundedBackground(int color, int arc) {
    this.color = color;
    this.arc = arc;
  }

  public RoundedBackground(int color, int arc, boolean drawBorder, int borderColor) {
    this.color = color;
    this.arc = arc;
    this.drawBorder = drawBorder;
    this.borderColor = borderColor;
  }

  public void draw(Graphics g, XYRect r) {
    int x = r.x + padding.left;
    int y = r.y + padding.top;
    int width = r.width - padding.left - padding.right;
    int height = r.height - padding.top - padding.bottom;

    XYRect rect = new XYRect(x, y, width, height);

    int oldColor = g.getColor();
    g.setColor(color);
    g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, arc, arc);
    if (drawBorder) {
      g.setColor(borderColor);
      g.drawRoundRect(rect.x, rect.y, rect.width, rect.height, arc, arc);
    }
    g.setColor(oldColor);
  }

  public int getBorderColor() {
    return borderColor;
  }

  public int getColor() {
    return color;
  }

  public XYEdges getPadding() {
    return padding;
  }

  public boolean isTransparent() {
    return true;
  }

  public void setPadding(XYEdges padding) {
    if (padding != null) {
      this.padding = padding;
    }
  }

}
