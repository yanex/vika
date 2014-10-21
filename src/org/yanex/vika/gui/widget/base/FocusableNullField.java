package org.yanex.vika.gui.widget.base;

import net.rim.device.api.ui.component.NullField;

public class FocusableNullField extends NullField {

  private boolean focusable = true;

  // public
  public void fieldChangeNotify(int context) {
    super.fieldChangeNotify(context);
  }

  public boolean isFocusable() {
    return focusable && super.isFocusable();
  }

  public void setFocusable(boolean focusable) {
    this.focusable = focusable;
  }

}
