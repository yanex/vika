package org.yanex.vika.gui.widget.base;

import com.mobiata.bb.ui.decor.NinePatchBitmap;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.*;
import org.yanex.vika.gui.util.R;
import org.yanex.vika.gui.util.Theme;

public class SliderField extends FocusableField implements FieldChangeListener {

  public static interface SliderListener {
    public void newPosition(float position);
  }

  private static final Theme theme = new Theme();

  private static final NinePatchBitmap Line1 = R.instance.getNinepatch("Audio/Line1.png");
  private static final NinePatchBitmap Line2 = R.instance.getNinepatch("Audio/Line2.png");
  private static final NinePatchBitmap Line3 = R.instance.getNinepatch("Audio/Line3.png");

  private static final Bitmap Circle = R.instance.getBitmap("Audio/Circle.png");
  private static final Bitmap CircleHover = R.instance.getBitmap("Audio/CircleHover.png");

  private boolean moving = false;
  private float newPosition = 0;
  private int newPositionPx = 0;
  private float position = 0;
  private int positionPx = 0;
  private int width = 0;

  private boolean showSlider = false;

  private float secondaryPosition = 0;
  private int secondaryPositionPx = 0;

  private SliderListener listener;

  public SliderField(int width, long style) {
    super(style, SliderField.theme);
    this.width = width;
    setChangeListener(this);
  }

  public void fieldChanged(Field arg0, int arg1) {
    if (moving && showSlider) {
      setPosition(newPosition);
      if (listener != null) {
        listener.newPosition(position);
        moving = false;
      }
    }
  }

  public SliderListener getListener() {
    return listener;
  }

  public float getPosition() {
    return position;
  }

  public int getPreferredHeight() {
    return Math.max(SliderField.Line1.getBitmap().getHeight(), SliderField.Circle.getHeight());
  }

  public int getPreferredWidth() {
    if (isStyle(Field.USE_ALL_WIDTH)) {
      return Integer.MAX_VALUE;
    } else {
      return Math.max(width, Display.getWidth() / 2 - DP6);
    }
  }

  public float getSecondaryPosition() {
    return secondaryPosition;
  }

  public boolean isFocusable() {
    return showSlider && super.isFocusable();
  }

  public boolean isShowSlider() {
    return showSlider;
  }

  protected boolean navigationMovement(int dx, int dy, int status, int time) {
    if (showSlider) {
      if (dx < 0 && dy == 0) { // left
        moving = true;
        newPosition -= 0.04f;
        updatePositionPx();
        invalidate();
        return true;
      } else if (dx > 0 && dy == 0) { // right
        moving = true;
        newPosition += 0.04f;
        updatePositionPx();
        invalidate();
        return true;
      }
    }

    return super.navigationMovement(dx, dy, status, time);
  }

  protected void onFieldFocus() {
    // moving = true;
  }

  protected void onFieldUnfocus() {
    if (moving == true && showSlider && newPosition != position) {
      // setPosition(newPosition);
    }

    moving = false;
    updatePositionPx();
    invalidate();
  }

  protected void paint(Graphics g) {
    int h = SliderField.Line1.getBitmap().getHeight();
    int y = (getContentHeight() - h) / 2;
    int m = SliderField.Circle.getWidth() / 2;

    if (positionPx < 0 || secondaryPositionPx < 0) {
      updatePositionPx();
    }

    try {
      SliderField.Line1.draw(g, new XYRect(m, y, getContentWidth() - m * 2, h));
      if (secondaryPositionPx > 0) {
        SliderField.Line2.draw(g, new XYRect(m, y, secondaryPositionPx, h));
      }
      if (positionPx > 0) {
        SliderField.Line3.draw(g, new XYRect(m, y, positionPx, h));
      }
    } catch (Exception e) {
    }

    if (isFocused() || isActive()) {
      g.drawBitmap(newPositionPx, 0, SliderField.CircleHover.getWidth(),
          SliderField.CircleHover.getHeight(), SliderField.CircleHover, 0, 0);
    } else {
      g.drawBitmap(newPositionPx, 0, SliderField.Circle.getWidth(), SliderField.Circle.getHeight(),
          SliderField.Circle, 0, 0);
    }
  }

  public void setListener(SliderListener listener) {
    this.listener = listener;
  }

  public void setPosition(float position) {
    this.position = position;
    updatePositionPx();
    // if (listener!=null)
    // listener.newPosition(position);
    invalidate();
  }

  public void setSecondaryPosition(float secondaryPosition) {
    this.secondaryPosition = secondaryPosition;
    updatePositionPx();
  }

  public void setShowSlider(boolean showSlider) {
    this.showSlider = showSlider;
    invalidate();
  }

  public void setWidth(int width) {
    this.width = width;
    updateLayout();
  }

  protected boolean touchEvent(TouchEvent message) {
    boolean isOutOfBounds = touchEventOutOfBounds(message);
    if (showSlider) {
      switch (message.getEvent()) {
        case TouchEvent.DOWN:
          if (!isOutOfBounds) {
            moving = true;

            int m = SliderField.Circle.getWidth() / 2;
            newPosition = (float) (message.getX(1) - m) / (float) (getContentWidth() - 2 * m);
            updatePositionPx();
            invalidate();
            return true;
          }
          return false;

        case TouchEvent.MOVE:
          if (moving) {
            int m = SliderField.Circle.getWidth() / 2;
            newPosition = (float) (message.getX(1) - m) / (float) (getContentWidth() - 2 * m);
            updatePositionPx();
            invalidate();
            return true;
          }
          return false;

        case TouchEvent.UNCLICK:
        case TouchEvent.UP:
          if (moving) {
            int m = SliderField.Circle.getWidth() / 2;
            setPosition((float) (message.getX(1) - m) / (float) (getContentWidth() - 2 * m));
            if (listener != null) {
              listener.newPosition(position);
            }
            return true;
          }

          return false;

        default:
          return false;
      }
    }
    return false;
  }

  private boolean touchEventOutOfBounds(TouchEvent message) {
    int x = message.getX(1);
    int y = message.getY(1);
    return x < 0 || y < 0 || x > getWidth() || y > getHeight();
  }

  private void updatePositionPx() {
    if (position < 0) {
      position = 0;
    }
    if (position > 1) {
      position = 1;
    }
    if (secondaryPosition < 0) {
      secondaryPosition = 0;
    }
    if (secondaryPosition > 1) {
      secondaryPosition = 1;
    }
    if (newPosition < 0) {
      newPosition = 0;
    }
    if (newPosition > 1) {
      newPosition = 1;
    }

    if (!moving) {
      newPosition = position;
    }

    int m = SliderField.Circle.getWidth() / 2;

    float d = getContentWidth() - m * 2;

    positionPx = (int) (position * d);
    newPositionPx = (int) (newPosition * d);
    secondaryPositionPx = (int) (secondaryPosition * d);

  }
}
