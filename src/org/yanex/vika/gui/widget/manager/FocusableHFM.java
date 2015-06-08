package org.yanex.vika.gui.widget.manager;

import com.mobiata.bb.ui.decor.NinePatchBackground;
import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Background;
import org.yanex.vika.gui.util.GuiItem;
import org.yanex.vika.gui.util.R;
import org.yanex.vika.gui.util.Theme;
import org.yanex.vika.gui.widget.base.FocusableNullField;

public abstract class FocusableHFM extends VerticalFieldManager implements FieldChangeListener,
        FocusChangeListener, GuiItem {

    private boolean isFocused = false;
    private boolean isActive = false;

    private boolean isFocusable = true;
    private XYRect contentRect = new XYRect();
    private XYRect borderRect = new XYRect();

    private XYRect backgroundRect = new XYRect();

    private int maxWidth = -1;

    private Theme theme;
    private FocusableNullField focusableNull;

    private FocusableNullField focusableNull2;

    private int id;

    private boolean lock = false;

    public FocusableHFM(long style, Theme theme) {
        super(style | Field.FOCUSABLE);
        this.theme = theme;

        setPadding(theme.getPaddingEdges());
    }

    protected void addingCompleted() {
        focusableNull = new FocusableNullField();
        focusableNull.setChangeListener(this);
        focusableNull.setFocusListener(this);

        focusableNull2 = new FocusableNullField();
        focusableNull2.setFocusable(false);
        // focusableNull.setFocusable(false);

        // focusableNull2 = new FocusableNull(0);
        // focusableNull2.setFocusable(false);

        insert(focusableNull2, 0);
        add(focusableNull);

    }

    protected void drawFocus(Graphics g, boolean on) {

    }

    public void fieldChanged(Field f, int arg1) {

    }

    public void focusChanged(Field field, int eventType) {
        if (field instanceof FocusableNullField && eventType == FocusChangeListener.FOCUS_GAINED
                && !lock) {
            isFocused = true;
            onLayoutFocus();

            if (getHeight() < getManager().getVisibleHeight()) {
                lock = true;
                focusableNull2.setFocusable(true);
                focusableNull2.setFocus();
                focusableNull.setFocus();
                focusableNull2.setFocusable(false);
                lock = false;
            }
            invalidate();
        } else if (field instanceof FocusableNullField && eventType == FocusChangeListener.FOCUS_LOST
                && !lock) {
            isFocused = false;
            onLayoutUnfocus();
            invalidate();
        }
    }

    protected XYRect getBackgroundRect() {
        return backgroundRect;
    }

    private Background getCurrentBackground() {
        Background ret;

        if (isActive) {
            ret = theme.getActiveBackground();
        } else if (isFocused) {
            ret = theme.getFocusBackground();
        } else {
            ret = theme.getDefaultBackground();
        }

        return ret;
    }

    public int getId() {
        return id;
    }

    public int getPreferredWidth() {
        if (maxWidth > 0) {
            return maxWidth;
        } else {
            return super.getPreferredWidth();
        }
    }

    protected boolean invokeAction(int action) {
        switch (action) {
            case ACTION_INVOKE: {
                raiseClick();
                return true;
            }
        }

        return super.invokeAction(action);
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isFocusable() {
        return isFocusable && super.isFocusable();
    }

    public boolean isFocused() {
        return isFocused;
    }

    protected boolean keyChar(char character, int status, int time) {
        if (character == Characters.ENTER) {
            boolean ret = super.keyChar(character, status, time);
            if (!ret) {
                raiseClick();
            }
            return true;
        }

        return super.keyChar(character, status, time);
    }

    protected boolean keyDown(int keycode, int time) {
        if (Keypad.map(Keypad.key(keycode), Keypad.status(keycode)) == Characters.ENTER) {
            isActive = true;
            invalidate();
        }

        return super.keyDown(keycode, time);
    }

    protected boolean keyUp(int keycode, int time) {
        if (Keypad.map(Keypad.key(keycode), Keypad.status(keycode)) == Characters.ENTER) {
            isActive = false;
            invalidate();
            return true;
        }

        return super.keyUp(keycode, time);
    }

    public void layout(int width, int height, boolean fullPrerender) {
        layout(width, height);

        if (fullPrerender) {
            NinePatchBackground bg;

            int w = borderRect.width, h = borderRect.height;

            if (theme.getDefaultBackground() != null
                    && theme.getDefaultBackground() instanceof NinePatchBackground) {
                bg = (NinePatchBackground) theme.getDefaultBackground();
                R.instance.prerenderNinepatch(bg.getNinePatch(), w, h);
            }
        }
    }

    protected boolean navigationClick(int status, int time) {
        isActive = true;
        invalidate();

        return super.navigationClick(status, time);
    }

    protected boolean navigationUnclick(int status, int time) {
        isActive = false;
        invalidate();

        boolean ret = super.navigationUnclick(status, time);
        if (!ret) {
            raiseClick();
        }

        return true;
    }

    protected void onLayoutFocus() {

    }

    protected void onLayoutUnfocus() {

    }

    protected void paintBackground(Graphics g) {
        Background currentBackground = getCurrentBackground();

        if (currentBackground != null) {
            currentBackground.draw(g, borderRect);
        }
    }

    private void prerender() {
        NinePatchBackground bg;

        int w = borderRect.width, h = borderRect.height;

        if (theme.getFocusBackground() != null
                && theme.getFocusBackground() instanceof NinePatchBackground) {
            bg = (NinePatchBackground) theme.getFocusBackground();
            R.instance.prerenderNinepatch(bg.getNinePatch(), w, h);
        }

        if (theme.getActiveBackground() != null
                && theme.getActiveBackground() instanceof NinePatchBackground) {
            bg = (NinePatchBackground) theme.getActiveBackground();
            R.instance.prerenderNinepatch(bg.getNinePatch(), w, h);
        }

        if (theme.getSelectedBackground() != null
                && theme.getSelectedBackground() instanceof NinePatchBackground) {
            bg = (NinePatchBackground) theme.getSelectedBackground();
            R.instance.prerenderNinepatch(bg.getNinePatch(), w, h);
        }
    }

    private void raiseClick() {
        // setFocus();
        focusableNull.fieldChangeNotify(0);
    }

    public void setFocusable(boolean value) {
        isFocusable = value;
    }

    public void setId(int id) {
        this.id = id;
    }

    protected void sublayout(int maxWidth, int maxHeight) {
        super.sublayout(maxWidth, maxHeight);

        XYEdges b = theme.getBorderEdges();

        int borderLeft = b.left;
        int borderRight = b.right;
        int borderTop = b.top;
        int borderBottom = b.bottom;

        int borderWidth = borderLeft + borderRight;
        int borderHeight = borderTop + borderBottom;

        getContentRect(contentRect);
        borderRect.set(0, 0, getWidth(), getHeight());
        backgroundRect.set(borderLeft, borderTop, borderRect.width - borderWidth, borderRect.height
                - borderHeight);

        prerender();
    }

    protected boolean touchEvent(TouchEvent message) {
        boolean isOutOfBounds = touchEventOutOfBounds(message);
        switch (message.getEvent()) {
            case TouchEvent.DOWN:
                boolean ret = super.touchEvent(message);
                if (!ret && !isOutOfBounds) {
                    if (!isActive) {
                        isActive = true;
                        invalidate();
                    }
                    return ret;
                }
                return false;

            case TouchEvent.UNCLICK:
                if (isActive) {
                    isActive = false;
                    invalidate();
                }

                if (!isOutOfBounds) {
                    boolean ret2 = super.touchEvent(message);
                    if (!ret2) {
                        raiseClick();
                    }
                    return true;
                }

                return super.touchEvent(message);
            case TouchEvent.UP:
                if (isActive) {
                    isActive = false;
                    invalidate();
                }

                return super.touchEvent(message);
            default:
                return super.touchEvent(message);
        }
    }

    private boolean touchEventOutOfBounds(TouchEvent message) {
        int x = message.getX(1);
        int y = message.getY(1);
        return x < 0 || y < 0 || x > getWidth() || y > getHeight();
    }

}
