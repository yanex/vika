package org.yanex.vika.gui.widget.base;

import com.mobiata.bb.ui.decor.NinePatchBackground;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.decor.Background;
import org.yanex.vika.gui.util.R;
import org.yanex.vika.gui.util.Theme;

public class EditTextField extends EditField {

    public abstract static class EditListener {
        public abstract boolean onButtonPressed(int key);

        public boolean onNavigationUnclick() {
            return false;
        }

        public abstract void pastButtonPressed(int key);

        public void postNavigationUnclick() {
        }
    }

    private Theme theme;
    private boolean isFocused = false;

    private Bitmap leftBitmap;
    private Bitmap rightBitmap;

    private int leftBitmapWidth = 0;
    private int rightBitmapWidth = 0;
    private String hint = "";

    private String text = "";

    private boolean displayingHint = false;

    private final XYRect borderRect = new XYRect();

    private EditListener listener;

    public EditTextField(long style, Theme theme) {
        super(style);
        init(theme);
    }

    private Background getCurrentBackground() {
        if (isFocused && theme.getFocusBackground() != null) {
            return theme.getFocusBackground();
        } else {
            return theme.getDefaultBackground();
        }
    }

    public String getHint() {
        return hint;
    }

    public EditListener getListener() {
        return listener;
    }

    public String getRealText() {
        return text;
    }

    public Theme getTheme() {
        return theme;
    }

    private void init(Theme theme) {
        this.theme = theme;
        setPadding(theme.getPaddingEdges());
        setMargin(theme.getBorderEdges());
    }

    private void initBitmaps() {
        int left = getPaddingLeft() - leftBitmapWidth, top = getPaddingTop(), right = getPaddingRight()
                - rightBitmapWidth, bottom = getPaddingBottom();

        if (leftBitmap == null) {
            leftBitmapWidth = 0;
        } else {
            leftBitmapWidth = leftBitmap.getWidth() + left;
        }

        if (rightBitmap == null) {
            rightBitmapWidth = 0;
        } else {
            rightBitmapWidth = rightBitmap.getWidth() + left;
        }

        left += leftBitmapWidth;
        right += rightBitmapWidth;

        setPadding(top, right, bottom, left);
    }

    protected boolean keyChar(char key, int status, int time) {
        boolean ret = false;

        if (listener != null) {
            ret = listener.onButtonPressed(key);
        }
        if (!ret) {
            ret = super.keyChar(key, status, time);
        }

        if (listener != null) {
            listener.pastButtonPressed(key);
        }

        return ret;
    }

    public void layout(int width, int height) {
        super.layout(width, height);

        borderRect.set(0, 0, getWidth(), getHeight());

        prerender();
    }

    protected boolean navigationUnclick(int status, int time) {
        boolean ret = false;
        if (listener != null) {
            ret = listener.onNavigationUnclick();
        }
        if (!ret) {
            ret = super.navigationUnclick(status, time);
        }

        if (listener != null) {
            listener.postNavigationUnclick();
        }

        return ret;
    }

    protected void onFocus(int direction) {
        super.onFocus(direction);
        isFocused = true;

        if (displayingHint) {
            // setText("");
            displayingHint = false;
        }

        invalidate();
    }

    protected void onUnfocus() {
        super.onUnfocus();
        isFocused = false;

        text = getText();
        displayingHint = text == null || text.length() == 0;

        invalidate();
    }

    protected void paint(Graphics g) {
        int oldColor = g.getColor();

        if (displayingHint) {
            g.setColor(theme.getSecondaryFontColor());
            g.drawText(hint, 0, 0);
        } else {
            g.setColor(theme.getPrimaryColor());
            super.paint(g);
        }

        g.setColor(oldColor);
    }

    protected void paintBackground(Graphics g) {
        Background currentBackground = getCurrentBackground();
        if (currentBackground != null) {
            currentBackground.draw(g, borderRect);
        }

        XYEdges padding = getTheme().getPaddingEdges();
        int height = getContentHeight();
        int width = getWidth() - getPaddingRight();

        if (leftBitmap != null) {
            int y = padding.top + (height - leftBitmap.getHeight()) / 2;
            g.drawBitmap(padding.left, y, leftBitmap.getWidth(), leftBitmap.getHeight(),
                    leftBitmap, 0, 0);
        }

        if (rightBitmap != null) {
            int y = padding.top + (height - rightBitmap.getHeight()) / 2;
            int x = width - rightBitmap.getWidth();
            g.drawBitmap(x, y, getContentWidth(), getContentHeight(), rightBitmap, 0, 0);
        }
    }

    private void prerender() {
        NinePatchBackground bg;

        int w = getWidth(), h = getHeight();

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

    public void setBitmaps(Bitmap leftBitmap, Bitmap rightBitmap) {
        if (this.leftBitmap == leftBitmap && this.rightBitmap == rightBitmap) {
            return;
        }

        this.leftBitmap = leftBitmap;
        this.rightBitmap = rightBitmap;

        initBitmaps();
        invalidate();
    }

    public void setHint(String hint) {
        this.hint = hint;
        displayingHint = text == null || text.length() == 0;
    }

    public void setListener(EditListener listener) {
        this.listener = listener;
    }
}
