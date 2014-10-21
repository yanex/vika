package org.yanex.vika.gui.widget.base;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;

public class SeparatorField extends Field {

  private final int height;
  private final int color;

  public SeparatorField() {
    super();
    this.color = 0;
    this.height = 1;
  }

  public SeparatorField(int height, int color) {
    super();
    this.color = color;
    this.height = height;
  }

  public int getPreferredHeight() {
    return height;
  }

  public int getPreferredWidth() {
    return Integer.MAX_VALUE;
  }

  protected void layout(int width, int height) {
    setExtent(Math.min(width, getPreferredWidth()), Math.min(height, getPreferredHeight()));
  }

  protected void paint(Graphics g) {
    int oldColor = g.getColor();
    try {
      g.setColor(color);
      g.fillRect(0, 0, getWidth(), getHeight());
    } finally {
      g.setColor(oldColor);
    }
  }

  protected void paintBackground(Graphics g) {

  }

}
