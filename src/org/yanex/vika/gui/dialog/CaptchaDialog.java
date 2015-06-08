package org.yanex.vika.gui.dialog;

import com.mobiata.bb.ui.decor.NinePatchBackground;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Background;
import org.yanex.vika.gui.util.Files;
import org.yanex.vika.gui.util.Fonts;
import org.yanex.vika.gui.util.GuiItem;
import org.yanex.vika.gui.util.Theme;
import org.yanex.vika.gui.widget.VkEditTextField;
import org.yanex.vika.gui.widget.base.ButtonField;
import org.yanex.vika.gui.widget.base.CustomLabelField;
import org.yanex.vika.gui.widget.base.EditTextField;
import org.yanex.vika.gui.widget.base.GifAnimationField;
import org.yanex.vika.local.VikaResource;
import org.yanex.vika.util.network.ImageLoader;
import org.yanex.vika.util.network.ImageLoaderCallback;

import javax.microedition.lcdui.TextField;

public class CaptchaDialog extends VkScreen implements
        FieldChangeListener, ImageLoaderCallback, GuiItem {

    private static final int MAX_PHOTO_LOAD_ATTEMPTS = 3;

    private static final Theme labelTheme =
            new Theme().setPrimaryColor(0xE0E0E0).setPaddingEdges(DP1, DP1, DP1, DP1);

    private VerticalFieldManager captchaWrapper;
    private EditTextField text;
    private ButtonField ok;
    private ButtonField cancel;

    private BitmapField captchaField;
    private int captchaLoadAttempts = 0;

    private String code = null;

    private String url;

    public CaptchaDialog() {
        super(new VerticalFieldManager(Manager.VERTICAL_SCROLL));
        setFont(Fonts.defaultFont);

        setBackground(new NinePatchBackground("Convs/AttachesMenu/Bg.png"));

        CustomLabelField l = new CustomLabelField(tr(VikaResource.Enter_captcha),
                Field.FIELD_HCENTER,
                CaptchaDialog.labelTheme);

        captchaField = new BitmapField();

        captchaWrapper = new VerticalFieldManager(Field.FIELD_HCENTER);
        captchaWrapper.setMargin(DP2, 0, DP1, 0);

        GifAnimationField animation = new GifAnimationField("loading.gif", Field.FIELD_HCENTER);
        animation.startAnimation();
        captchaWrapper.add(animation);

        text = new VkEditTextField(TextField.NON_PREDICTIVE
                | net.rim.device.api.ui.component.TextField.NO_LEARNING
                | net.rim.device.api.ui.component.TextField.NO_COMPLEX_INPUT
                | Field.NON_SPELLCHECKABLE | net.rim.device.api.ui.component.TextField.NO_NEWLINE);
        text.setNonSpellCheckable(true);
        text.setMargin(0, 0, -DP1, 0);

        text.setListener(new EditTextField.EditListener() {

            public boolean onButtonPressed(int key) {
                if (key == 10) {
                    fieldChanged(ok, 0);
                    return true;
                }
                return false;
            }

            public void pastButtonPressed(int key) {

            }
        });

        Theme theme = new Theme();
        theme.setPrimaryColor(0x000000);
        theme.setSecondaryFontColor(0xFFFFFF);

        Background defaultBackground = new NinePatchBackground(Files.DARK_BUTTON);
        Background focusBackground = new NinePatchBackground(Files.DARK_BUTTON_FOCUS);
        Background activeBackground = new NinePatchBackground(Files.DARK_BUTTON_FOCUS_PUSHED);

        theme.setBackground(defaultBackground, focusBackground, activeBackground, null);

        theme.setPaddingEdges(0, DP1, 0, DP1);

        ok = new ButtonField("ОК", 0, theme);
        cancel = new ButtonField(tr(VikaResource.Cancel), 0, theme);
        ok.setMargin(0, DP2, 0, 0);
        cancel.setMargin(0, 0, 0, DP2);

        ok.setChangeListener(this);
        cancel.setChangeListener(this);

        HorizontalFieldManager buttonWrapper = new HorizontalFieldManager(Field.FIELD_HCENTER);
        buttonWrapper.setMargin(0, 0, DP4, 0);
        buttonWrapper.add(ok);
        buttonWrapper.add(cancel);

        add(l);
        add(captchaWrapper);
        add(text);
        add(buttonWrapper);
    }

    public void dismiss() {
        UiApplication.getUiApplication().popScreen(this);
    }

    public void fieldChanged(Field f, int arg1) {
        if (f == ok) {
            code = text.getText();
            dismiss();
        } else if (f == cancel) {
            code = null;
            dismiss();
        }
    }

    public String getCode() {
        return code;
    }

    private void loadCaptcha() {
        captchaLoadAttempts++;
        ImageLoader.instance.load(url, "captcha", -1, -1, true, false, this);
    }

    public void onError(String url, String tag) {
        if (captchaLoadAttempts < CaptchaDialog.MAX_PHOTO_LOAD_ATTEMPTS) {
            loadCaptcha();
        }
    }

    public void onLoad(String url, String tag, Bitmap bmp) {
        captchaField.setBitmap(bmp);
        captchaWrapper.deleteAll();
        captchaWrapper.add(captchaField);
        invalidate();
    }

    public CaptchaDialog show(String url) {
        this.url = url;
        loadCaptcha();
        UiApplication.getUiApplication().pushModalScreen(this);
        return this;
    }

    protected void sublayout(int width, int height) {
        layoutDelegate(width - 80, height - 80);

        int desiredWidth = Math.max(getDelegate().getWidth() + 20, width / 2);
        setExtent(Math.min(width - 60, desiredWidth), Math.min(height - 60, getDelegate().getHeight() + 20));
        setPositionDelegate((getContentWidth() - getDelegate().getWidth()) / 2, 10);
        setPosition((width - getWidth()) / 2, (height - getHeight()) / 2);
    }
}
