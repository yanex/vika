package org.yanex.vika.gui.widget.base;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import org.yanex.vika.gui.util.Fonts;
import org.yanex.vika.gui.util.TextDrawHelper;
import org.yanex.vika.gui.util.Theme;
import org.yanex.vika.util.fun.RichVector;

import java.util.Vector;

public class PaneCaptionField extends FocusableField {

    public interface PaneCaptionFeedback {
        void moveLeft(boolean fromCaption);

        void moveRight(boolean fromCaption);
    }

    private Vector titles;

    private int current;

    private int dx;

    private static final Font font = Fonts.narrow(DP6 + DP1 / 2,
            Ui.UNITS_px);

    private PaneCaptionFeedback feedback;

    private static final int HEIGHT;

    static {
        HEIGHT = DP9;
    }

    public PaneCaptionField(Theme theme) {
        super(Field.USE_ALL_WIDTH, theme);
    }

    public String getCurrentText() {
        return (String) titles.elementAt(current);
    }

    public int getPreferredHeight() {
        return PaneCaptionField.HEIGHT;
    }

    public int getPreferredWidth() {
        return Integer.MAX_VALUE;
    }

    protected boolean navigationMovement(int dx, int dy, int status, int time) {
        if (dy == 0) {
            if (dx < 0) {
                if (feedback != null) {
                    feedback.moveLeft(true);
                }
            } else if (dx > 0) {
                if (feedback != null) {
                    feedback.moveRight(true);
                }
            }

            return true;
        } else {
            return super.navigationMovement(dx, dy, status, time);
        }
    }

    protected void paint(Graphics g) {
        int oldColor = g.getColor();
        int oldAlpha = g.getGlobalAlpha();

        int height = getContentHeight(), width = getContentWidth();

        int color = getTheme().getPrimaryColor();
        if (isFocused() || isActive()) {
            color = getTheme().getSecondaryFontColor();
        }

        try {
            Font f = PaneCaptionField.font;

            int y = (height - f.getHeight()) / 2;

            int centerX = width / 2 - dx;

            g.setGlobalAlpha(255);
            g.setFont(f);
            g.setColor(color);

            int curX = centerX;
            for (int i = 0; i < titles.size(); ++i) {
                String t = (String) titles.elementAt(i);
                int x = curX - f.getAdvance(t) / 2;

                if (i < current) {
                    TextDrawHelper.drawAlphaGradientString(t, g, x, y, true);
                } else if (i > current) {
                    TextDrawHelper.drawAlphaGradientString(t, g, x, y, false);
                } else {
                    g.drawText(t, x, y);
                }
                curX += width / 2;
                if (curX > width) {
                    break;
                }
            }
        } finally {
            g.setColor(oldColor);
            g.setGlobalAlpha(oldAlpha);
        }
    }

    public void setCurrent(int current) {
        this.current = current;
        invalidate();
    }

    public void setDx(int dx) {
        this.dx = dx / 2;
        invalidate();
    }

    public void setFeedback(PaneCaptionFeedback feedback) {
        this.feedback = feedback;
    }

    public void setTitles(RichVector titles) {
        this.titles = titles;
        invalidate();
    }

}
