package org.yanex.vika.gui.screen;

import com.mobiata.bb.ui.decor.NinePatchBackground;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import org.yanex.vika.ChatEditScreen;
import org.yanex.vika.gui.list.List;
import org.yanex.vika.gui.util.Fonts;
import org.yanex.vika.gui.util.R;
import org.yanex.vika.gui.util.Theme;
import org.yanex.vika.gui.widget.base.*;
import org.yanex.vika.gui.widget.manager.RightFieldManager;
import org.yanex.vika.local.CountHelper;
import org.yanex.vika.local.VikaResource;

public class ChatEditScreenGui extends ScreenGui implements FieldChangeListener {

    private static final NinePatchBackground
            HEADER_BACKGROUND = new NinePatchBackground("Convs/Header.png"),
            INPUT_BACKGROUND = new NinePatchBackground("Convs/MultiEditInput.png"),
            BUTTON_DEFAULT = new NinePatchBackground("Other/ContactsSyncButton.png"),
            BUTTON_PUSHED = new NinePatchBackground("Other/ContactsSyncButtonPushed.png"),
            MULTIHEADER_DEFAULT = new NinePatchBackground("Convs/MultiHeaderRightButton.png"),
            MULTIHEADER_HOVER = new NinePatchBackground("Convs/MultiHeaderRightButtonHover.png");

    private static final Bitmap MULTI_ADD = R.instance.getBitmap("Convs/MultiAdd.png");

    private static final Theme HEADER_LABEL_FRAME = new Theme()
            .setPrimaryColor(0xffffff)
            .setPaddingEdges(0, DP2, 0, DP2);

    private static final Theme EDIT_THEME = new Theme()
            .setPrimaryColor(0x000000)
            .setBackground(INPUT_BACKGROUND, INPUT_BACKGROUND, INPUT_BACKGROUND, null)
            .setBorderEdges(DP2, DP2, DP2, DP2)
            .setPaddingEdges(INPUT_BACKGROUND.getNinePatch()
                    .getPadding());

    private static final Theme LABEL_THEME = new Theme()
            .setPrimaryColor(0x39597e)
            .setBorderEdges(0, 0, 0, DP2);

    private static final Theme BUTTON_FRAME = new Theme()
            .setBackground(BUTTON_DEFAULT, BUTTON_PUSHED, BUTTON_PUSHED, null)
            .setPrimaryColor(0xffffff)
            .setSecondaryFontColor(0xffffff)
            .setPaddingEdges(0, DP1, 0, DP1);

    private static final Theme ADD_USER_THEME = new Theme()
            .setBackground(MULTIHEADER_DEFAULT, MULTIHEADER_HOVER, MULTIHEADER_HOVER, null);

    private final ChatEditScreen screen;

    public VerticalFieldManager top;
    public SimpleLabelField topTitle;
    public ImageButtonField addUsers;
    public RightFieldManager title;
    public List list;
    public CustomLabelField chatTitleCaption;
    public EditTextField chatTitle;
    public CustomLabelField chatMessageCaption;
    public EditTextField chatMessage;
    public ButtonField createChat;
    public ButtonField changeSubject;

    public ChatEditScreenGui(ChatEditScreen screen, boolean newMode) {
        this.screen = screen;

        screen.setFont(Fonts.defaultFont);

        top = new VerticalFieldManager();
        Bitmap bg = R.instance.getBitmap("LightBg.png");
        top.setBackground(BackgroundFactory.createBitmapBackground(bg));
        top.setPadding(DP3, DP8, DP3, DP8);

        addUsers = new ImageButtonField(MULTI_ADD, DP14, DP14,
                Field.FIELD_RIGHT, ADD_USER_THEME, false);
        addUsers.setChangeListener(this);

        topTitle = new SimpleLabelField(tr(VikaResource.Createchat), Field.FIELD_LEFT,
                Fonts.defaultBold, HEADER_LABEL_FRAME);

        title = new RightFieldManager();
        title.setBackground(HEADER_BACKGROUND);

        list = new List();
        list.setOwner(screen);
        list.setListener(screen);

        chatTitle = new EditTextField(0, EDIT_THEME);
        chatMessage = new EditTextField(0, EDIT_THEME);

        chatTitleCaption = new CustomLabelField(tr(VikaResource.Chat_title) + ":", 0,
                LABEL_THEME);
        chatMessageCaption = new CustomLabelField(tr(VikaResource.Chat_message) + ":", 0,
                LABEL_THEME);

        createChat = new ButtonField(tr(VikaResource.Createchat), Field.FIELD_HCENTER,
                BUTTON_FRAME);
        createChat.setChangeListener(this);

        changeSubject = new ButtonField(tr(VikaResource.Change_subject), Field.FIELD_HCENTER,
                BUTTON_FRAME);
        changeSubject.setChangeListener(this);

        title.add(topTitle);
        title.add(addUsers);

        top.add(chatTitleCaption);
        top.add(chatTitle);

        if (newMode) {
            createChat.setText(tr(VikaResource.Createchat));
            top.add(chatMessageCaption);
            top.add(chatMessage);
            top.add(createChat);
        } else {
            chatTitle.setText(screen.getChat().getTitle());
            topTitle.setText(CountHelper.talkPeopleString(screen.getChat().getUsersCount()));
            top.add(changeSubject);
        }

        screen.setBanner(title);
        screen.add(top);
        screen.add(list);
    }

    public void fieldChanged(Field f, int arg1) {
        if (f == createChat) { // create new chat
            screen._createChat();
        } else if (f == addUsers) { // top button
            screen._addUsers();
        } else if (f == changeSubject) {
            screen._changeSubject();
        }
    }


}
