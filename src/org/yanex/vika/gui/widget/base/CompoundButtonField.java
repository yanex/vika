package org.yanex.vika.gui.widget.base;

import com.mobiata.bb.ui.decor.NinePatchBackground;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Background;
import org.yanex.vika.gui.util.Theme;
import org.yanex.vika.gui.widget.manager.FocusableHFM;
import org.yanex.vika.util.StringUtils;

public class CompoundButtonField extends FocusableHFM {

    private static final Background BACKGROUND_FOCUS =
            new NinePatchBackground("Convs/AttachesMenu/FocusBg.png");

    private static final Theme THEME = new Theme()
            .setBackground(null, BACKGROUND_FOCUS, BACKGROUND_FOCUS, null);

    private static final Theme LABEL_THEME = new Theme().setPrimaryColor(0xffffff);

    public CompoundButtonField(String text, Bitmap b) {
        super(0, CompoundButtonField.THEME);

        VerticalFieldManager vfm = new VerticalFieldManager();
        vfm.setPadding(DP2, DP2, DP2, DP2);

        BitmapField bitmap = new BitmapField(b, Field.FIELD_HCENTER);
        vfm.add(bitmap);

        String[] lines = StringUtils.split(text, "\n");
        for (int i = 0; i < lines.length; ++i) {
            CustomLabelField label = new CustomLabelField(lines[i],
                    DrawStyle.HCENTER | Field.FIELD_HCENTER,
                    CompoundButtonField.LABEL_THEME);
            vfm.add(label);
        }

        add(vfm);

        addingCompleted();
    }

    public void fieldChanged(Field field, int context) {
        getChangeListener().fieldChanged(this, context);
    }

}
