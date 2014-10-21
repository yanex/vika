package org.yanex.vika.gui.widget;

import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.decor.Background;
import org.yanex.vika.gui.util.Fonts;
import org.yanex.vika.gui.util.GradientBackground;
import org.yanex.vika.gui.util.Theme;
import org.yanex.vika.gui.widget.base.CustomLabelField;

public class VkTitleField extends CustomLabelField {

  private static final Background BACKGROUND =
      new GradientBackground(0x1e1e1e, 0x131313, 1, 0x000000, 0, 0x000000);

  private static final Theme THEME = new Theme()
      .setPrimaryColor(0xFFFFFF)
      .setPaddingEdges(DP5, DP5, DP4, DP5)
      .setBackground(BACKGROUND, null, null, null);

  public VkTitleField(Object text) {
    super(text, Field.USE_ALL_WIDTH | DrawStyle.ELLIPSIS, VkTitleField.THEME);
    setFont(Fonts.narrow(7));
  }
}
