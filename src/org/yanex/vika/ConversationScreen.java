package org.yanex.vika;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.LongHashtable;
import org.yanex.vika.api.APIException;
import org.yanex.vika.api.item.*;
import org.yanex.vika.api.item.collections.Messages;
import org.yanex.vika.api.util.APIHelper;
import org.yanex.vika.api.util.TaskWorker;
import org.yanex.vika.api.util.ThreadHelper;
import org.yanex.vika.gui.dialog.AttachmentDialog;
import org.yanex.vika.gui.dialog.WaitingDialog;
import org.yanex.vika.gui.list.List.ListListener;
import org.yanex.vika.gui.list.item.AbstractListItem;
import org.yanex.vika.gui.list.item.MessageItem;
import org.yanex.vika.gui.screen.ConversationScreenGui;
import org.yanex.vika.gui.screen.VkMainScreen;
import org.yanex.vika.gui.util.Fonts;
import org.yanex.vika.gui.util.R;
import org.yanex.vika.gui.widget.base.AutoLoadingFocusableBitmapField;
import org.yanex.vika.local.Local;
import org.yanex.vika.local.VikaResource;
import org.yanex.vika.storage.MessagesStorage;
import org.yanex.vika.storage.UserStorage;
import org.yanex.vika.util.fun.Pair;
import org.yanex.vika.util.fun.RichVector;
import org.yanex.vika.util.tdparty.GoogleMaps;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class ConversationScreen extends VkMainScreen implements ListListener {

    public static final Hashtable OPENTALKS = new Hashtable();

    public static final int MODE_INPUT = 0;
    public static final int MODE_CONTEXT = 1;
    public static final int MODE_UPLOADING = 2;

    public static final int MAX_ITEMS = 50;

    private final ConversationScreenGui gui;
    private final ConversationScreenManager mgr;

    private final static TaskWorker readingWorker = new TaskWorker();

    private User user;
    private Chat chat;

    private int mode = MODE_INPUT;
    private boolean firstLaunch = true;
    private boolean paintingLock = false;

    private int lastPaintWidth = 0, lastPaintHeight = 0;
    private final Hashtable reading = new Hashtable();

    private final LongHashtable mids = new LongHashtable();
    private RichVector currentItems = new RichVector();

    private RichVector selectedItems = new RichVector();
    private final RichVector attachments = new RichVector();

    private boolean showAttachmentsBar = false;

    public ConversationScreen(Chat chat) {
        this.user = null;
        this.chat = chat;

        setFont(Fonts.defaultFont);
        gui = new ConversationScreenGui(this, null, chat);
        mgr = new ConversationScreenManager(this, gui);
        initListeners();

        ConversationScreen.OPENTALKS.put("chat" + chat.getChatId(), chat);

        if (ConversationScreenManager.forwardedItems != null && ConversationScreenManager.forwardedItems.size() > 0) {
            updateAttachmentsBar();
            updateSelectedItems();
        }
    }

    public ConversationScreen(User user) {
        this.chat = null;
        this.user = user;

        setFont(Fonts.defaultFont);
        gui = new ConversationScreenGui(this, user, null);
        mgr = new ConversationScreenManager(this, gui);
        initListeners();

        ConversationScreen.OPENTALKS.put("user" + user.getId(), user);

        User cu = UserStorage.instance.get(user.getId());
        if (cu != null) {
            user = cu;
        }

        if (ConversationScreenManager.forwardedItems != null && ConversationScreenManager.forwardedItems.size() > 0) {
            updateAttachmentsBar();
            updateSelectedItems();
        }
    }

    public void forwardMessages() {
        for (int i = 0; i < selectedItems.size(); ++i) {
            MessageItem m = (MessageItem) selectedItems.elementAt(i);
            ConversationScreenManager.forwardedItems.addElement(m.getMessage());
            m.setSelected(false);
        }
        selectedItems.removeAllElements();
        showAttachmentsBar = true;
        updateAttachmentsBar();
        updateSelectedItems();
    }

    public void itemClick(int id, AbstractListItem item) {
        if (item instanceof MessageItem) {
            MessageItem m = (MessageItem) item;

            if (m.getMessage().getMid() == 0) {
                Dialog.alert(tr(VikaResource.You_cant_select_undelivered));
                return;
            }

            if (selectedItems.contains(item)) {
                selectedItems.removeElement(item);
                m.setSelected(false);
            } else {
                selectedItems.addElement(item);
                m.setSelected(true);
            }
            int scroll = getMainManager().getVerticalScroll();
            updateSelectedItems();
            try {
                getMainManager().setVerticalScroll(scroll);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loadNextPage(int already) {
        final int offset = currentItems.size();
        final String storageKey = mgr.getStorageKey();

        gui.title.incUpdate();

        new APIHelper() {

            public void after(Object obj) {
                gui.title.decUpdate();
                gui.list.appendItemsBeginningInvert((Vector) obj, -1);
                gui.list.nextPageLoaded();
            }

            public void error(int error) {
                gui.title.decUpdate();
                gui.list.nextPageLoaded();
            }

            public Object task() throws APIException {
                Messages _ret = Vika.api().messages.getHistory(null, mgr.userId, mgr.chatId, offset, 20);
                RichVector ret = _ret.copyInvert();

                currentItems.addAll(0, ret);
                MessagesStorage.instance.put(storageKey, new Messages(currentItems.last(MAX_ITEMS)));
                Vector listItems = MessageItem.insertDateMarkers(MessageItem.fromMessages(ret));
                for (int i = 0; i < listItems.size(); ++i) {
                    MessageItem _m = (MessageItem) listItems.elementAt(i);
                    mids.put(_m.getMessage().getMid(), _m);
                }
                return listItems;
            }
        }.start();
    }

    public void updateSelectedItems(int newMode) {
        if (newMode != Integer.MIN_VALUE) {
            mode = Integer.MIN_VALUE;
        }

        if (mode == ConversationScreen.MODE_UPLOADING) {
            gui.bottom.deleteAll();
            gui.bottom.add(gui.bottomLoading);
            return;
        }

        if (selectedItems.size() > 0) {
            if (mode != ConversationScreen.MODE_CONTEXT) {
                mode = ConversationScreen.MODE_CONTEXT;
                gui.bottom.deleteAll();
                gui.bottom.add(gui.bottomTypingWrapper);
                gui.contextCancel.setText(tr(VikaResource.Cancel) + " (" + selectedItems.size()
                        + ")");
                gui.bottom.add(gui.bottomContextWrapper);
            } else {
                gui.contextCancel.setText(tr(VikaResource.Cancel) + " (" + selectedItems.size()
                        + ")");
            }
        } else {
            if (mode != ConversationScreen.MODE_INPUT) {
                mode = ConversationScreen.MODE_INPUT;
                gui.bottom.deleteAll();
                gui.bottom.add(gui.bottomTypingWrapper);
                gui.bottom.add(gui.bottomText);
                if ((attachments.size() > 0 || ConversationScreenManager.forwardedItems.size() > 0)
                        && showAttachmentsBar) {
                    gui.bottom.add(gui.bottomAttachments);
                }
            }
        }
    }

    public void deleteMessages() {
        if (Dialog
                .ask(Dialog.D_DELETE, tr(VikaResource.Remove_selected_messages_question)) != Dialog.DELETE) {
            return;
        }

        final WaitingDialog dialog = new WaitingDialog(tr(VikaResource.Deleting_messages));

        dialog.setCancellable(true);
        dialog.setListener(new WaitingDialog.WaitingDialogListener() {
            public void onCancel() {
            }
        });

        dialog.show();

        StringBuffer m = new StringBuffer();
        for (int i = 0; i < selectedItems.size(); ++i) {
            MessageItem message = (MessageItem) selectedItems.elementAt(i);
            if (m.length() == 0) {
                m.append(message.getMessage().getMid());
            } else {
                m.append(",").append(message.getMessage().getMid());
            }
        }

        final String mids = m.toString();

        new APIHelper() {

            public void after(Object result) {
                dialog.dismiss();

                for (int i = 0; i < selectedItems.size(); ++i) {
                    MessageItem item = (MessageItem) selectedItems.elementAt(i);
                    item.setSelected(false);
                }

                selectedItems.removeAllElements();
                updateSelectedItems();
            }

            public void error(int error) {
                dialog.dismiss();
                Dialog.alert(tr(VikaResource.Unable_to_delete_messages));
            }

            public Object task() throws APIException {
                Vika.api().messages.delete(captcha(), mids);
                return null;
            }
        }.start();
    }

    public void specialPaint(int id, AbstractListItem item) {
        if (!(item instanceof MessageItem)) {
            return;
        }

        final MessageItem mi = (MessageItem) item;

        Message m = mi.getMessage().edit().setRead(true).build();

        final StringBuffer b = new StringBuffer(Long.toString(m.getMid()));
        Enumeration en = reading.keys();
        while (en.hasMoreElements()) {
            Object o = en.nextElement();
            if (o instanceof String) {
                b.append(",").append((String) o);
            }
        }

        reading.clear();

        final String s = b.toString();

        ConversationScreen.readingWorker.addTask(new Runnable() {

            public void run() {
                boolean success = false;
                int tries = 0;
                while (tries++ < 20) {
                    try {
                        Vika.api().messages.markAsRead(null, s);
                        success = true;
                        break;
                    } catch (Exception e) {
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e2) {
                            return;
                        }
                    }
                }

                if (!success) {
                    reading.put(s, mi);
                }
            }
        });
    }

    public boolean onClose() {
        saveToCache();
        if (chat != null) {
            ConversationScreen.OPENTALKS.remove("chat" + chat.getChatId());
        } else if (user != null) {
            ConversationScreen.OPENTALKS.remove("user" + user.getId());
        }
        Vika.api().longpoll.removeListener(mgr);
        return super.onClose();
    }

    public void send() {
        mgr.send();
    }

    public void imTyping() {
        mgr.notifyTyping();
    }

    public boolean isDirty() {
        return false;
    }

    public void updateSelectedItems() {
        updateSelectedItems(Integer.MIN_VALUE);
    }

    public User getUser() {
        return user;
    }

    public Chat getChat() {
        return chat;
    }

    public RichVector getAttachments() {
        return attachments;
    }

    public RichVector getCurrentItems() {
        return currentItems;
    }

    public LongHashtable getMids() {
        return mids;
    }

    public void updateChatInfo() {
        //TODO: implement
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    void updateAttachmentsBar() {
        gui.bottomAttachments.deleteAll();

        updateAttachmentsButton();

        if (attachments.size() == 0 && ConversationScreenManager.forwardedItems.size() == 0) {
            if (gui.bottomAttachments.getManager() != null) {
                try {
                    gui.bottom.delete(gui.bottomAttachments);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            boolean hasMapAttach = false;
            final int px1 = DP1;
            final int px2 = DP2;

            if (ConversationScreenManager.forwardedItems.size() > 0) {
                Bitmap _forwarded = R.instance
                        .getBitmap("Convs/AttachesMenu/Forwarded.png");
                AutoLoadingFocusableBitmapField forwarded = new AutoLoadingFocusableBitmapField(
                        DP16, DP16, 0,
                        ConversationScreenGui.ATTACHMENT_ADD_THEME);
                forwarded.setPadding(px1, px1, px1, px1);
                forwarded.setMargin(px2, px2, px2, px2);
                forwarded.setChangeListener(new FieldChangeListener() {

                    public void fieldChanged(Field field, int context) {
                        if (Dialog.ask(Dialog.D_DELETE,
                                tr(VikaResource.Remove_forward_messages)) != Dialog.DELETE) {
                            return;
                        }

                        ConversationScreenManager.forwardedItems.removeAllElements();
                        updateSelectedItems();
                        updateAttachmentsBar();
                    }
                });
                forwarded.setBitmap(_forwarded);
                gui.bottomAttachments.add(forwarded);
            }

            for (int i = 0; i < attachments.size(); ++i) {
                Object o = attachments.elementAt(i);
                final int ii = i;
                Field f = null;

                if (o instanceof PendingLocation) {
                    hasMapAttach = true;
                    PendingLocation pl = (PendingLocation) o;
                    AutoLoadingFocusableBitmapField alb = new AutoLoadingFocusableBitmapField(DP16
                            + px1 * 4, DP16 + px1 * 4, 0, Bitmap.SCALE_TO_FILL, 1, 4,
                            ConversationScreenGui.ATTACHMENT_ADD_THEME);
                    alb.setURL(GoogleMaps.getThumb(pl.getLatitude(), pl.getLongitude()));
                    // alb.setPadding(px1, px1, px1, px1);
                    alb.setMargin(px2, px2, px2, px2);
                    f = alb;
                    gui.bottomAttachments.add(alb);
                } else if (o instanceof PendingPhoto) {
                    PendingPhoto pp = (PendingPhoto) o;
                    AutoLoadingFocusableBitmapField alb = new AutoLoadingFocusableBitmapField(DP16
                            + px1 * 4, DP16 + px1 * 4, 0, Bitmap.SCALE_TO_FILL, 4, 4,
                            ConversationScreenGui.ATTACHMENT_ADD_THEME);
                    alb.setURL(pp.filename);
                    alb.setMargin(px2, px2, px2, px2);
                    f = alb;
                    gui.bottomAttachments.add(alb);
                }

                if (f != null) {
                    f.setChangeListener(new FieldChangeListener() {

                        public void fieldChanged(Field field, int context) {
                            deleteAttachment(ii);
                        }
                    });
                }
            }

            Bitmap _camera = R.instance.getBitmap("Convs/Attaches/Camera.png");
            Bitmap _map = R.instance.getBitmap("Convs/Attaches/Map.png");
            Bitmap _photo = R.instance.getBitmap("Convs/Attaches/Photo.png");

            AutoLoadingFocusableBitmapField camera = new AutoLoadingFocusableBitmapField(DP16,
                    DP16, 0, Bitmap.SCALE_TO_FIT, 0, -1,
                    ConversationScreenGui.ATTACHMENT_ADD_THEME);
            AutoLoadingFocusableBitmapField map = new AutoLoadingFocusableBitmapField(DP16,
                    DP16, 0, Bitmap.SCALE_TO_FIT, 0, -1,
                    ConversationScreenGui.ATTACHMENT_ADD_THEME);
            AutoLoadingFocusableBitmapField photo = new AutoLoadingFocusableBitmapField(DP16,
                    DP16, 0, Bitmap.SCALE_TO_FIT, 0, -1,
                    ConversationScreenGui.ATTACHMENT_ADD_THEME);

            camera.setPadding(px1, px1, px1, px1);
            map.setPadding(px1, px1, px1, px1);
            photo.setPadding(px1, px1, px1, px1);

            camera.setMargin(px2, px2, px2, px2);
            map.setMargin(px2, px2, px2, px2);
            photo.setMargin(px2, px2, px2, px2);

            camera.setChangeListener(new FieldChangeListener() {

                public void fieldChanged(Field field, int context) {
                    addCameraShot();
                }
            });

            map.setChangeListener(new FieldChangeListener() {

                public void fieldChanged(Field field, int context) {
                    addLocation();
                }
            });

            photo.setChangeListener(new FieldChangeListener() {

                public void fieldChanged(Field field, int context) {
                    addFileShot();
                }
            });

            camera.setBitmap(_camera);
            map.setBitmap(_map);
            photo.setBitmap(_photo);

            gui.bottomAttachments.add(camera);
            gui.bottomAttachments.add(photo);
            if (!hasMapAttach) {
                gui.bottomAttachments.add(map);
            }

            if (gui.bottomAttachments.getManager() == null) {
                try {
                    gui.bottom.add(gui.bottomAttachments);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void paint(Graphics graphics) {
        super.paint(graphics);

        int w = Display.getWidth();
        int h = Display.getHeight();

        if (lastPaintWidth != w && lastPaintHeight != h && !paintingLock) {
            if (lastPaintWidth * lastPaintHeight > 0) {
                paintingLock = true;

                lastPaintWidth = w;
                lastPaintHeight = h;

                final WaitingDialog dialog = new WaitingDialog(
                        tr(VikaResource.Please_wait));
                dialog.show();

                new ThreadHelper() {

                    public void error() {
                        dialog.dismiss();
                    }

                    public void after(Object items) {
                        dialog.dismiss();

                        gui.list.nextPageForceLoading();
                        gui.list.setItems((Vector) items);

                        gui.list.scrollToBottom();
                        gui.list.selectLast();
                        gui.list.nextPageLoaded();

                        paintingLock = false;
                    }

                    public Object task() {
                        return MessageItem.insertDateMarkers(MessageItem
                                .fromMessages(currentItems));
                    }

                }.start();
            }
            lastPaintWidth = w;
            lastPaintHeight = h;
        }
    }

    protected void onUiEngineAttached(boolean attached) {
        super.onUiEngineAttached(attached);
        if (attached && firstLaunch) {
            firstLaunch = false;
            initialMessagesLoad();
        }
    }

    protected boolean keyChar(char ch, int status, int time) {
        if (ch == 'R' || ch == 'r' && gui.list.isFocus()) {
            gui.list.scrollToBottom();
            gui.list.selectLast();
            gui.text.setFocus();
            return true;
        } else if ((ch == 'f' || ch == 'F' || ch == 'П' || ch == 'п') && gui.list.isFocus()) {
            forwardMessages();
        } else if ((ch == 'd' || ch == 'D' || ch == 'А' || ch == 'а') && gui.list.isFocus()) {
            deleteMessages();
        } else if ((ch == 'O' || ch == 'o' || ch == 'З' || ch == 'з') && gui.list.isFocus()) {
            gui.title.openOptions();
        }

        return super.keyChar(ch, status, time);
    }

    protected boolean trackwheelRoll(int amount, int status, int time) {
        if (amount > 0) {
            if (gui.list.getFieldWithFocusIndex() >= gui.list.getFieldCount() - amount * 2) {
                try {
                    gui.text.setFocus();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        }
        return super.trackwheelRoll(amount, status, time);
    }

    protected boolean navigationMovement(int dx, int dy, int status, int time) {
        if (dx == 0 && dy > 0 || dx > 0 && dy == 0) {
            if (gui.list.getFieldWithFocusIndex() >= gui.list.getFieldCount() - 1) {
                try {
                    gui.text.setFocus();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return super.navigationMovement(dx, dy, status, time);
    }

    private void initListeners() {
        Vika.api().longpoll.addListener(mgr);

        gui.addAttachments.setChangeListener(new FieldChangeListener() {

            public void fieldChanged(Field field, int context) {
                if (attachments.size() == 0) {
                    AttachmentDialog dialog = new AttachmentDialog();
                    dialog.show();
                    int a = dialog.getSelection();
                    if (a < 0) {
                        return;
                    }

                    if (a == 0) {
                        addCameraShot();
                    } else if (a == 1) {
                        addFileShot();
                    } else if (a == 2) {
                        addLocation();
                    }
                } else {
                    showAttachmentsBar = !showAttachmentsBar;
                    if (showAttachmentsBar) {
                        if (gui.bottomAttachments.getManager() == null
                                && mode == ConversationScreen.MODE_INPUT) {
                            gui.bottom.add(gui.bottomAttachments);
                        }
                    } else {
                        if (gui.bottomAttachments.getManager() != null) {
                            gui.bottom.delete(gui.bottomAttachments);
                        }
                    }
                }
            }

        });

        gui.contextCancel.setChangeListener(new FieldChangeListener() {

            public void fieldChanged(Field field, int context) {
                for (int i = 0; i < selectedItems.size(); ++i) {
                    MessageItem m = (MessageItem) selectedItems.elementAt(i);
                    m.setSelected(false);
                }
                selectedItems.removeAllElements();
                updateSelectedItems();
            }
        });
    }

    private void initialMessagesLoad() {
        if (user != null || chat != null) {
            final String storageKey = mgr.getStorageKey();
            final WaitingDialog dialog = new WaitingDialog(Local.tr(VikaResource.Loading_messages));

            dialog.setCancellable(true);
            dialog.setListener(new WaitingDialog.WaitingDialogListener() {
                public void onCancel() {
                    close();
                }
            });
            dialog.show();

            new APIHelper() {

                public void error(int error) {
                    dialog.dismiss();
                    Dialog.alert(Local.tr(VikaResource.Unable_to_load_messages));
                }

                public Object task() throws APIException {
                    return mgr.loadMessagesSync(storageKey);
                }

                public void after(Object result) {
                    dialog.dismiss();
                    Pair p = (Pair) result;
                    RichVector listItems = (RichVector) p.second;
                    currentItems = (RichVector) p.first;

                    if (Configuration.DEBUG) {
                        Vika.log(listItems.toString());
                    }

                    for (int i = 0; i < listItems.size(); ++i) {
                        MessageItem _m = (MessageItem) listItems.elementAt(i);
                        mids.put(_m.getMessage().getMid(), _m);
                    }

                    gui.list.nextPageForceLoading();
                    gui.list.setItems(listItems);
                    gui.list.scrollToBottom();
                    gui.list.selectLast();
                    gui.list.nextPageLoaded();

                    updateLayout();
                }
            }.start();
        }
    }

    private void addCameraShot() {
        CameraScreen camera = new CameraScreen(new CameraScreen.CameraListener() {

            public void onCameraError(String error) {
                Dialog.alert(error);
            }

            public void onShot(String filename) {
                attachments.addElement(new PendingPhoto(filename));
                showAttachmentsBar = true;
                updateAttachmentsBar();
            }
        });

        camera.launch();
    }

    private void addFileShot() {
        Vector filter = new Vector();
        filter.addElement(".png");
        filter.addElement(".jpg");
        FileSelectWindow files = new FileSelectWindow(filter);
        files.showModal();

        String filename = files.getFilename();
        if (filename != null && filename.length() > 0) {
            for (int i = 0; i < attachments.size(); ++i) {
                Object o = attachments.elementAt(i);
                if (o instanceof PendingPhoto) {
                    PendingPhoto pp = (PendingPhoto) o;
                    if (pp.filename.equals(filename)) {
                        Dialog.alert(tr(VikaResource.Photo_exist));
                        return;
                    }
                }
            }

            attachments.addElement(new PendingPhoto(filename));
            showAttachmentsBar = true;
            updateAttachmentsBar();
        }
    }

    private void addLocation() {
        for (int i = 0; i < attachments.size(); ++i) {
            Object o = attachments.elementAt(i);
            if (o instanceof PendingLocation) {
                return;
            }
        }

        new MapScreen(new MapScreen.MapListener() {

            public void onMapCancel() {
            }

            public void onMapSelected(double latitude, double longitude) {
                attachments.addElement(new PendingLocation(latitude, longitude));
                showAttachmentsBar = true;
                updateAttachmentsBar();
            }
        }).show();
    }

    private void deleteAttachment(int id) {
        if (id < 0 || id >= attachments.size()) {
            return;
        }

        Object o = attachments.elementAt(id);
        String message = null;

        if (o instanceof PendingLocation) {
            message = tr(VikaResource.Remove_attach_geo);
        } else if (o instanceof PendingPhoto) {
            message = tr(VikaResource.Remove_attach_photo);
        }

        int ret = Dialog.ask(Dialog.D_DELETE, message);
        if (ret == Dialog.DELETE) {
            attachments.removeElementAt(id);
            updateAttachmentsBar();
        }
    }

    private void saveToCache() {
        if (currentItems.size() == 0) {
            return;
        }

        RichVector v = new RichVector();
        for (int i = 0; i < currentItems.size(); ++i) {
            Message m = (Message) currentItems.elementAt(i);
            if (m.getMid() != 0) {
                v.addElement(m);
            }
        }

        MessagesStorage.instance.putFast(mgr.getStorageKey(), new Messages(v.last(MAX_ITEMS)));
        MessagesStorage.instance.updateDialogs();
    }

    private void updateAttachmentsButton() {
        if (attachments.size() > 0) {
            gui.addAttachments.setBitmaps(
                    ConversationScreenGui.addAttachmentsActive,
                    ConversationScreenGui.addAttachmentsActive,
                    ConversationScreenGui.addAttachmentsActive,
                    ConversationScreenGui.addAttachmentsActive);
        } else {
            gui.addAttachments.setBitmaps(
                    ConversationScreenGui.addAttachmentsDefault,
                    ConversationScreenGui.addAttachmentsFocus,
                    ConversationScreenGui.addAttachmentsFocus,
                    ConversationScreenGui.addAttachmentsFocus);
        }
    }

}
