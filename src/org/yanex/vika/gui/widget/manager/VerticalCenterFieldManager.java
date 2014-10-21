package org.yanex.vika.gui.widget.manager;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import org.yanex.vika.gui.screen.VkMainScreen;

public class VerticalCenterFieldManager extends Manager {

  public VerticalCenterFieldManager() {
    super(0);
  }

  public VerticalCenterFieldManager(long style) {
    super(style);
  }

  protected void sublayout(int width, int height) {
    int i, totalHeight = 0;

    int hpaddings = getPaddingLeft() + getPaddingRight();
    int vpaddings = getPaddingTop() + getPaddingBottom();

    totalHeight = vpaddings;

    for (i = 0; i < getFieldCount(); ++i) {
      Field f = getField(i);
      int hmargins = f.getMarginLeft() + f.getMarginRight(), vmargins = f.getMarginTop()
          + f.getMarginBottom();
      layoutChild(f, width - hmargins, height);
      totalHeight += f.getHeight() + vmargins;
    }

    int bannerHeight = 0;
    int titleHeight = 0;
    if (getScreen() != null && getScreen() instanceof VkMainScreen) {
      VkMainScreen screen = (VkMainScreen) getScreen();
      if (screen.getBanner() != null) {
        bannerHeight = Math.max(screen.getBanner().getHeight(), screen.getBanner()
            .getPreferredHeight());
      }
      if (screen.getTitle() != null) {
        titleHeight = Math.max(screen.getTitle().getHeight(), screen.getBanner()
            .getPreferredHeight());
      }
    }

    int screenHeight = Display.getHeight() - bannerHeight - titleHeight;
    int screenWidth = Math.min(width, Display.getWidth()) - hpaddings;
    int y = totalHeight > screenHeight ? 0 : (screenHeight - totalHeight) / 2;
    y += getPaddingTop();
    int x = 0;

    for (i = 0; i < getFieldCount(); ++i) {
      Field f = getField(i);
      int hmargins = f.getMarginLeft() + f.getMarginRight(), vmargins = f.getMarginTop()
          + f.getMarginBottom();
      x = f.getMarginLeft();
      if ((f.getStyle() & Field.FIELD_HCENTER) > 0) {
        x = (screenWidth - f.getWidth() - hmargins) / 2 + f.getMarginLeft() + getMarginLeft();
      }
      setPositionChild(f, x, y);
      y += f.getHeight() + vmargins;
    }

    setExtent(width, Math.max(totalHeight, screenHeight));
  }

}
