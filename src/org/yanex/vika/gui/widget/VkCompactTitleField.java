package org.yanex.vika.gui.widget;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.GIFEncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.decor.Background;
import org.yanex.vika.gui.util.GradientBackground;

public class VkCompactTitleField extends Field {

  protected static int px(int pt) {
    return Ui.convertSize(pt, Ui.UNITS_pt, Ui.UNITS_px);
  }

  private EncodedImage image;
  private int height;
  private int width;
  private int currentFrame = 0;

  private int frameCount;

  private int updating = 0;

  private Thread animationThread;

  private static final Background BACKGROUND = new GradientBackground(0x1e1e1e, 0x131313, 1,
      0x000000, 0, 0x000000);
  private boolean animationLaunched = false;

  private String text = "";

  public VkCompactTitleField() {
    super(Field.USE_ALL_WIDTH);
    setPadding(VkCompactTitleField.px(1), VkCompactTitleField.px(1), VkCompactTitleField.px(1),
        VkCompactTitleField.px(1));
    setBackground(VkCompactTitleField.BACKGROUND);
    image = EncodedImage.getEncodedImageResource("loading_small.gif");
    frameCount = image.getFrameCount();
    height = image.getHeight();
    width = image.getWidth();
  }

  public VkCompactTitleField(String text) {
    this();
    setText(text);
  }

  public void decUpdating() {
    updating--;
    updateGifVisibility();
  }

  public int getPreferredHeight() {
    return Math.max(VkCompactTitleField.px(10),
        VkCompactTitleField.px(1) + VkCompactTitleField.px(1) + image.getHeight());
  }

  public int getPreferredWidth() {
    return Integer.MAX_VALUE;
  }

  public void incUpdating() {
    updating++;
    updateGifVisibility();
  }

  protected void layout(int width, int height) {
    setExtent(Math.min(width, getPreferredWidth()), Math.min(height, getPreferredHeight()));
  }

  protected void paint(Graphics g) {
    int oldColor = g.getColor();

    try {
      int x = getContentLeft() + getContentWidth() - image.getWidth() - VkCompactTitleField.px(1);
      int y = (getContentHeight() - image.getHeight()) / 2;

      if (animationLaunched) {
        g.drawBitmap(x, y, image.getWidth(), image.getHeight(),
            image.getBitmap(currentFrame), 0, 0);
      }

      if (text != null) {
        g.setColor(0xffffff);
        g.drawText(text, getContentLeft(), (getContentHeight() - g.getFont().getHeight()) / 2);
      }
    } finally {
      g.setColor(oldColor);
    }
  }

  public void setText(String text) {
    this.text = text;
  }

  public void startAnimation() {
    if (animationThread != null) {
      animationThread.interrupt();
    }

    animationLaunched = true;

    animationThread = new Thread() {

      public void run() {
        try {
          while (true) {
            if (isVisible()) {
              currentFrame++;
              if (currentFrame >= frameCount) {
                currentFrame = 0;
              }

              invalidate();
            }

            int delay = 50;
            if (image instanceof GIFEncodedImage) {
              delay = 10 * ((GIFEncodedImage) image).getFrameDelay(currentFrame);
            }
            Thread.sleep(delay);
          }
        } catch (InterruptedException e) {
          interrupt();
        }
      }

    };
    animationThread.start();
    invalidate();
  }

  public void stopAnimation() {
    if (animationThread != null) {
      animationThread.interrupt();
      animationThread = null;
    }
    animationLaunched = false;
    invalidate();
  }

  private void updateGifVisibility() {
    if (updating < 0) {
      updating = 0;
    }

    if (updating > 0 && !animationLaunched) {
      startAnimation();
    } else if (updating == 0 && animationLaunched) {
      stopAnimation();
    }
  }

}
