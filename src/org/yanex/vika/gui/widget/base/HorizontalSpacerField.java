package org.yanex.vika.gui.widget.base;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;

public class HorizontalSpacerField extends Field {

  private final int width;

  public HorizontalSpacerField(int width) {
    super();
    this.width = width;
  }

  public int getPreferredHeight() {
    return isStyle(Field.USE_ALL_WIDTH) ? Integer.MAX_VALUE : 1;
  }

  public int getPreferredWidth() {
    return width;
  }

  protected void layout(int width, int height) {
    int w = Math.min(width, getPreferredWidth());
    int h = Math.min(height, getPreferredHeight());
    setExtent(w, h);
  }

  protected void paint(Graphics g) {
    // empty
  }

  protected void paintBackground(Graphics g) {
    // empty
  }

}
