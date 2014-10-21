package org.yanex.vika.gui.widget;

import com.mobiata.bb.ui.decor.NinePatchBackground;
import net.rim.device.api.ui.decor.Background;
import org.yanex.vika.gui.util.Files;
import org.yanex.vika.gui.util.Theme;
import org.yanex.vika.gui.widget.base.PaneCaptionField;

public class VkPaneCaptionField extends PaneCaptionField {

  private static final Background BACKGROUND =
      new NinePatchBackground(Files.TABS_FRIENDSTABBAR_BG);
  private static final Background BACKGROUND_FOCUS =
      new NinePatchBackground(Files.TABS_FRIENDSTABBAR_HOVER_BG);

  private static final Theme THEME = new Theme()
      .setPrimaryColor(0x676767)
      .setSecondaryFontColor(0xFFFFFF)
      .setBackground(BACKGROUND, BACKGROUND_FOCUS, BACKGROUND_FOCUS, null);

  public VkPaneCaptionField() {
    super(VkPaneCaptionField.THEME);
  }

}
