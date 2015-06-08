package org.yanex.vika.gui.widget.manager;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;

public class RightFieldManager extends Manager {

    public RightFieldManager() {
        super(0);
    }

    public RightFieldManager(long style) {
        super(style);
    }

    protected void sublayout(int width, int height) {
        int totalHeight = 0;
        int totalWidth = 0;
        int totalLeftWidth = 0;
        int totalRightWidth = 0;
        int count = getFieldCount();
        int lastLeftField = -1;

        for (int i = count - 1; i >= 0; --i) {
            Field f = getField(i);
            if (totalHeight < f.getPreferredHeight()) {
                totalHeight = f.getPreferredHeight();
            }

            int pw = f.getPreferredWidth();
            totalWidth += pw;

            if ((f.getStyle() & Field.FIELD_RIGHT) > 0) {
                totalRightWidth += pw;
            } else {
                if (lastLeftField == -1) {
                    lastLeftField = i;
                }
                if (i > 0) {
                    totalLeftWidth += pw;
                }
            }
        }

        int curX = width - totalRightWidth;

        for (int i = lastLeftField + 1; i < count; ++i) {
            Field f = getField(i);
            int y = 0;

            int pw = f.getPreferredWidth();
            int ph = f.getPreferredHeight();

            if ((f.getStyle() & Field.FIELD_BOTTOM) > 0) {
                y = totalHeight - ph;
            } else if ((f.getStyle() & Field.FIELD_TOP) > 0) {
                y = 0;
            } else {
                y = (totalHeight - ph) / 2;
            }

            setPositionChild(f, curX, y);
            layoutChild(f, pw, ph);
            curX += pw;
        }

        if (totalWidth <= width) {
            curX = 0;

            for (int i = 0; i <= lastLeftField; ++i) {
                Field f = getField(i);
                int y = 0;

                int pw = f.getPreferredWidth();
                int ph = f.getPreferredHeight();

                if ((f.getStyle() & Field.FIELD_BOTTOM) > 0) {
                    y = totalHeight - ph;
                } else if ((f.getStyle() & Field.FIELD_TOP) > 0) {
                    y = 0;
                } else {
                    y = (totalHeight - ph) / 2;
                }

                setPositionChild(f, curX, y);
                layoutChild(f, pw, ph);
                curX += pw;
            }
        } else {
            Field ff = getField(0);

            int ppw = ff.getPreferredWidth();
            int pph = ff.getPreferredHeight();
            int y = 0;

            if ((ff.getStyle() & Field.FIELD_BOTTOM) > 0) {
                y = totalHeight - pph;
            } else if ((ff.getStyle() & Field.FIELD_TOP) > 0) {
                y = 0;
            } else {
                y = (totalHeight - pph) / 2;
            }

            setPositionChild(ff, 0, y);
            layoutChild(ff, width - totalLeftWidth - totalRightWidth, height);

            curX = width - totalLeftWidth - totalRightWidth;

            for (int i = 1; i <= lastLeftField; ++i) {
                Field f = getField(i);

                int pw = f.getPreferredWidth();
                int ph = f.getPreferredHeight();

                if ((f.getStyle() & Field.FIELD_BOTTOM) > 0) {
                    y = totalHeight - ph;
                } else if ((f.getStyle() & Field.FIELD_TOP) > 0) {
                    y = 0;
                } else {
                    y = (totalHeight - ph) / 2;
                }

                setPositionChild(f, curX, y);
                layoutChild(f, pw, ph);
                curX += pw;
            }
        }

        setExtent(width, totalHeight);
    }

}
