package org.yanex.vika.gui.util;

import com.mobiata.bb.ui.decor.NinePatchBackground;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.decor.Background;

public class Theme {
    private Background defaultBackground = null;
    private Background focusedBackground = null;
    private Background activeBackground = null;
    private Background selectedBackground = null;

    private int fontColor = 0;
    private int secondaryFontColor = 0;

    private XYEdges borderEdges = new XYEdges();
    private XYEdges paddingEdges = new XYEdges();

    public Background getDefaultBackground() {
        return defaultBackground;
    }

    public Background getFocusBackground() {
        return focusedBackground;
    }

    public Background getActiveBackground() {
        return activeBackground;
    }

    public Background getSelectedBackground() {
        return selectedBackground;
    }

    public XYEdges getBorderEdges() {
        return borderEdges;
    }

    public XYEdges getPaddingEdges() {
        return paddingEdges;
    }

    public int getPrimaryColor() {
        return fontColor;
    }

    public int getSecondaryFontColor() {
        return secondaryFontColor;
    }

    public Theme setBackground(Background defaultBackground, Background focusedBackground,
                               Background activeBackground, Background selectedBackground) {
        this.defaultBackground = defaultBackground;
        this.focusedBackground = focusedBackground;
        this.activeBackground = activeBackground;
        this.selectedBackground = selectedBackground;

        if (defaultBackground != null && defaultBackground instanceof NinePatchBackground) {
            borderEdges = ((NinePatchBackground) defaultBackground).getNinePatch().getPadding();
        } else if (focusedBackground != null && focusedBackground instanceof NinePatchBackground) {
            borderEdges = ((NinePatchBackground) focusedBackground).getNinePatch().getPadding();
        }
        return this;
    }

    public Theme setBorderEdges(int top, int right, int bottom, int left) {
        borderEdges.set(top, right, bottom, left);
        return this;
    }

    public Theme setPaddingEdges(int top, int right, int bottom, int left) {
        paddingEdges.set(top, right, bottom, left);
        return this;
    }

    public Theme setPaddingEdges(XYEdges edges) {
        if (edges != null) {
            this.paddingEdges = edges;
        } else {
            this.paddingEdges.set(0, 0, 0, 0);
        }
        return this;
    }

    public Theme setPrimaryColor(int fontColor) {
        this.fontColor = fontColor;
        return this;
    }

    public Theme setSecondaryFontColor(int secondFontColor) {
        this.secondaryFontColor = secondFontColor;
        return this;
    }

}
