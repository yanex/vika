package org.yanex.vika.gui.list.item;

import com.mobiata.bb.ui.decor.NinePatchBackground;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;
import org.yanex.vika.gui.util.GuiItem;
import org.yanex.vika.gui.util.R;
import org.yanex.vika.gui.util.Theme;
import org.yanex.vika.gui.widget.base.ButtonField;
import org.yanex.vika.gui.widget.base.CustomLabelField;

public class ButtonItem extends VerticalFieldManager implements AbstractListItem, GuiItem {

  private int id;
  private ItemPaintListener itemPaintListener;
  private ItemListener itemListener;

  private static Background BACKGROUND =
      BackgroundFactory.createBitmapBackground(R.instance.getBitmap("LightBg.png"));

  public ButtonItem(String text) {
    this(text, null);
  }

  public ButtonItem(String text, String label) {
    super(Field.USE_ALL_WIDTH | Field.USE_ALL_HEIGHT);

    setBackground(ButtonItem.BACKGROUND);

    if (label != null) {
      Theme labelTheme = new Theme();
      labelTheme.setPrimaryColor(0x666666);
      labelTheme.setPaddingEdges(2, 3, 2, 3);

      add(new CustomLabelField(label, Field.FIELD_HCENTER | DrawStyle.HCENTER, labelTheme));
    }

    Theme buttonTheme = new Theme();
    Background bgDefault = new NinePatchBackground("Other/ContactsSyncButton.png");
    Background bgFocus = new NinePatchBackground("Other/ContactsSyncButtonPushed.png");
    buttonTheme.setPaddingEdges(DP1, DP3, DP1, DP3);
    buttonTheme.setPrimaryColor(0xffffff);
    buttonTheme.setSecondaryFontColor(0xffffff);
    buttonTheme.setBackground(bgDefault, bgFocus, bgFocus, null);

    ButtonField b = new ButtonField(text, Field.FIELD_HCENTER, buttonTheme);
    b.setChangeListener(new FieldChangeListener() {

      public void fieldChanged(Field field, int context) {
        if (itemListener != null) {
          itemListener.itemClick(id, ButtonItem.this);
        }
      }
    });

    add(b);
  }

  public boolean filter(String filter) {
    return filter == null || filter.length() == 0;
  }

  public int getId() {
    return id;
  }

  public ItemListener getItemListener() {
    return itemListener;
  }

  public ItemPaintListener getItemPaintListener() {
    return itemPaintListener;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setItemListener(ItemListener itemListener) {
    this.itemListener = itemListener;
  }

  public void setItemPaintListener(ItemPaintListener itemPaintListener) {
    this.itemPaintListener = itemPaintListener;
  }

}
