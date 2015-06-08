package org.yanex.vika.gui.screen;

import com.mobiata.bb.ui.decor.NinePatchBackground;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.NullField;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;
import org.yanex.vika.UserScreen;
import org.yanex.vika.api.item.User;
import org.yanex.vika.gui.util.Files;
import org.yanex.vika.gui.util.Fonts;
import org.yanex.vika.gui.util.R;
import org.yanex.vika.gui.util.Theme;
import org.yanex.vika.gui.widget.VkButtonField;
import org.yanex.vika.gui.widget.VkTitleField;
import org.yanex.vika.gui.widget.base.AutoLoadingFocusableBitmapField;
import org.yanex.vika.gui.widget.base.ButtonField;
import org.yanex.vika.gui.widget.base.MultiFontButtonField;
import org.yanex.vika.gui.widget.manager.VerticalCenterFieldManager;
import org.yanex.vika.local.VikaResource;
import org.yanex.vika.util.fun.RichVector;

public class UserScreenGui extends ScreenGui {

    private static final Font FONT = Fonts.narrow(7);

    private static final Background
            BACKGROUND = BackgroundFactory.createBitmapBackground
            (R.instance.getBitmap(Files.DARK_BG)),
            BACKGROUND_REGISTER_FOCUS = new NinePatchBackground(Files.DARK_RED_BUTTON_FOCUS),
            BACKGROUND_REGISTER_ACTIVE = new NinePatchBackground(Files.DARK_RED_BUTTON_FOCUS_PUSHED),
            BACKGROUND_PHOTO_DEFAULT = new NinePatchBackground(Files.DARK_PHOTO_HOLDER);

    private static final Theme THEME_REGISTER = new Theme()
            .setPrimaryColor(0xEEEEEE)
            .setSecondaryFontColor(0xFFFFFF)
            .setBackground(null, BACKGROUND_REGISTER_FOCUS, BACKGROUND_REGISTER_ACTIVE, null);

    private static final Theme THEME_IMAGE = new Theme()
            .setBackground(BACKGROUND_PHOTO_DEFAULT, BACKGROUND_PHOTO_DEFAULT, BACKGROUND_PHOTO_DEFAULT, null)
            .setPaddingEdges(DP2, DP2, DP2, DP2);

    private final VkMainScreen screen;

    private final VerticalCenterFieldManager root;
    private final VkTitleField title;
    public final AutoLoadingFocusableBitmapField image;
    public final ButtonField addToFriends, rejectRequest,
            cancelRequest, sendMessage, deleteFriend;

    public UserScreenGui(VkMainScreen screen, User user) {
        this.screen = screen;

        root = new VerticalCenterFieldManager(USE_ALL_HEIGHT | USE_ALL_WIDTH);
        root.setPadding(DP1, 0, DP1, 0);
        root.setBackground(BACKGROUND);

        title = new VkTitleField(user.getFullName());

        image = new AutoLoadingFocusableBitmapField(DP30, DP30, FIELD_HCENTER, THEME_IMAGE);

        addToFriends = new VkButtonField(tr(VikaResource.Add_to_friends), FIELD_HCENTER);
        addToFriends.setFont(FONT);

        rejectRequest = new ButtonField(tr(VikaResource.Decline_request), FIELD_HCENTER, THEME_REGISTER);
        rejectRequest.setFont(FONT);

        cancelRequest = new VkButtonField(tr(VikaResource.Cancel_request), FIELD_HCENTER);
        cancelRequest.setFont(FONT);

        sendMessage = new VkButtonField(tr(VikaResource.Send_message), FIELD_HCENTER);
        sendMessage.setFont(FONT);

        deleteFriend = new ButtonField(tr(VikaResource.Remove_from_friends), FIELD_HCENTER, THEME_REGISTER);
        deleteFriend.setFont(FONT);
    }

    public void display(int relations) {
        root.deleteAll();
        screen.deleteAll();

        screen.setBanner(title);

        root.add(new NullField());
        root.add(image);

        RichVector visibleFields = new RichVector();

        addToFriends.setFixedWidth(-1);
        rejectRequest.setFixedWidth(-1);
        cancelRequest.setFixedWidth(-1);
        sendMessage.setFixedWidth(-1);
        deleteFriend.setFixedWidth(-1);

        if (relations == UserScreen.RELATIONS_FRIENDSHIP) {
            visibleFields.addElement(sendMessage);
            root.add(sendMessage);
            visibleFields.addElement(deleteFriend);
            root.add(deleteFriend);
        } else if (relations == UserScreen.RELATIONS_NONE) {
            visibleFields.addElement(addToFriends);
            root.add(addToFriends);
        } else if (relations == UserScreen.RELATIONS_NONE) {
            visibleFields.addElement(addToFriends);
            root.add(addToFriends);
            visibleFields.addElement(rejectRequest);
            root.add(rejectRequest);
        } else if (relations == UserScreen.RELATIONS_OUTCOMING_REQUEST) {
            visibleFields.addElement(cancelRequest);
            root.add(cancelRequest);
        }

        int max = 0;
        for (int i = 0; i < visibleFields.size(); ++i) {
            max = Math.max(max, ((Field) visibleFields.elementAt(i)).getPreferredWidth());
        }

        for (int i = 0; i < visibleFields.size(); ++i) {
            Object o = visibleFields.elementAt(i);
            if (o instanceof ButtonField) {
                ((ButtonField) o).setFixedWidth(max);
            } else if (o instanceof MultiFontButtonField) {
                ((MultiFontButtonField) o).setFixedWidth(max);
            }
        }

        addToFriends.setChangeListener(null);
        rejectRequest.setChangeListener(null);
        cancelRequest.setChangeListener(null);
        sendMessage.setChangeListener(null);
        deleteFriend.setChangeListener(null);
        image.setChangeListener(null);

        addToFriends.setChangeListener(screen);
        rejectRequest.setChangeListener(screen);
        cancelRequest.setChangeListener(screen);
        sendMessage.setChangeListener(screen);
        deleteFriend.setChangeListener(screen);
        image.setChangeListener(screen);

        screen.add(root);
    }

}
