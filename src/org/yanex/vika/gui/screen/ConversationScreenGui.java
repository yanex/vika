package org.yanex.vika.gui.screen;

import com.mobiata.bb.ui.decor.NinePatchBackground;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;
import org.yanex.vika.ConversationScreen;
import org.yanex.vika.api.item.Chat;
import org.yanex.vika.api.item.User;
import org.yanex.vika.api.util.Emoticons;
import org.yanex.vika.gui.dialog.EmoticonSelectDialog;
import org.yanex.vika.gui.list.List;
import org.yanex.vika.gui.util.R;
import org.yanex.vika.gui.util.Theme;
import org.yanex.vika.gui.widget.VkConversationTitleField;
import org.yanex.vika.gui.widget.VkLabelField;
import org.yanex.vika.gui.widget.base.*;
import org.yanex.vika.gui.widget.manager.RightFieldManager;
import org.yanex.vika.local.Local;
import org.yanex.vika.local.VikaResource;

public class ConversationScreenGui extends ScreenGui {

    public static final Bitmap addAttachmentsDefault = R.instance
            .getBitmap("Convs/Plus.png");

    public static final Bitmap addAttachmentsFocus = R.instance
            .getBitmap("Convs/PlusHover.png");

    public static final Bitmap addAttachmentsActive = R.instance
            .getBitmap("Convs/PlusActive.png");

    public static final Theme ATTACH_BUTTON_THEME, TEXT_THEME, CANCEL_THEME, FORWARD_THEME,
            DELETE_THEME, ATTACHMENT_ADD_THEME;

    static {
        ATTACH_BUTTON_THEME = new Theme();
        Background focusBackground = new NinePatchBackground("Convs/PlusFocusBg.png");
        Background activeBackground = new NinePatchBackground("Convs/PlusFocusBgPushed.png");
        ATTACH_BUTTON_THEME.setBackground(null, focusBackground,
                activeBackground, activeBackground);

        CANCEL_THEME = new Theme();
        Background cancelDefaultBackground = new NinePatchBackground(
                "Convs/ForwardingButtons/CancelButton.png");
        Background cancelFocusBackground = new NinePatchBackground(
                "Convs/ForwardingButtons/CancelButtonFocus.png");
        Background cancelActiveBackground = new NinePatchBackground(
                "Convs/ForwardingButtons/CancelButtonPushed.png");
        CANCEL_THEME.setBackground(cancelDefaultBackground,
                cancelFocusBackground, cancelActiveBackground, null);
        CANCEL_THEME.setPrimaryColor(0x7e7e7e);
        CANCEL_THEME.setSecondaryFontColor(0x7e7e7e);
        CANCEL_THEME.setPaddingEdges(DP1, DP1, DP1, DP1);

        FORWARD_THEME = new Theme();
        Background forwardDefaultBackground = new NinePatchBackground(
                "Convs/ForwardingButtons/ForwardButton.png");
        Background forwardFocusBackground = new NinePatchBackground(
                "Convs/ForwardingButtons/ForwardButtonFocus.png");
        Background forwardActiveBackground = new NinePatchBackground(
                "Convs/ForwardingButtons/ForwardButtonPushed.png");
        FORWARD_THEME.setBackground(forwardDefaultBackground,
                forwardFocusBackground, forwardActiveBackground, null);
        FORWARD_THEME.setPrimaryColor(0xFFFFFF);
        FORWARD_THEME.setSecondaryFontColor(0xFFFFFF);
        FORWARD_THEME.setPaddingEdges(DP1, DP1, DP1, DP1);

        DELETE_THEME = new Theme();
        Background deleteDefaultBackground = new NinePatchBackground(
                "Convs/ForwardingButtons/DeleteButton.png");
        Background deleteFocusBackground = new NinePatchBackground(
                "Convs/ForwardingButtons/DeleteButtonFocus.png");
        Background deleteActiveBackground = new NinePatchBackground(
                "Convs/ForwardingButtons/DeleteButtonPushed.png");
        DELETE_THEME.setBackground(deleteDefaultBackground,
                deleteFocusBackground, deleteActiveBackground, null);
        DELETE_THEME.setPrimaryColor(0xFFFFFF);
        DELETE_THEME.setSecondaryFontColor(0xFFFFFF);
        DELETE_THEME.setPaddingEdges(DP1, DP1, DP1, DP1);

        TEXT_THEME = new Theme();
        NinePatchBackground textBackground = new NinePatchBackground(
                R.instance.getNinepatch("Convs/Input.png"));
        TEXT_THEME.setBackground(textBackground, textBackground, textBackground, null);
        int p = Math.max(textBackground.getNinePatch().getPadding().left, DP2);
        TEXT_THEME.setPrimaryColor(0x000000);
        TEXT_THEME.setSecondaryFontColor(0xa9a9a9);
        TEXT_THEME.setPaddingEdges(p, p, p, p);
        TEXT_THEME.getBorderEdges().left = 0;

        ATTACHMENT_ADD_THEME = new Theme();
        Background attachesDefaultBackground = new NinePatchBackground("Convs/Attaches/ItemBg.png");
        Background attachesFocusedBackground = new NinePatchBackground(
                "Convs/Attaches/ItemBgFocus.png");
        ATTACHMENT_ADD_THEME.setBackground(attachesDefaultBackground,
                attachesFocusedBackground, attachesFocusedBackground, null);
    }

    public final VkConversationTitleField title;
    public final List list;
    public final EditTextField text;

    public final VerticalFieldManager bottom;
    public final HorizontalFieldManager bottomText;

    public final ImageSelectorField addAttachments;
    public final ImageButtonField insertEmoji;

    public final HorizontalFieldManager bottomContextWrapper;
    public final RightFieldManager bottomContext;
    public final ButtonField contextCancel;
    public final ButtonField contextForward;
    public final ButtonField contextDelete;

    public final HorizontalFieldManager bottomAttachments;

    public final VerticalFieldManager bottomLoading;
    public final CustomLabelField bottomLoadingText;

    public final CustomLabelField bottomTyping;
    public final VerticalFieldManager bottomTypingWrapper;

    public ConversationScreenGui(final ConversationScreen screen, User user, Chat chat) {
        screen.addMenuItem(new SendMenuItem(screen));

        title = user != null ?
                new VkConversationTitleField(user) : new VkConversationTitleField(chat);

        list = new List(List.MODE_INVERT);
        list.setOwner(screen);
        list.setListener(screen);
        list.setSeparatorHeight(0);
        list.setSeparatorColor(0xFFFFFF);
        Bitmap bg = R.instance.getBitmap("LightBg.png");

        Background bgb = BackgroundFactory.createSolidBackground(0xdfe4ee);

        screen.getMainManager().setBackground(bgb);
        screen.setBackground(bgb);

        bottom = new VerticalFieldManager(Field.USE_ALL_WIDTH);

        bottomTypingWrapper = new VerticalFieldManager(Field.USE_ALL_WIDTH);
        bottomTypingWrapper.setBackground(bgb);

        Theme typingTheme = new Theme();
        typingTheme.setPrimaryColor(0x718aa0);
        bottomTyping = new CustomLabelField("", 0, typingTheme);
        bottomTyping.setMargin(DP2, DP2, DP2, DP2);

        bottomText = new HorizontalFieldManager(Field.USE_ALL_WIDTH);
        Background inputBg = new NinePatchBackground(
                R.instance.getNinepatch("Convs/InputBg.png"));
        bottomText.setBackground(inputBg);

        bottomLoading = new VerticalFieldManager(Field.USE_ALL_WIDTH);
        bottomLoading.setBackground(inputBg);
        bottomLoadingText = new VkLabelField(tr(VikaResource.Uploading_images) + "...",
                Field.FIELD_HCENTER);
        bottomLoading.add(bottomLoadingText);

        addAttachments = new ImageSelectorField(addAttachmentsDefault,
                addAttachmentsFocus,
                addAttachmentsActive,
                addAttachmentsActive, DP10, DP10,
                Field.FIELD_VCENTER, ATTACH_BUTTON_THEME, false);

        insertEmoji = new ImageButtonField(Emoticons.instance.getByHexString("D83DDE0A"),
                DP10, DP10, Field.FIELD_VCENTER,
                ATTACH_BUTTON_THEME, false);

        text = new EditTextField(Field.FIELD_VCENTER | TextField.JUMP_FOCUS_AT_END,
                TEXT_THEME);

        text.setHint(tr(VikaResource.Write_message));

        bottomContextWrapper = new HorizontalFieldManager(Field.USE_ALL_WIDTH);
        bottomContextWrapper.setBackground(inputBg);
        bottomContextWrapper.setPadding(DP2, DP2, DP2, DP2);
        bottomContext = new RightFieldManager();

        contextCancel = new ButtonField(tr(VikaResource.Cancel), 0,
                CANCEL_THEME);
        contextForward = new ButtonField(tr(VikaResource.Forward_F), Field.FIELD_RIGHT,
                FORWARD_THEME);
        contextDelete = new ButtonField(tr(VikaResource.Delete_D), Field.FIELD_RIGHT,
                DELETE_THEME);

        bottomAttachments = new HorizontalFieldManager(Field.USE_ALL_WIDTH
                | Manager.HORIZONTAL_SCROLL);

        Background bottomAttachmentsBg = new NinePatchBackground("Convs/Attaches/Bg.png");
        bottomAttachments.setBackground(bottomAttachmentsBg);

        add(screen);
        setListeners(screen);
    }

    private void add(ConversationScreen screen) {
        screen.setBanner(title);

        screen.add(list);

        bottomContextWrapper.add(bottomContext);

        bottomText.add(addAttachments);
        bottomText.add(text);

        bottomContext.add(contextCancel);
        bottomContext.add(contextForward);
        bottomContext.add(contextDelete);

        bottom.add(bottomTypingWrapper);
        bottom.add(bottomText);

        screen.setStatus(bottom);
    }

    private void setListeners(final ConversationScreen screen) {
        text.setChangeListener(new FieldChangeListener() {

            public void fieldChanged(Field field, int context) {
                int a = 5;
                a = 6;
                screen.imTyping();
            }
        });

        text.setListener(new EditTextField.EditListener() {

            public boolean onButtonPressed(int key) {
                if (key == 10) {
                    screen.send();
                    return true;
                }
                return false;
            }

            public void pastButtonPressed(int key) {
                if (key > 30) {
                    // imTyping();
                }
            }

            public void postNavigationUnclick() {
                // imTyping();
            }
        });

        insertEmoji.setChangeListener(new FieldChangeListener() {

            public void fieldChanged(Field field, int context) {
                EmoticonSelectDialog dialog = new EmoticonSelectDialog();
                dialog.show();
                String s = dialog.getText();
                if (s != null) {
                    String t = text.getText();

                    String leftText = "";
                    String rightText = "";

                    if (text.getCursorPosition() > 0) {
                        leftText = t.substring(0, text.getCursorPosition());
                    }

                    if (text.getCursorPosition() < t.length()) {
                        rightText = t.substring(text.getCursorPosition());
                    }

                    t = leftText + " " + s + " " + rightText;
                    text.setText(t);
                }
            }

        });

        contextDelete.setChangeListener(new FieldChangeListener() {

            public void fieldChanged(Field field, int context) {
                screen.deleteMessages();
            }
        });

        contextForward.setChangeListener(new FieldChangeListener() {

            public void fieldChanged(Field field, int context) {
                screen.forwardMessages();
            }
        });
    }

    private class SendMenuItem extends MenuItem {
        private final ConversationScreen screen;

        public SendMenuItem(ConversationScreen screen) {
            super(Local.tr(VikaResource.Send), 10, 10);
            this.screen = screen;
        }

        public void run() {
            screen.send();
        }
    }

}
