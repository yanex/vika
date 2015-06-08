package org.yanex.vika.gui.dialog;

import com.mobiata.bb.ui.decor.NinePatchBackground;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.GridFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import org.yanex.vika.gui.util.Fonts;

public class EmoticonSelectDialog extends VkScreen {

    private String text = null;

    public EmoticonSelectDialog() {
        super(new VerticalFieldManager());
        setFont(Fonts.narrow(6));

        GridFieldManager gfm = new GridFieldManager(4, 8, 0);
        gfm.setPadding(DP2, DP2, DP2, DP2);

        add(gfm);

        setBackground(new NinePatchBackground("Convs/AttachesMenu/Bg.png"));
    }

    public void dismiss() {
        if (isVisible()) {
            this.close();
        }
    }

    public String getText() {
        return text;
    }

    protected boolean keyChar(char c, int status, int time) {
        if (c == KEY_BACK) {
            dismiss();
            return true;
        }

        return super.keyChar(c, status, time);
    }

    public EmoticonSelectDialog show() {
        if (!isVisible()) {
            UiApplication.getUiApplication().pushModalScreen(this);
        }
        return this;
    }

    protected void sublayout(int width, int height) {
        layoutDelegate(width - 80, height - 80);

        int desiredWidth = getDelegate().getWidth() + 20;
        setExtent(Math.min(width - 60, desiredWidth), Math.min(height - 60, getDelegate().getHeight() + 20));
        setPositionDelegate((getContentWidth() - getDelegate().getWidth()) / 2, 10);
        setPosition((width - getWidth()) / 2, (height - getHeight()) / 2);
    }
}
