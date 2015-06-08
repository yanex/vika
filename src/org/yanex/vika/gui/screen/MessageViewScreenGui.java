package org.yanex.vika.gui.screen;

import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYDimension;
import net.rim.device.api.ui.component.ActiveRichTextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;
import org.yanex.vika.gui.util.Fonts;
import org.yanex.vika.gui.util.R;
import org.yanex.vika.gui.util.RoundedBackground;
import org.yanex.vika.gui.util.Theme;
import org.yanex.vika.gui.widget.VkCompactTitleField;
import org.yanex.vika.gui.widget.base.AutoLoadingBitmapField;
import org.yanex.vika.gui.widget.base.CustomLabelField;
import org.yanex.vika.gui.widget.base.TextField;
import org.yanex.vika.local.VikaResource;

import java.util.Vector;

public class MessageViewScreenGui extends ScreenGui {

    private static Background OUTER_BG = new RoundedBackground(0xefefef, 6, true, 0xcccccc);

    private static Background INNER_BG = BackgroundFactory.createBitmapBackground(R.instance
            .getBitmap("LightBg.png"));

    public static final Theme BLACK_THEME = new Theme().setPrimaryColor(0),
            GRAY_THEME = new Theme().setPrimaryColor(0x91a4b6),
            BLUE_THEME = new Theme().setPrimaryColor(0x4f7ca3);

    public final AutoLoadingBitmapField photo;
    public final CustomLabelField name;
    public final CustomLabelField time;
    public final TextField text;
    public final ActiveRichTextField activeText;

    public final VerticalFieldManager outerLayout;
    public final VerticalFieldManager mainLayout;

    public MessageViewScreenGui(final VkMainScreen screen) {
        screen.setFont(Fonts.defaultFont);
        screen.getMainManager().setBackground(INNER_BG);

        outerLayout = new VerticalFieldManager(Field.FIELD_HCENTER);
        outerLayout.setBackground(OUTER_BG);
        outerLayout.setMargin(DP3, DP8, DP3, DP8);

        mainLayout = new VerticalFieldManager();
        mainLayout.setPadding(DP2, DP2, DP2, DP2);
        outerLayout.add(mainLayout);

        VkCompactTitleField title = new VkCompactTitleField(tr(VikaResource.View_message));

        photo = new AutoLoadingBitmapField(new XYDimension(DP14, DP14), 0, true);
        photo.setMargin(0, DP2, 0, 0);

        name = new CustomLabelField("", 0, BLACK_THEME);
        name.setFont(Fonts.bold(7));

        time = new CustomLabelField("", 0, GRAY_THEME);
        time.setFont(Fonts.narrow(6));

        text = new TextField("", 0);
        text.setColor(0x4f7ca3);

        activeText = new ActiveRichTextField("");

        VerticalFieldManager nameLayout = new VerticalFieldManager();
        nameLayout.add(name);
        nameLayout.add(time);

        HorizontalFieldManager topLayout = new HorizontalFieldManager(Field.USE_ALL_WIDTH);
        topLayout.setBackground(BackgroundFactory.createSolidBackground(0xefefef));
        topLayout.setPadding(DP1, DP2, DP1, DP2);
        topLayout.add(photo);
        topLayout.add(nameLayout);

        VerticalFieldManager banner = new VerticalFieldManager();
        banner.add(title);
        banner.add(topLayout);
        screen.setBanner(banner);
    }

    private void addAll(Manager manager, Vector fields) {
        Field[] a = new Field[fields.size()];
        fields.copyInto(a);
        manager.addAll(a);
    }

    public void addAttachments(Vector fields, String counter) {
        if (fields.size() > 0 && counter != null && counter.length() > 0) {
            CustomLabelField utilLabel = new CustomLabelField(counter,
                    Field.FIELD_HCENTER | DrawStyle.HCENTER, MessageViewScreenGui.BLUE_THEME);
            utilLabel.setMargin(0, 0, DP1, 0);
            fields.addElement(utilLabel);
        }
        addAll(mainLayout, fields);
    }

    public void addBody(String body) {
        if (testActive(body)) {
            activeText.setText(body);
            mainLayout.add(activeText);
        } else {
            text.setText(body);
            mainLayout.add(text);
        }
    }

    private boolean testActive(String text) {
        return false;
    }

}
