package org.yanex.vika.gui.widget;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import org.yanex.vika.gui.util.Fonts;
import org.yanex.vika.gui.util.R;

public class VkConversationUserField extends Field {

  private String name;
  private String status;

  private static int INTERVAL = R.px(2);

  private int nameWidth;
  private int statusWidth;
  private int nameHeight = VkConversationUserField.fName.getHeight();
  private int statusHeight = VkConversationUserField.fStatus.getHeight();

  private static final Font fName = Fonts.bold(7);
  private static final Font fStatus = Fonts.bold(6);

  private int width = Integer.MAX_VALUE;

  public VkConversationUserField() {

  }

  public VkConversationUserField(long style) {
    super(style);
  }

  public String getName() {
    return name;
  }

  public int getPreferredHeight() {
    return R.px(12);
  }

  public int getPreferredWidth() {
    return width;
  }

  public String getStatus() {
    return status;
  }

  protected void layout(int width, int height) {
    setExtent(Math.min(width, getPreferredWidth()), Math.min(height, getPreferredHeight()));
  }

  protected void paint(Graphics g) {
    if (status == null || status.length() == 0) {
      g.setFont(VkConversationUserField.fName);
      g.setColor(0xFFFFFF);
      g.drawText(name, 0, (getContentHeight() - nameHeight) / 2);
    } else {
      if (nameWidth + VkConversationUserField.INTERVAL + statusWidth > getContentWidth()) { // 2
        // lines
        g.setFont(VkConversationUserField.fName);
        g.setColor(0xFFFFFF);
        g.drawText(name, 0, 0);

        g.setFont(VkConversationUserField.fStatus);
        g.setColor(0x666666);
        g.drawText(status, 0, getContentHeight() - statusHeight);
      } else { // 1 line
        g.setFont(VkConversationUserField.fName);
        g.setColor(0xFFFFFF);
        g.drawText(name, 0, (getContentHeight() - nameHeight) / 2);

        g.setFont(VkConversationUserField.fStatus);
        g.setColor(0x666666);
        g.drawText(status, nameWidth + VkConversationUserField.INTERVAL, (getContentHeight() - 2
            * statusHeight + nameHeight) / 2);
      }
    }
  }

  public void setName(String name) {
    this.name = name;
    if (name != null && name.length() > 0) {
      nameWidth = VkConversationUserField.fName.getAdvance(name);
    } else {
      nameWidth = 0;
    }
    invalidate();
  }

  public void setStatus(String status) {
    this.status = status;
    if (status != null && status.length() > 0) {
      statusWidth = VkConversationUserField.fStatus.getAdvance(status);
    } else {
      statusWidth = 0;
    }
    invalidate();
  }

  public void setWidth(int width) {
    this.width = width;
  }

}
