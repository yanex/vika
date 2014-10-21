package org.yanex.vika.gui.list.item;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import org.yanex.vika.gui.util.Theme;

public class NullItem extends ListItem {

  private static final Theme THEME = new Theme();

  public NullItem() {
    super(THEME);
  }

  public boolean filter(String filter) {
    return true;
  }

  public int getPreferredHeight() {
    return 0;
  }

  public int getPreferredWidth() {
    return Integer.MAX_VALUE;
  }

  public boolean isFocusable() {
    return false;
  }

  protected void paint(Graphics g, XYRect rect) {
    // empty
  }

}
