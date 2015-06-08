package org.yanex.vika.gui.widget.base;

import com.mobiata.bb.ui.decor.NinePatchBackground;
import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.decor.Background;
import org.yanex.vika.gui.util.GuiItem;
import org.yanex.vika.gui.util.R;
import org.yanex.vika.gui.util.Theme;

public abstract class FocusableField extends Field implements GuiItem {

    private boolean isFocused = false;
    private boolean isActive = false;

    private boolean isFocusable = true;

    private final XYRect contentRect = new XYRect();
    private final XYRect borderRect = new XYRect();
    private final XYRect backgroundRect = new XYRect();

    private final Theme theme;

    private int id;

    public FocusableField(long style, Theme theme) {
        super(style);
        this.theme = theme;

        setPadding(theme.getPaddingEdges());
    }

    protected void drawFocus(Graphics g, boolean on) {

    }

    protected XYRect getBackgroundRect() {
        return backgroundRect;
    }

    protected Background getCurrentBackground() {
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

    protected Theme getTheme() {
        return theme;
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
        return isFocusable;
    }

    public boolean isFocused() {
        return isFocused;
    }

    protected boolean keyChar(char character, int status, int time) {
        if (character == Characters.ENTER) {
            raiseClick();
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

    public void layout(int width, int height) {
        setExtent(Math.min(width, getPreferredWidth()), Math.min(height, getPreferredHeight()));

        XYEdges b = getTheme().getBorderEdges();

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

        // prerender();
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
        raiseClick();

        return true;
    }

    protected void onFieldFocus() {

    }

    protected void onFieldUnfocus() {

    }

    protected void onFocus(int direction) {
        isFocused = true;
        onFieldFocus();
        invalidate();
        super.onFocus(direction);
    }

    protected void onUnfocus() {
        if (isActive || isFocused) {
            isFocused = false;
            onFieldUnfocus();
            isActive = false;
            invalidate();
        }
        super.onUnfocus();
    }

    protected void paintBackground(Graphics g) {
        Background currentBackground = getCurrentBackground();

        if (currentBackground != null) {
            currentBackground.draw(g, borderRect);
        }
    }

    private void raiseClick() {
        fieldChangeNotify(0);
    }

    public void setFocusable(boolean value) {
        isFocusable = value;
    }

    public void setId(int id) {
        this.id = id;
    }

    protected boolean touchEvent(TouchEvent message) {
        boolean isOutOfBounds = touchEventOutOfBounds(message);
        switch (message.getEvent()) {
            case TouchEvent.DOWN:
                if (!isOutOfBounds) {
                    if (!isActive) {
                        isActive = true;
                        invalidate();
                    }
                    return true;
                }
                return false;

            case TouchEvent.UNCLICK:
                if (isActive) {
                    isActive = false;
                    invalidate();
                }

                if (!isOutOfBounds) {
                    raiseClick();
                    return true;
                }
            case TouchEvent.UP:
                if (isActive) {
                    isActive = false;
                    invalidate();
                }

            default:
                return false;
        }
    }

    private boolean touchEventOutOfBounds(TouchEvent message) {
        int x = message.getX(1);
        int y = message.getY(1);
        return x < 0 || y < 0 || x > getWidth() || y > getHeight();
    }

}
