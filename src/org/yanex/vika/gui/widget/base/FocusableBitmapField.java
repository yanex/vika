package org.yanex.vika.gui.widget.base;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.BitmapField;

public class FocusableBitmapField extends BitmapField {

  private boolean disableFocusOnLost = true;
  private boolean focusable = true;

  public FocusableBitmapField() {

  }

  public FocusableBitmapField(Bitmap bitmap, long style) {
    super(bitmap, style);
  }

  protected void drawFocus(Graphics g, boolean on) {

  }

  public boolean isDisableFocusOnLost() {
    return disableFocusOnLost;
  }

  public boolean isFocusable() {
    return focusable;
  }

  protected void onUnfocus() {
    super.onUnfocus();
    if (disableFocusOnLost) {
      focusable = false;
    }
  }

  public void setDisableFocusOnLost(boolean disableFocusOnLost) {
    this.disableFocusOnLost = disableFocusOnLost;
  }
}
