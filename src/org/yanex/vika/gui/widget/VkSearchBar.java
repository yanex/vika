package org.yanex.vika.gui.widget;

import com.mobiata.bb.ui.decor.NinePatchBackground;
import net.rim.device.api.ui.container.VerticalFieldManager;
import org.yanex.vika.gui.widget.base.EditTextField;

public class VkSearchBar extends VerticalFieldManager {

  public VkSearchBar(EditTextField text) {
    setBackground(new NinePatchBackground("Lists/Contacts/SearchLineBg.png"));
    add(text);
  }

}
