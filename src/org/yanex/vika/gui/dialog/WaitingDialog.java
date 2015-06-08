package org.yanex.vika.gui.dialog;

import com.mobiata.bb.ui.decor.NinePatchBackground;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.VerticalFieldManager;
import org.yanex.vika.gui.util.Fonts;
import org.yanex.vika.gui.util.GuiItem;
import org.yanex.vika.gui.util.Theme;
import org.yanex.vika.gui.widget.base.CustomLabelField;
import org.yanex.vika.gui.widget.base.GifAnimationField;

public class WaitingDialog extends VkScreen implements GuiItem {

    public static interface WaitingDialogListener {
        public void onCancel();
    }

    private static Theme labelTheme;

    static {
        WaitingDialog.labelTheme = new Theme();
        WaitingDialog.labelTheme.setPrimaryColor(0xE0E0E0);
        WaitingDialog.labelTheme.setPaddingEdges(DP1, DP2, DP4, DP2);
    }

    private boolean cancellable = false;
    private WaitingDialogListener listener = null;

    public WaitingDialog(String text) {
        super(new VerticalFieldManager());
        setFont(Fonts.defaultFont);

        CustomLabelField l = new CustomLabelField(text, Field.FIELD_HCENTER, WaitingDialog.labelTheme);

        GifAnimationField animation = new GifAnimationField("loading.gif", Field.FIELD_HCENTER);
        animation.setPadding(0, 0, px(4), 0);
        animation.startAnimation();

        setBackground(new NinePatchBackground("Convs/AttachesMenu/Bg.png"));

        add(l);
        add(animation);
    }

    public void dismiss() {
        // if (isVisible())
        this.close();
    }

    public WaitingDialogListener getListener() {
        return listener;
    }

    public boolean isCancellable() {
        return cancellable;
    }

    protected boolean keyChar(char c, int status, int time) {
        if (c == 27) {
            if (cancellable) {
                this.close();
                if (listener != null) {
                    listener.onCancel();
                }
            }
            return true;
        } else {
            return super.keyChar(c, status, time);
        }
    }

    protected int px(int pt) {
        return Ui.convertSize(pt, Ui.UNITS_pt, Ui.UNITS_px);
    }

    public void setCancellable(boolean cancellable) {
        this.cancellable = cancellable;
    }

    public void setListener(WaitingDialogListener listener) {
        this.listener = listener;
    }

    public WaitingDialog show() {
        if (!isVisible()) {
            UiApplication.getUiApplication().pushScreen(this);
        }
        return this;
    }

    protected void sublayout(int width, int height) {
        layoutDelegate(width - 80, height - 80);

        int desiredWidth = Math.max(getDelegate().getWidth() + 20, width / 2);

        setExtent(Math.min(width - 60, desiredWidth),
                Math.min(height - 60, getDelegate().getHeight() + 20));

        setPositionDelegate((getContentWidth() - getDelegate().getWidth()) / 2, 10);

        setPosition((width - getWidth()) / 2, (height - getHeight()) / 2);
    }
}
