package org.yanex.vika.gui.widget.base;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.GIFEncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;

public class GifAnimationField extends Field {

  private static final int DEFAULT_DELAY = 50;
  private static final int GIF_DELAY = 10;

  private final EncodedImage image;
  private final int height;
  private final int width;
  private final int frameCount;

  private int currentFrame = 0;
  private boolean visible = true;
  private Thread animationThread;

  private final Runnable animationLoop = new Runnable() {

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

          int delay = DEFAULT_DELAY;
          if (image instanceof GIFEncodedImage) {
            delay = GIF_DELAY * ((GIFEncodedImage) image).getFrameDelay(currentFrame);
          }
          Thread.sleep(delay);
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  };

  public GifAnimationField(String resource, long style) {
    super(style);
    image = EncodedImage.getEncodedImageResource(resource);
    frameCount = image.getFrameCount();
    height = image.getHeight();
    width = image.getWidth();
  }

  public int getPreferredHeight() {
    return height;
  }

  public int getPreferredWidth() {
    return width;
  }

  public void stop() {
    if (visible) {
      visible = false;
      stopAnimation();
      invalidate();
    }
  }

  protected void layout(int width, int height) {
    setExtent(Math.min(width, getPreferredWidth()), Math.min(height, getPreferredHeight()));
  }

  protected void paint(Graphics g) {
    if (visible) {
      int x = (getContentWidth() - image.getWidth()) / 2;
      int y = (getContentHeight() - image.getHeight()) / 2;

      g.drawBitmap(x, y, image.getWidth(), image.getHeight(), image.getBitmap(currentFrame),
          0, 0);
    }
  }

  public void start() {
    if (!visible) {
      visible = true;
      startAnimation();
    }
  }

  public void startAnimation() {
    if (animationThread != null) {
      animationThread.interrupt();
    }

    animationThread = new Thread(animationLoop);
    animationThread.start();
  }

  private void stopAnimation() {
    if (animationThread != null) {
      animationThread.interrupt();
      animationThread = null;
    }
  }

}
