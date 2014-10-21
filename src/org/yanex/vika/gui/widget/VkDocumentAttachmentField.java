package org.yanex.vika.gui.widget;

import com.mobiata.bb.ui.decor.NinePatchBackground;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.decor.Background;
import org.yanex.vika.api.item.DocumentAttachment;
import org.yanex.vika.gui.screen.VkMainScreen;
import org.yanex.vika.gui.util.R;
import org.yanex.vika.gui.util.Theme;
import org.yanex.vika.gui.widget.base.ImageTextButtonField;
import org.yanex.vika.local.VikaResource;
import org.yanex.vika.util.bb.Blackberry;

public class VkDocumentAttachmentField extends ImageTextButtonField implements FieldChangeListener {

  private static final Theme theme;

  private static final Bitmap IMAGE = R.instance.getBitmap("Convs/DocIcon.png");

  private int forceWidth = -1;

  static {
    theme = new Theme();
    VkDocumentAttachmentField.theme.setPrimaryColor(0xFFFFFF);
    VkDocumentAttachmentField.theme.setSecondaryFontColor(0xFFFFFF);

    Background defaultBackground = new NinePatchBackground("Convs/DocBg.png");
    Background focusBackground = new NinePatchBackground("Convs/DocBgFocus.png");

    VkDocumentAttachmentField.theme.setBackground(defaultBackground, focusBackground,
        focusBackground, null);

    VkDocumentAttachmentField.theme.setPaddingEdges(DP2, DP2, DP2, DP2);
  }

  private DocumentAttachment attachment;

  public VkDocumentAttachmentField(DocumentAttachment attachment) {
    super(attachment.getTitle(), VkDocumentAttachmentField.IMAGE, Field.FIELD_HCENTER,
        VkDocumentAttachmentField.theme);
    this.attachment = attachment;
    setChangeListener(this);
  }

  public void fieldChanged(Field f, int arg1) {
    if (!Blackberry.launch(attachment.getUrl())) {
      Dialog.alert(VkMainScreen.tr(VikaResource.UNABLE_TO_OPEN_ATTACHMENT));
    }
  }

  public int getForceWidth() {
    return forceWidth;
  }

  public int getPreferredWidth() {
    if (forceWidth > 0) {
      return forceWidth;
    } else {
      return super.getPreferredWidth();
    }
  }

  public void setForceWidth(int forceWidth) {
    this.forceWidth = forceWidth;
  }

}
