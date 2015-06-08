package org.yanex.vika.gui.widget.base;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYDimension;
import org.yanex.vika.gui.util.R;
import org.yanex.vika.gui.util.Theme;
import org.yanex.vika.util.network.ImageLoader;
import org.yanex.vika.util.network.ImageLoaderCallback;
import org.yanex.vika.util.network.State;

public class AutoLoadingFocusableBitmapField extends FocusableField implements ImageLoaderCallback {

    private final int height;
    private final int width;

    private String text = null;
    private String url;

    private State photoState = State.None;
    private AbstractBitmapField bitmap;
    private int scaleMode = Bitmap.SCALE_TO_FILL;
    private final int downscale;
    private final int roundAngle;

    public AutoLoadingFocusableBitmapField(int width, int height, long style,
                                           int scaleMode, int downscale, int roundAngle, Theme theme) {
        super(style, theme);
        this.height = height;
        this.width = width;
        this.scaleMode = scaleMode;
        this.downscale = downscale;
        this.roundAngle = roundAngle;
    }

    public AutoLoadingFocusableBitmapField(int height, int width, long style, Theme theme) {
        super(style, theme);
        this.height = height;
        this.width = width;
        downscale = 1;
        roundAngle = 3;
    }

    public AbstractBitmapField getBitmap() {
        return bitmap;
    }

    public int getH() {
        return height;
    }

    public int getPreferredHeight() {
        return height + getPaddingLeft() + getPaddingRight();
    }

    public int getPreferredWidth() {
        return width + getPaddingLeft() + getPaddingRight();
    }

    public String getText() {
        return text;
    }

    public int getW() {
        return width;
    }

    // uiThread
    private void load() {
        if (url == null || photoState != State.None) {
            return;
        }

        photoState = State.Loading;
        ImageLoader.instance.load(url, "bitmap",
                getPreferredWidth() - getPaddingLeft() - getPaddingRight(),
                getPreferredHeight() - getPaddingTop() - getPaddingBottom(), true, // roundAngles
                true, // cache
                false, // cache in memory
                scaleMode, this, downscale, roundAngle);
    }

    public void onError(String url, String tag) {
        photoState = State.Error;
    }

    public void onLoad(String url, String tag, Bitmap bmp) {
        this.bitmap = new AbstractBitmapField(bmp, new XYDimension(width, height), false);
        photoState = State.Complete;
        setBitmap(bmp);
        invalidate();
    }

    protected void paint(Graphics g) {
        if (bitmap != null) {
            int oldAlpha = g.getGlobalAlpha();
            if (isFocused() || isActive()) {
                g.setGlobalAlpha(200);
            }

            int w = getContentWidth() - getPaddingLeft() - getPaddingRight();
            int h = getContentHeight() - getPaddingTop() - getPaddingBottom();

            int x = (w - width) / 2 + getPaddingLeft();
            int y = (h - height) / 2 + getPaddingTop();

            bitmap.draw(g, x, y, width, height);

            g.setGlobalAlpha(oldAlpha);
        } else {
            if (photoState == State.None) {
                load();
            }
        }
        if (text != null) {
            int h = R.px(10);

            int oldAlpha = g.getGlobalAlpha();
            int oldColor = g.getColor();

            g.setGlobalAlpha(150);
            g.fillRect(0, getContentHeight() - h, getContentWidth(), h);
            g.setGlobalAlpha(200);
            g.setColor(0xEEEEEE);
            g.drawText(text, getContentWidth() / 2, getContentHeight() - h / 2, DrawStyle.HCENTER);

            g.setGlobalAlpha(oldAlpha);
            g.setColor(oldColor);
        }
    }

    public void setBitmap(Bitmap bmp) {
        bitmap = new AbstractBitmapField(bmp, new XYDimension(width, height), false);
        invalidate();
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setURL(String url) {
        this.url = url;
        photoState = State.None;
        load();
    }

}
