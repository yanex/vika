package org.yanex.vika.gui.widget.base;

import com.mobiata.bb.ui.decor.NinePatchBitmap;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYDimension;
import org.yanex.vika.gui.util.R;

public class ImageField extends Field {

    private AbstractBitmapField bitmap;
    private final int height;
    private final int width;

    private String text = null;

    private AbstractBitmapField defaultBitmap;

    public ImageField(Bitmap bmp, int width, int height, long style, boolean scale) {
        super(style);
        this.height = height;
        this.width = width;
        if (bmp != null) {
            bitmap = new AbstractBitmapField(bmp, new XYDimension(width, height), scale);
        }
    }

    public ImageField(NinePatchBitmap npbmp, int height, int width, long style) {
        super(style);
        this.height = height;
        this.width = width;
        if (npbmp != null) {
            bitmap = new AbstractBitmapField(npbmp);
        }
    }

    public AbstractBitmapField getAbstractBitmap() {
        return bitmap;
    }

    public AbstractBitmapField getBitmap() {
        return bitmap;
    }

    public AbstractBitmapField getDefaultBitmap() {
        return defaultBitmap;
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

    protected void layout(int width, int height) {
        setExtent(Math.min(width, getPreferredWidth()), Math.min(height, getPreferredHeight()));
    }

    protected void paint(Graphics g) {
        if (bitmap != null) {
            bitmap.draw(g, 0, 0, getContentWidth(), getContentHeight());
        } else if (defaultBitmap != null) {
            defaultBitmap.draw(g, 0, 0, getContentWidth(), getContentHeight());
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

    public void setBitmap(AbstractBitmapField bmp) {
        bitmap = bmp;
        invalidate();
    }

    public void setBitmap(Bitmap bmp) {
        bitmap = new AbstractBitmapField(bmp, new XYDimension(width, height), false);
        invalidate();
    }

    public void setDefaultBitmap(AbstractBitmapField defaultBitmap) {
        this.defaultBitmap = defaultBitmap;
    }

    public void setText(String text) {
        this.text = text;
    }

}
