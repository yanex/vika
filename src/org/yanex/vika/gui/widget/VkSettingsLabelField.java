package org.yanex.vika.gui.widget;

import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import org.yanex.vika.gui.util.Fonts;
import org.yanex.vika.gui.util.Theme;
import org.yanex.vika.gui.widget.base.CustomLabelField;

public class VkSettingsLabelField extends CustomLabelField {

  private static final Theme THEME = new Theme().setPrimaryColor(0x666666);

  public VkSettingsLabelField(String label) {
    super(label, Field.USE_ALL_WIDTH | DrawStyle.HCENTER, VkSettingsLabelField.THEME);
    setMargin(CustomLabelField.px(4), CustomLabelField.px(5), CustomLabelField.px(4),
        CustomLabelField.px(5));
    setFont(Fonts.narrow(7));
  }

}
