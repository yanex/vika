package org.yanex.vika.gui.widget.base;

import com.nutiteq.BasicMapComponent;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.listeners.MapListener;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.TouchEvent;
import org.yanex.vika.gui.util.R;

public class TouchMapField extends Field implements MapListener {

    private final BasicMapComponent map;
    private MapListener mapListener;

    private Graphics wrapped;
    private com.nutiteq.wrappers.Graphics graphicsWrapper;

    private boolean dualZoom;

    private static final Bitmap OK_BITMAP = R.instance.getBitmap("maps_ok.png");

    // BlackBerry specific key and touch control handlers

    // Take it out for older than 4.7 BB API
    private int firstPointerX;
    private int firstPointerY;

    private boolean touchOkShow = false;

    public TouchMapField(BasicMapComponent map) {
        super(Field.FOCUSABLE);
        this.map = map;
        map.setMapListener(this);

        // this.setPadding(2, 2, 2, 2);
    }

    protected void drawFocus(Graphics graphics, boolean on) {

    }

    public void fieldChangeNotify(int context) {
        try {
            this.getChangeListener().fieldChanged(this, context);
        } catch (Exception e) {
        }
    }

    // universial accessor for the map
    public BasicMapComponent getMap() {
        return this.map;
    }

    public boolean isTouchOkShow() {
        return touchOkShow;
    }

    protected boolean keyDown(int keycode, int time) {
        map.keyPressed(Keypad.key(keycode));
        return super.keyDown(keycode, time);
    }

    protected boolean keyRepeat(int keycode, int time) {
        map.keyRepeated(Keypad.key(keycode));
        return super.keyRepeat(keycode, time);
    }

    protected boolean keyUp(int keycode, int time) {
        map.keyReleased(Keypad.key(keycode));
        return super.keyUp(keycode, time);
    }

    protected void layout(int w, int h) {
        setExtent(w, h);
    }

    public void mapClicked(WgsPoint p) {
        if (mapListener != null) {
            mapListener.mapClicked(p);
        }
    }

    public void mapMoved() {
        if (mapListener != null) {
            mapListener.mapMoved();
        }
    }

    protected boolean navigationMovement(int dx, int dy, int status, int time) {
        map.panMap(dx * 10, dy * 10);
        // return super.navigationMovement(dx, dy, status, time);
        return false;
    }

    public void needRepaint(boolean mapIsComplete) {
        invalidate();
        if (mapListener != null) {
            mapListener.needRepaint(mapIsComplete);
        }
    }

    protected void okClicked() {

    }

    /*
     * protected boolean navigationClick(int status, int time) { fieldChangeNotify(1); return true; }
     */
    protected void onFocus(int direction) {
        invalidate();
    }

    protected void onUnfocus() {
        invalidate();
    }

    protected void paint(Graphics g) {
        if (wrapped != g) {
            wrapped = g;
            graphicsWrapper = new com.nutiteq.wrappers.Graphics(g);
        }
        // paint on wrapper (in effect painting on native graphics)
        map.paint(graphicsWrapper);
        // remove pushContext() done inside library
        graphicsWrapper.popAll();

        if (touchOkShow) {
            int x = getContentWidth() - TouchMapField.OK_BITMAP.getWidth();
            int y = 0;// getContentHeight()-OK_BITMAP.getHeight();

            g.drawBitmap(x, y, TouchMapField.OK_BITMAP.getWidth(),
                    TouchMapField.OK_BITMAP.getHeight(), TouchMapField.OK_BITMAP, 0, 0);
        }
    }

    public void setMapListener(final MapListener mL) {
        mapListener = mL;
    }

    public void setTouchOkShow(boolean touchOkShow) {
        this.touchOkShow = touchOkShow;
    }

    protected boolean touchEvent(TouchEvent event) {

        int x = getWidth() - TouchMapField.OK_BITMAP.getWidth();
        int y = TouchMapField.OK_BITMAP.getHeight();
        if (touchOkShow && event.getEvent() == TouchEvent.UNCLICK && event.getX(1) >= x
                && event.getY(1) <= y) {
            okClicked();
            return true;
        }

        int eventCode = event.getEvent();
        boolean hasSecondPointer = true;
        if (event.getX(2) == -1 || event.getY(2) == -1) {
            hasSecondPointer = false;
        }

        switch (eventCode) {
            case TouchEvent.CLICK:
                map.pointerPressed(event.getX(1), event.getY(1));
                break;
            case TouchEvent.DOWN:
                if (!hasSecondPointer) {
                    firstPointerX = event.getX(1);
                    firstPointerY = event.getY(1);
                    map.pointerPressed(firstPointerX, firstPointerY);
                } else if (hasSecondPointer && !dualZoom) {
                    dualZoom = true;
                    double dist = Math.sqrt((event.getX(2) - firstPointerX) * (event.getX(2) - firstPointerX)
                            + (event.getY(2) - firstPointerY) * (event.getY(2) - firstPointerY));
                    map.pointerDraggedDual(dist);
                    map.startDualZoom((event.getX(1) + event.getX(2)) / 2, (event.getY(1) + event.getY(2)) / 2,
                            dist);
                }
                break;
            case TouchEvent.MOVE:
                if (!hasSecondPointer && !dualZoom) {
                    map.pointerDragged(event.getX(1), event.getY(1));
                } else if (hasSecondPointer && dualZoom) {
                    double dist = Math.sqrt((event.getX(2) - event.getX(1)) * (event.getX(2) - event.getX(1))
                            + (event.getY(2) - event.getY(1)) * (event.getY(2) - event.getY(1)));
                    map.pointerDraggedDual(dist);
                }
                break;
            case TouchEvent.UNCLICK:
                map.pointerReleased(event.getX(1), event.getY(1));
                break;
            case TouchEvent.UP:
                map.pointerReleased(event.getX(1), event.getY(1));
                if (dualZoom) {
                    dualZoom = false;
                    map.stopDualZoom();
                }
                break;
        }

        return false;
    }

}
