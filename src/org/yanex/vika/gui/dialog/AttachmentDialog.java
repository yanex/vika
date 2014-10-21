package org.yanex.vika.gui.dialog;

import com.mobiata.bb.ui.decor.NinePatchBackground;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import org.yanex.vika.gui.util.Fonts;
import org.yanex.vika.gui.util.R;
import org.yanex.vika.gui.widget.base.CompoundButtonField;
import org.yanex.vika.local.VikaResource;

public class AttachmentDialog extends VkScreen implements FieldChangeListener {

  private static final Bitmap ICON_CAMERA = R.instance.getBitmap("Convs/AttachesMenu/Camera.png");
  private static final Bitmap ICON_PHOTO = R.instance.getBitmap("Convs/AttachesMenu/Photo.png");
  private static final Bitmap ICON_MAP = R.instance.getBitmap("Convs/AttachesMenu/Map.png");

  private CompoundButtonField camera, photo, map;

  private int selection = -1;

  public AttachmentDialog() {
    super(new VerticalFieldManager());
    setFont(Fonts.narrow(6));

    camera = new CompoundButtonField(tr(VikaResource.Take_photo), ICON_CAMERA);
    photo = new CompoundButtonField(tr(VikaResource.Choose_existing_photo), ICON_PHOTO);
    map = new CompoundButtonField(tr(VikaResource.Share_location), ICON_MAP);

    camera.setChangeListener(this);
    photo.setChangeListener(this);
    map.setChangeListener(this);

    HorizontalFieldManager hfm = new HorizontalFieldManager();
    hfm.setPadding(DP2, DP2, DP2, DP2);

    hfm.add(camera);
    hfm.add(photo);
    hfm.add(map);

    add(hfm);

    setBackground(new NinePatchBackground("Convs/AttachesMenu/Bg.png"));
  }

  public void dismiss() {
    if (isVisible()) {
      this.close();
    }
  }

  public void fieldChanged(Field field, int context) {
    if (field == camera) {
      selection = 0;
    } else if (field == photo) {
      selection = 1;
    } else if (field == map) {
      selection = 2;
    }
    dismiss();
  }

  public int getSelection() {
    return selection;
  }

  public AttachmentDialog show() {
    if (!isVisible()) {
      UiApplication.getUiApplication().pushModalScreen(this);
    }
    return this;
  }

  protected boolean keyChar(char c, int status, int time) {
    if (c == 27) {
      dismiss();
      return true;
    }

    return super.keyChar(c, status, time);
  }

  protected void sublayout(int width, int height) {
    layoutDelegate(width - 80, height - 80);

    int desiredWidth = getDelegate().getWidth() + 20;
    setExtent(Math.min(width - 60, desiredWidth), Math.min(height - 60, getDelegate().getHeight() + 20));
    setPositionDelegate((getContentWidth() - getDelegate().getWidth()) / 2, 10);
    setPosition((width - getWidth()) / 2, (height - getHeight()) / 2);
  }
}
