package org.yanex.vika.gui.widget.base;

import com.mobiata.bb.ui.decor.NinePatchBitmap;
import com.patchou.ui.GPATools;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYDimension;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.XYRect;
import org.yanex.vika.gui.util.RoundAngles;

public class AbstractBitmapField {

    private Bitmap bmp = null;
    private NinePatchBitmap npbmp = null;

    private int height = 0, width = 0;

    public AbstractBitmapField(Bitmap bmp, XYDimension size, boolean scale) {
        this(bmp, size, scale, false);
    }

    public AbstractBitmapField(Bitmap bmp, XYDimension size, boolean scale,
                               boolean roundAngles) {
        int newHeight = size.height, newWidth = size.width;
        Bitmap newBmp;

        if (bmp.getWidth() > newWidth || bmp.getHeight() > newHeight || scale) {
            newBmp = GPATools.ResizeTransparentBitmap(bmp, newWidth, newHeight,
                    Bitmap.FILTER_LANCZOS, Bitmap.SCALE_TO_FIT);
        } else {
            newBmp = bmp;
        }

        this.height = newHeight;
        this.width = newWidth;

        if (roundAngles) {
            RoundAngles.roundAngles(newBmp, 3);
        }

        this.bmp = newBmp;
    }

    public AbstractBitmapField(NinePatchBitmap npbmp) {
        this.npbmp = npbmp;
    }

    public void draw(Graphics g, int left, int top, int width, int height) {
        draw(g, new XYRect(left, top, width, height));
    }

    public void draw(Graphics g, XYRect rect) {
        if (isNinePatch()) {
            npbmp.draw(g, rect);
        } else {
            int width = bmp.getWidth(), height = bmp.getHeight();
            int x = (rect.width - width) / 2 + rect.x;
            int y = (rect.height - height) / 2 + rect.y;
            g.drawBitmap(x, y, width, height, bmp, 0, 0);
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    private boolean isNinePatch() {
        return npbmp != null;
    }
}
