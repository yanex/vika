package org.yanex.vika.gui.widget;

import com.mobiata.bb.ui.decor.NinePatchBackground;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.NullField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.util.LongEnumeration;
import net.rim.device.api.util.LongHashtable;
import org.yanex.vika.ChatEditScreen;
import org.yanex.vika.UserScreen;
import org.yanex.vika.api.item.Chat;
import org.yanex.vika.api.item.User;
import org.yanex.vika.gui.screen.VkMainScreen;
import org.yanex.vika.gui.util.Fonts;
import org.yanex.vika.gui.util.GuiItem;
import org.yanex.vika.gui.util.R;
import org.yanex.vika.gui.util.Theme;
import org.yanex.vika.gui.widget.base.AutoLoadingBitmapField;
import org.yanex.vika.gui.widget.base.GifAnimationField;
import org.yanex.vika.gui.widget.base.ImageTextButtonField;
import org.yanex.vika.gui.widget.manager.RightFieldManager;
import org.yanex.vika.local.CountHelper;
import org.yanex.vika.local.VikaResource;
import org.yanex.vika.util.HappyDate;

public class VkConversationTitleField extends HorizontalFieldManager implements
        FieldChangeListener, GuiItem {

    private RightFieldManager rfm;

    private VkConversationUserField userField;
    private ImageTextButtonField chatManage;
    private AutoLoadingBitmapField photo;

    private User user;
    private Chat chat;

    private LongHashtable typingUids = new LongHashtable();
    private Thread typingThread = null;

    private static final NinePatchBackground BACKGROUND = new NinePatchBackground(
            R.instance.getNinepatch("Convs/Header.png"));

    private static final Bitmap MULTICHAT_WHITE = R.instance
            .getBitmap("Other/MultiChatWhite.png");

    private static final Theme TITLE_THEME, addUserButtonTheme, ONLINE_THEME;

    private final GifAnimationField animation = new GifAnimationField("loading_small.gif",
            Field.FIELD_VCENTER);
    private final HorizontalFieldManager animationManager = new HorizontalFieldManager(
            Field.FIELD_RIGHT);

    private int update = 0;

    static {
        TITLE_THEME = new Theme();
        VkConversationTitleField.TITLE_THEME.setPrimaryColor(0xFFFFFF);

        ONLINE_THEME = new Theme();
        VkConversationTitleField.ONLINE_THEME.setPrimaryColor(0xb6b6b6);

        addUserButtonTheme = new Theme();
        Background addUserDefault = new NinePatchBackground("Convs/MultiHeaderRightButton.png");
        Background addUserFocus = new NinePatchBackground("Convs/MultiHeaderRightButtonHover.png");
        VkConversationTitleField.addUserButtonTheme.setBackground(addUserDefault, addUserFocus,
                addUserFocus, null);
        VkConversationTitleField.addUserButtonTheme.setPrimaryColor(0xffffff);
        VkConversationTitleField.addUserButtonTheme.setSecondaryFontColor(0xffffff);
    }

    protected static int px(int pt) {
        return Ui.convertSize(pt, Ui.UNITS_pt, Ui.UNITS_px);
    }

    public VkConversationTitleField() {
        setBackground(VkConversationTitleField.BACKGROUND);
        rfm = new RightFieldManager();
        rfm.setMargin(VkConversationTitleField.px(2), VkConversationTitleField.px(2),
                VkConversationTitleField.px(2),
                VkConversationTitleField.px(2));
        add(rfm);

        animation.stop();
        animation.setMargin(0, VkConversationTitleField.px(1) / 2, 0, 0);
    }

    public VkConversationTitleField(Chat c) {
        this();
        set(c);
    }

    public VkConversationTitleField(User u) {
        this();
        set(u);
    }

    public void decUpdate() {
        update--;
        if (update < 0) {
            update = 0;
        }
        if (update == 0) {
            animation.stop();
        }
    }

    public void fieldChanged(Field f, int arg1) {
        if (f == chatManage && chat != null) {
            openOptions();
        }
    }

    public void incUpdate() {
        update++;
        if (update == 1) {
            animation.start();
        }
    }

    public void openOptions() {
        if (chat != null) {
            new ChatEditScreen(chat).show();
        }
    }

    public void set(Chat c) {
        this.chat = c;
        rfm.deleteAll();

        rfm.setMargin(0, 0, 0, VkConversationTitleField.px(2));

        if (userField == null) {
            userField = new VkConversationUserField();
            userField.setMargin(VkConversationTitleField.px(2), VkConversationTitleField.px(2),
                    VkConversationTitleField.px(2), VkConversationTitleField.px(2));
        }

        if (chatManage == null) {
            chatManage = new ImageTextButtonField(chat.getUsersCount() + " (O)",
                    VkConversationTitleField.MULTICHAT_WHITE, Field.FIELD_RIGHT,
                    VkConversationTitleField.addUserButtonTheme, VkConversationTitleField.px(3),
                    VkConversationTitleField.px(14));
            chatManage.setFont(Fonts.narrow(6));
            chatManage.setChangeListener(this);
        }

        userField.setName(c.getTitle());

        rfm.add(userField);
        animationManager.add(animation);
        rfm.add(animationManager);
        rfm.add(chatManage);
    }

    public void set(User u) {
        this.user = u;
        rfm.deleteAll();

        rfm.setMargin(VkConversationTitleField.px(2), VkConversationTitleField.px(2),
                VkConversationTitleField.px(2),
                VkConversationTitleField.px(2));

        if (userField == null) {
            userField = new VkConversationUserField(Field.FIELD_LEFT);
            userField.setMargin(VkConversationTitleField.px(2), VkConversationTitleField.px(2),
                    VkConversationTitleField.px(2), VkConversationTitleField.px(4));
        }

        userField.setName(user.getFullName());

        String caption = null;
        if (user.isOnline()) {
            caption = "online";
        } else {
            if (user.getLastSeen() == 0) {
                userField.setStatus("");
            } else {
                String was = VkMainScreen.tr(VikaResource.wasm);
                if (!user.isMale()) {
                    was = VkMainScreen.tr(VikaResource.wasf);
                }
                userField.setStatus(was + " " + VkMainScreen.tr(VikaResource.in_network) + " "
                        + textTime(user.getLastSeen()));
            }
        }

        userField.setStatus(caption);

        if (photo == null) {
            photo = new AutoLoadingBitmapField(new XYDimension(DP12, DP12),
                    Field.FIELD_RIGHT, true);
            photo.setURL(u.getPhotoURL());
            photo.setMargin(VkConversationTitleField.px(2), 0, VkConversationTitleField.px(2),
                    VkConversationTitleField.px(2));
        }

        rfm.add(userField);
        rfm.add(new NullField());
        animationManager.add(animation);
        rfm.add(animationManager);
        rfm.add(photo);
    }

    protected void sublayout(int width, int height) {
        if (userField != null) {
            userField.setWidth(width - VkConversationTitleField.px(2) * 4
                    - VkConversationTitleField.px(16));
        }
        super.sublayout(width, height);
    }

    private String textTime(long timestamp) {
        long seconds = System.currentTimeMillis() / 1000 - timestamp;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long years = days / 365;

        if (hours == 0 && minutes == 0) {
            return CountHelper.secondsString(seconds);
        } else if (hours == 0) {
            return CountHelper.minutesString(minutes);
        } else if (hours < 24) {
            return CountHelper.hoursString(hours);
        } else if (years == 0) {
            HappyDate d = new HappyDate(timestamp * 1000);
            return d.day2() + "." + d.month2();
        } else {
            HappyDate d = new HappyDate(timestamp * 1000);
            return d.day2() + "." + d.month2() + "." + d.year;
        }
    }

    protected boolean touchEvent(TouchEvent message) {
        if (message.getEvent() == TouchEvent.CLICK) {
            if (user != null) {
                new UserScreen(user).show();
            }
            return true;
        } else {
            return super.touchEvent(message);
        }
    }

    public void typing(long uid) {
        typingUids.put(uid, new Long(System.currentTimeMillis() + 8500));

        updateLabels();

        if (typingThread == null || !typingThread.isAlive()) {
            typingThread = new Thread() {
                public void run() {
                    while (true) {
                        try {
                            LongEnumeration keys = typingUids.keys();
                            long now = System.currentTimeMillis();
                            boolean changed = false;

                            while (keys.hasMoreElements()) {
                                long k = keys.nextElement();
                                long time = ((Long) typingUids.get(k)).longValue();

                                if (now > time) {
                                    typingUids.remove(k);
                                    changed = true;
                                }
                            }

                            if (changed) {
                                UiApplication.getUiApplication().invokeLater(new Runnable() {
                                    public void run() {
                                        updateLabels();
                                    }
                                });
                            }

                            if (typingUids.size() == 0) {
                                interrupt();
                                return;
                            }

                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            interrupt();
                            return;
                        }
                    }
                }
            };

            typingThread.start();
        }
    }

    public void update() {
        updateLabels();
    }

    private void updateLabels() {
        if (user != null) {
            if (typingUids.size() > 0) {
                userField.setStatus(VkMainScreen.tr(VikaResource.typing3));
            } else {
                if (user.isOnline()) {
                    userField.setStatus("online");
                } else {
                    if (user.getLastSeen() == 0) {
                        userField.setStatus("");
                    } else {
                        String was = VkMainScreen.tr(VikaResource.wasm);
                        if (!user.isMale()) {
                            was = VkMainScreen.tr(VikaResource.wasf);
                        }
                        userField.setStatus(was + " " + VkMainScreen.tr(VikaResource.in_network) + " "
                                + textTime(user.getLastSeen()));
                    }
                }
            }
        } else if (chat != null) {

        }
    }

}
