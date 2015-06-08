package org.yanex.vika.gui.dialog;

import com.mobiata.bb.ui.decor.NinePatchBackground;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import org.yanex.vika.gui.util.Fonts;
import org.yanex.vika.gui.util.R;
import org.yanex.vika.gui.widget.base.CompoundButtonField;
import org.yanex.vika.local.VikaResource;

public class NewChatDialog extends Dialog implements FieldChangeListener {

    private static final Bitmap WRITE_TO_FRIEND_BITMAP =
            R.instance.getBitmap("Convs/Single.png");
    private static final Bitmap CREATE_CHAT_BITMAP =
            R.instance.getBitmap("Convs/Chat.png");

    private CompoundButtonField writeToFriend, createChat;
    private int selection = -1;

    public NewChatDialog() {
        super(new VerticalFieldManager());
        setFont(Fonts.narrow(6));

        writeToFriend = new CompoundButtonField(
                tr(VikaResource.Write_to_friend), WRITE_TO_FRIEND_BITMAP);
        createChat = new CompoundButtonField(tr(VikaResource.Create_chat), CREATE_CHAT_BITMAP);

        writeToFriend.setChangeListener(this);
        createChat.setChangeListener(this);

        HorizontalFieldManager hfm = new HorizontalFieldManager();
        hfm.setPadding(DP2, DP2, DP2, DP2);

        hfm.add(writeToFriend);
        hfm.add(createChat);

        add(hfm);

        setBackground(new NinePatchBackground("Convs/AttachesMenu/Bg.png"));
    }

    public void fieldChanged(Field field, int context) {
        if (field == writeToFriend) {
            selection = 0;
        } else if (field == createChat) {
            selection = 1;
        }
        dismiss();
    }

    public int getSelection() {
        return selection;
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
