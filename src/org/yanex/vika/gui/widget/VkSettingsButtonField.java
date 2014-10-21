package org.yanex.vika.gui.widget;

import com.mobiata.bb.ui.decor.NinePatchBackground;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.decor.Background;
import org.yanex.vika.gui.util.Theme;
import org.yanex.vika.gui.widget.base.ButtonField;

public class VkSettingsButtonField extends ButtonField {

  private static final Background BACKGROUND =
      new NinePatchBackground("Other/ContactsSyncButton.png");
  private static final Background BACKGROUND_FOCUS =
      new NinePatchBackground("Other/ContactsSyncButtonPushed.png");

  private static final Theme THEME = new Theme()
      .setPaddingEdges(DP1 * 2 / 3, 0, DP1 * 2 / 3, 0)
      .setPrimaryColor(0xffffff)
      .setSecondaryFontColor(0xffffff)
      .setBackground(BACKGROUND, BACKGROUND_FOCUS, BACKGROUND_FOCUS, null);

  public VkSettingsButtonField(String text) {
    super(text, Field.FIELD_HCENTER | Field.USE_ALL_WIDTH, VkSettingsButtonField.THEME);
  }

}
