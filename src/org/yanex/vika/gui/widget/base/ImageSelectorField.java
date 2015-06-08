package org.yanex.vika.gui.widget.base;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYDimension;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.decor.Background;
import org.yanex.vika.gui.util.Theme;

public class ImageSelectorField extends FocusableField {

    private AbstractBitmapField defaultBmp;
    private AbstractBitmapField focusBmp;
    private AbstractBitmapField activeBmp;
    private AbstractBitmapField selectedBmp;

    private boolean isSelected = false;

    private int imageWidth;
    private int imageHeight;

    private int fullWidth;
    private int fullHeight;

    private boolean scale;

    public ImageSelectorField(Bitmap defaultBmp, Bitmap focusBmp, Bitmap activeBmp,
                              Bitmap selectedBmp,
                              int width, int height, long style, Theme theme, boolean scale) {
        super(style, theme);

        this.scale = scale;

        XYEdges padding = theme.getPaddingEdges();

        fullWidth = width;
        imageWidth = fullWidth - padding.left - padding.right;
        fullHeight = height;
        imageHeight = fullHeight - padding.top - padding.bottom;

        XYDimension dim = new XYDimension(imageWidth, imageHeight);
        this.defaultBmp = new AbstractBitmapField(defaultBmp, dim, scale);
        this.focusBmp = new AbstractBitmapField(focusBmp, dim, scale);
        this.activeBmp = new AbstractBitmapField(activeBmp, dim, scale);

        if (activeBmp != selectedBmp) {
            this.selectedBmp = new AbstractBitmapField(selectedBmp, dim, scale);
        } else {
            this.selectedBmp = this.activeBmp;
        }
    }

    protected Background getCurrentBackground() {
        if (isSelected && !isActive() && !isFocused()) {
            return getTheme().getSelectedBackground();
        } else {
            return super.getCurrentBackground();
        }
    }

    public int getPreferredHeight() {
        XYEdges b = getTheme().getBorderEdges();

        return b.top + fullHeight + b.bottom;
    }

    public int getPreferredWidth() {
        XYEdges b = getTheme().getBorderEdges();

        if (isStyle(Field.USE_ALL_WIDTH)) {
            return Integer.MAX_VALUE;
        } else {
            return b.left + fullWidth + b.right;
        }
    }

    public boolean isSelected() {
        return isSelected;
    }

    protected void paint(Graphics g) {
        AbstractBitmapField bmp;

        if (isSelected && !isActive()) {
            bmp = selectedBmp;
        } else if (isActive()) {
            bmp = activeBmp;
        } else if (isFocused()) {
            bmp = focusBmp;
        } else {
            bmp = defaultBmp;
        }

        if (bmp != null) {
            bmp.draw(g, 0, 0, getContentWidth(), getContentHeight());
        }
    }

    public void select() {
        isSelected = true;
        invalidate();
    }

    public void setBitmaps(Bitmap defaultBmp, Bitmap focusBmp, Bitmap activeBmp, Bitmap selectedBmp) {
        XYDimension dim = new XYDimension(imageWidth, imageHeight);
        this.defaultBmp = new AbstractBitmapField(defaultBmp, dim, scale);
        this.focusBmp = new AbstractBitmapField(focusBmp, dim, scale);
        this.activeBmp = new AbstractBitmapField(activeBmp, dim, scale);
        if (activeBmp != selectedBmp) {
            this.selectedBmp = new AbstractBitmapField(selectedBmp, dim, scale);
        } else {
            this.selectedBmp = this.activeBmp;
        }
    }

    public void unselect() {
        isSelected = false;
        invalidate();
    }
}
