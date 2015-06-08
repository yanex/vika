package org.yanex.vika.gui.dialog;

import com.mobiata.bb.ui.decor.NinePatchBackground;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Background;
import org.yanex.vika.gui.util.Fonts;
import org.yanex.vika.gui.util.GuiItem;
import org.yanex.vika.gui.util.Theme;
import org.yanex.vika.gui.widget.base.ButtonField;
import org.yanex.vika.gui.widget.base.CustomLabelField;

public class ListBoxDialog extends Screen implements GuiItem {

    private static final Theme THEME_BUTTON, THEME_LABEL;

    static {
        THEME_LABEL = new Theme();
        ListBoxDialog.THEME_LABEL.setPrimaryColor(0xffffff);

        THEME_BUTTON = new Theme();
        ListBoxDialog.THEME_BUTTON.setPrimaryColor(0xffffff);
        ListBoxDialog.THEME_BUTTON.setSecondaryFontColor(0xffffff);
        ListBoxDialog.THEME_BUTTON.setPaddingEdges(DP1, DP1, DP1, DP1);

        Background focusBackground = new NinePatchBackground("Convs/AttachesMenu/FocusBg.png");
        ListBoxDialog.THEME_BUTTON.setBackground(null, focusBackground, focusBackground, null);
    }

    private int selection = -1;

    public ListBoxDialog(String label, String[] items) {
        super(new VerticalFieldManager());
        setFont(Fonts.narrow(7));

        VerticalFieldManager vfm = new VerticalFieldManager(Manager.VERTICAL_SCROLL
                | Manager.VERTICAL_SCROLLBAR);
        vfm.setPadding(px(2), px(2), px(2), px(2));

        if (label != null && label.length() > 0) {
            CustomLabelField l = new CustomLabelField(label, DrawStyle.HCENTER, ListBoxDialog.THEME_LABEL);
            l.setPadding(0, 0, px(3), 0);
            vfm.add(l);
        }

        ButtonField[] buttons = new ButtonField[items.length];

        int maxWidth = 0;

        for (int i = 0; i < items.length; ++i) {
            final int index = i;

            ButtonField b = buttons[i] = new ButtonField(items[i], 0, ListBoxDialog.THEME_BUTTON);
            b.setChangeListener(new FieldChangeListener() {

                public void fieldChanged(Field field, int context) {
                    selection = index;
                    dismiss();
                }
            });
            maxWidth = Math.max(maxWidth, b.getPreferredWidth());
            vfm.add(b);
        }

        for (int i = 0; i < items.length; ++i) {
            buttons[i].setFixedWidth(maxWidth);
        }

        add(vfm);

        setBackground(new NinePatchBackground("Convs/AttachesMenu/Bg.png"));
    }

    public ListBoxDialog(String[] items) {
        this(null, items);
    }

    public void dismiss() {
        if (isVisible()) {
            this.close();
        }
    }

    public int getSelection() {
        return selection;
    }

    protected boolean keyChar(char c, int status, int time) {
        if (c == 27) {
            dismiss();
            return true;
        }

        return super.keyChar(c, status, time);
    }

    protected int px(int pt) {
        return Ui.convertSize(pt, Ui.UNITS_pt, Ui.UNITS_px);
    }

    public ListBoxDialog show() {
        if (!isVisible()) {
            UiApplication.getUiApplication().pushModalScreen(this);
        }
        return this;
    }

    protected void sublayout(int width, int height) {
        layoutDelegate(width - 80, height - 80);

        int desiredWidth = getDelegate().getWidth() + 20;

        setExtent(Math.min(width - 60, desiredWidth),
                Math.min(height - 60, getDelegate().getHeight() + 20));

        setPositionDelegate((getContentWidth() - getDelegate().getWidth()) / 2, 10);

        setPosition((width - getWidth()) / 2, (height - getHeight()) / 2);
    }
}
