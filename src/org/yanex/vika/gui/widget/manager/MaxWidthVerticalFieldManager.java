package org.yanex.vika.gui.widget.manager;

import net.rim.device.api.ui.container.VerticalFieldManager;

public class MaxWidthVerticalFieldManager extends VerticalFieldManager {

  private int maxWidth = -1;

  public int getMaxWidth() {
    return maxWidth;
  }

  public int getPreferredWidth() {
    if (maxWidth > 0) {
      return maxWidth;
    } else {
      return super.getPreferredWidth();
    }
  }

  public void setMaxWidth(int value) {
    maxWidth = value;
  }

  protected void sublayout(int maxWidth, int maxHeight) {
    if (this.maxWidth > 0) {
      super.sublayout(this.maxWidth, maxHeight);
    } else {
      super.sublayout(maxWidth, maxHeight);
    }
  }
}
