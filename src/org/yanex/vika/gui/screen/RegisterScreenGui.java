package org.yanex.vika.gui.screen;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;
import org.yanex.vika.RegisterScreen;
import org.yanex.vika.gui.util.Display;
import org.yanex.vika.gui.util.Files;
import org.yanex.vika.gui.util.Fonts;
import org.yanex.vika.gui.util.R;
import org.yanex.vika.gui.widget.VkButtonField;
import org.yanex.vika.gui.widget.VkEditTextField;
import org.yanex.vika.gui.widget.base.ButtonField;
import org.yanex.vika.gui.widget.base.EditTextField;
import org.yanex.vika.gui.widget.base.FocusableBitmapField;
import org.yanex.vika.gui.widget.base.VerticalSpacerField;
import org.yanex.vika.gui.widget.manager.VerticalCenterFieldManager;
import org.yanex.vika.local.VikaResource;

public class RegisterScreenGui extends ScreenGui implements FieldChangeListener {

    private static final Background DARK_BG =
            BackgroundFactory.createBitmapBackground(R.instance.getBitmap(Files.DARK_BG));

    private final RegisterScreen screen;

    public EditTextField phone;
    public EditTextField firstName;
    public EditTextField familyName;
    public ButtonField register;

    public RegisterScreenGui(RegisterScreen screen) {
        this.screen = screen;

        screen.setFont(Fonts.defaultFont);
        screen.setTitle((Field) null);

        final boolean isTall = Display.isTall();

        final VerticalCenterFieldManager root = new VerticalCenterFieldManager(
                Field.USE_ALL_HEIGHT | Field.USE_ALL_WIDTH | Manager.VERTICAL_SCROLL);
        root.setBackground(DARK_BG);
        root.setPadding(DP1, 0, DP1, 0);

        final BitmapField logo = new FocusableBitmapField(
                R.instance.getBitmap(Files.REGISTERFORM_VK), Field.FIELD_HCENTER);
        logo.setMargin(0, 0, DP4, 0);

        phone = new VkEditTextField(TextField.NO_NEWLINE | BasicEditField.FILTER_PHONE);
        phone.setMargin(-DP1, DP10, -DP1, DP10);
        phone.setHint(tr(VikaResource.Your_phone_number));
        phone.setFocusListener(screen);

        firstName = new VkEditTextField(TextField.NO_NEWLINE);
        firstName.setMargin(-DP1, DP10, -DP1, DP10);
        firstName.setHint(tr(VikaResource.Your_name));
        firstName.setFocusListener(screen);

        familyName = new VkEditTextField(TextField.NO_NEWLINE);
        familyName.setMargin(-DP1, DP10, -DP1, DP10);
        familyName.setHint(tr(VikaResource.Your_family_name));
        familyName.setFocusListener(screen);

        register = new VkButtonField(tr(VikaResource.Sign_up), Field.FIELD_HCENTER);
        register.setChangeListener(this);
        register.setPadding(0, DP2, 0, DP2);
        register.setFont(Fonts.bold(DP6 + DP1 / 2, Ui.UNITS_px));

        root.add(logo);
        root.add(phone);
        root.add(firstName);
        root.add(familyName);
        root.add(new VerticalSpacerField(isTall ? DP5 : DP1));
        root.add(register);
        root.add(new VerticalSpacerField(isTall ? DP5 : DP1));

        VerticalFieldManager scroller = new VerticalFieldManager(Manager.VERTICAL_SCROLL);
        scroller.add(root);
        screen.add(scroller);
    }

    public void fieldChanged(final Field field, final int context) {
        if (field == register) {
            final String userPhone = phone.getText();
            final String userFirstName = firstName.getText();
            final String userLastName = familyName.getText();

            if (!RegisterScreen.isCorrectName(userFirstName) || !RegisterScreen.isCorrectName(userLastName)) {
                Dialog.alert(tr(VikaResource.Enter_your_real));
                return;
            }

            screen._register(userPhone, userFirstName, userLastName);
        }
    }

}
