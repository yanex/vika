package org.yanex.vika.gui.widget.manager;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.NullField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import org.yanex.vika.api.util.ThreadHelper;
import org.yanex.vika.gui.list.List;
import org.yanex.vika.gui.screen.VkMainScreen;
import org.yanex.vika.gui.widget.base.PaneCaptionField;
import org.yanex.vika.gui.widget.base.PaneCaptionField.PaneCaptionFeedback;
import org.yanex.vika.util.fun.RichVector;

public class PaneManager extends HorizontalFieldManager implements PaneCaptionFeedback {

    private class Navigator extends ThreadHelper {

        private static final int DISPLAY_TIME = 1;
        private static final int STEPS = 10;

        private float fromX, toX;
        private int totalTime;
        private float step;
        private int stepTime;
        private volatile float x;
        private Field moveFocusTo;

        public Navigator(int fromX, int toX, Field moveFocusTo) {
            this.fromX = x = fromX;
            this.toX = toX;
            totalTime = Navigator.DISPLAY_TIME * Display.getWidth() / Math.abs(fromX - toX);
            stepTime = totalTime / Navigator.STEPS;

            if (stepTime < 1) {
                stepTime = 1;
            }
            step = (toX - fromX) / (float) Navigator.STEPS;
            this.moveFocusTo = moveFocusTo;
        }

        public void after(Object o) {
            animating = false;
            current = currentNew;
            setDx((int) toX);

            yankInvalidateField();
            invalidate();
        }

        public void before() {
            animating = true;
            owner.getMainManager().setVerticalScroll(0);
            if (moveFocusTo != null) {
                moveFocusTo.setFocus();
            }
        }

        private boolean notEnd() {
            if (toX > fromX) {
                return x < toX;
            } else {
                return x > toX;
            }
        }

        public Object task() {
            try {
                while (notEnd()) {
                    x += step;
                    setDx((int) x);
                    invalidate();
                    Thread.sleep(stepTime);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Boolean.TRUE;
        }

    }

    private class NonFocusableNullField extends NullField {

        public boolean isFocusable() {
            return false;
        }
    }

    private class Pane extends VerticalFieldManager implements FocusChangeListener {
        private int id;
        private NullField firstField = new NullField();
        private Field lastFocusedField;
        private Field content;

        public Pane(int id) {
            super(PaneManager.MAGIC | Manager.VERTICAL_SCROLL);
            this.id = id;
            firstField.setFocusListener(this);
        }

        public void addContent(Field f) {
            content = f;
            add(f);
            f.setFocusListener(this);
        }

        public void focusChanged(Field field, int eventType) {
            if (eventType == FocusChangeListener.FOCUS_GAINED && field == this) {
                if (lastFocusedField != null) {
                    lastFocusedField.setFocus();
                }
            }
        }

        public boolean isFocusable() {
            return current == id || currentNew == id;
        }

        public boolean touchEvent(TouchEvent message, int dy) {
            if (content instanceof List) {
                return ((List) content).touchEvent(message, dy + PaneManager.this.getTop());
            } else if (content instanceof Manager) {
                Manager m = (Manager) content;
                int f = m.getFieldAtLocation(message.getX(1), message.getY(1) - dy);

                if (f < 0) {
                    return false;
                }

                Field content = m.getField(f);

                if (content instanceof List) {
                    return ((List) content).touchEvent(message, dy + content.getTop() + m.getTop()
                            + PaneManager.this.getTop());
                } else {
                    content.setFocus();
                    return false;
                }
            } else {
                return super.touchEvent(message);
            }
        }
    }

    private static final long MAGIC = 48028866924707840L;

    private int paneCount = 0;

    private int current = 0;
    private VkMainScreen owner;

    private RichVector titles = new RichVector();
    private PaneCaptionField caption;

    private NonFocusableNullField nnnf;

    private boolean animating = false;

    private int currentNew = 0;

    private int dx = 0;

    public PaneManager(PaneCaptionField caption, VkMainScreen owner) {
        super(PaneManager.MAGIC);
        this.caption = caption;
        caption.setFeedback(this);
        setOwner(owner);
    }

    public void addPane(String caption, Field content) {
        int id = getFieldCount();

        paneCount++;

        Pane v = new Pane(id);
        v.addContent(content);
        add(v);
        titles.addElement(caption);

        if (this.caption != null) {
            this.caption.setTitles(titles);
            this.caption.setCurrent(current);
        }
    }

    public int getMainVerticalScroll() {
        return owner.getMainManager().getVerticalScroll();
    }

    public void moveLeft(boolean fromCaption) {
        if (animating) {
            return;
        }

        if (current > 0) {
            currentNew = current - 1;

            if (!fromCaption) {
                getField(currentNew).setFocus();
            }
            new Navigator(dx, dx - Display.getWidth(), fromCaption ? null : getField(currentNew))
                    .start();

            invalidate();
        }
    }

    public void moveRight(boolean fromCaption) {
        if (animating) {
            return;
        }

        if (current < paneCount - 1) {
            currentNew = current + 1;
            new Navigator(dx, dx + Display.getWidth(), fromCaption ? null : getField(currentNew))
                    .start();
        }
    }

    protected boolean navigationMovement(int dx, int dy, int status, int time) {
        if (animating) {
            return true;
        }

        if (dy == 0) {
            if (dx < 0) {
                moveLeft(false);
            } else if (dx > 0) {
                moveRight(false);
            }

            return true;
        } else {
            return super.navigationMovement(dx, dy, status, time);
        }
    }

    protected void paint(Graphics g) {
        super.paint(g);
    }

    private void setDx(int dx) {
        this.dx = dx;
        if (caption != null) {
            caption.setDx(dx);
            caption.setCurrent(current);
        }
    }

    public void setOwner(VkMainScreen owner) {
        this.owner = owner;
    }

    protected void sublayout(int width, int height) {
        Field field;
        int numberOfFields = getFieldCount();

        int ch = Integer.MAX_VALUE;
        int chNew = Integer.MAX_VALUE;

        for (int i = 0; i < numberOfFields; i++) {
            field = getField(i);
            setPositionChild(field, 0, 0);
            layoutChild(field, width, height);

            if (current == i) {
                ch = field.getContentHeight();
            }

            if (currentNew == i) {
                chNew = field.getContentHeight();
            }
        }

        int h = ch;
        if (animating) {
            h = Math.max(ch, chNew);
        }

        setExtent(width, h);
    }

    protected void subpaint(Graphics g) {
        int dx = -this.dx, dy = 0;

        g.translate(dx, dy);

        for (int i = 0; i < getFieldCount(); ++i) {
            paintChild(g, getField(i));
            g.translate(getWidth(), 0);
            dx += getWidth();
        }

        g.translate(-dx, -dy);
    }

    protected boolean touchEvent(TouchEvent message) {
        if (message.getEvent() == TouchEvent.GESTURE) {
            TouchGesture gesture = message.getGesture();
            if (gesture.getEvent() == TouchGesture.SWIPE) {
                if (gesture.getSwipeDirection() == TouchGesture.SWIPE_EAST) {
                    moveLeft(false);
                    return true;
                } else if (gesture.getSwipeDirection() == TouchGesture.SWIPE_WEST) {
                    moveRight(false);
                    return true;
                }
            }
        }

        return super.touchEvent(message);
    }

    public boolean touchEvent(TouchEvent message, int dy) {
        return !animating && ((Pane) getField(current)).touchEvent(message, dy);
    }

    private void yankInvalidateField() {
        if (nnnf != null) {
            delete(nnnf);

        }

        nnnf = new NonFocusableNullField();
        add(nnnf);
    }

}
