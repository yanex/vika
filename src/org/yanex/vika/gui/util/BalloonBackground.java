package org.yanex.vika.gui.util;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.XYRect;

public class BalloonBackground extends RoundedBackground {

    private static final int RADIUS = 9;
    private static final int PADDING = BalloonBackground.px(1);

    private static int px(int pt) {
        return Ui.convertSize(pt, Ui.UNITS_pt, Ui.UNITS_px);
    }

    public BalloonBackground(int color, boolean left) {
        super(color, BalloonBackground.RADIUS);
        if (left) {
            super.setPadding(new XYEdges(BalloonBackground.PADDING, BalloonBackground.PADDING,
                    BalloonBackground.PADDING, BalloonBackground.PADDING * 3));
        } else {
            super.setPadding(new XYEdges(BalloonBackground.PADDING, BalloonBackground.PADDING * 3,
                    BalloonBackground.PADDING, BalloonBackground.PADDING));
        }
    }

    public void draw(Graphics g, XYRect r) {
        super.draw(g, r);

        XYEdges padding = getPadding();

        int x = r.x + padding.left;
        int y = r.y + padding.top;
        int width = r.width - padding.left - padding.right;
        int height = r.height - padding.top - padding.bottom;

        int x1, x2, x3, y1, y2, y3;

        if (padding.left > padding.right) {
            x1 = x;
            y1 = y + height - BalloonBackground.RADIUS / 2;

            x2 = x1 - BalloonBackground.PADDING * 2;
            y2 = y1 - BalloonBackground.PADDING;

            x3 = x1;
            y3 = y2 - BalloonBackground.PADDING;
        } else {
            x1 = x + width;
            y1 = y + height - BalloonBackground.RADIUS / 2;

            x2 = x1 + BalloonBackground.PADDING * 2;
            y2 = y1 - BalloonBackground.PADDING;

            x3 = x1;
            y3 = y2 - BalloonBackground.PADDING;
        }

        int[] xes = {x1, x2, x3};
        int[] yes = {y1, y2, y3};

        int oldColor = g.getColor();
        g.setColor(getColor());
        g.drawFilledPath(xes, yes, null, null);

        g.setColor(oldColor);
    }

    public void setPadding(XYEdges padding) {

    }

}
