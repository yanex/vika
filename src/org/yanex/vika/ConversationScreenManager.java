package org.yanex.vika;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.LongEnumeration;
import net.rim.device.api.util.LongHashtable;
import org.yanex.vika.api.APIException;
import org.yanex.vika.api.APIUtils;
import org.yanex.vika.api.item.*;
import org.yanex.vika.api.item.collections.Messages;
import org.yanex.vika.api.longpoll.*;
import org.yanex.vika.api.util.APIHelper;
import org.yanex.vika.api.util.ThreadHelper;
import org.yanex.vika.gui.list.item.MessageItem;
import org.yanex.vika.gui.screen.ConversationScreenGui;
import org.yanex.vika.local.Local;
import org.yanex.vika.local.VikaResource;
import org.yanex.vika.storage.MessagesStorage;
import org.yanex.vika.storage.UserStorage;
import org.yanex.vika.util.RandomUtils;
import org.yanex.vika.util.fun.Function1;
import org.yanex.vika.util.fun.Pair;
import org.yanex.vika.util.fun.RichVector;

import java.util.Hashtable;
import java.util.Vector;

public class ConversationScreenManager implements LongPollListener {

    private final ConversationScreen screen;
    private final ConversationScreenGui gui;

    public final long userId, chatId;
    private User user;
    private Chat chat;

    private long nextTypingTime = 0;

    private volatile Thread typingThread = null;
    private final LongHashtable typingUids = new LongHashtable();

    private final RichVector attachments;
    private final RichVector sendingMessages = new RichVector();
    private final Hashtable uploadedFiles = new Hashtable();

    public static Vector forwardedItems = new Vector();

    public ConversationScreenManager(ConversationScreen screen, ConversationScreenGui gui) {
        this.screen = screen;
        this.gui = gui;
        this.user = screen.getUser();
        this.chat = screen.getChat();
        this.userId = (user != null) ? user.getId() : 0;
        this.chatId = (chat != null) ? chat.getChatId() : 0;
        this.attachments = screen.getAttachments();
    }

    public Message buildSendingMessage() {
        Message.Builder message = new Message.Builder();

        message.setId(0);
        message.setDate(System.currentTimeMillis() / 1000);

        if (user != null) {
            message.setUser(user);
            message.setUserId(user != null ? user.getId() : 0);
        }

        if (chat != null) {
            message.setChatId(chat.getChatId());
            message.setUserId(Vika.api().getToken().getUserId());
        }

        User myUser = UserStorage.instance.get(Vika.api().getToken().getUserId());
        if (myUser != null) {
            message.setMyUser(myUser);
        }

        boolean fromMe = user != null && Vika.api().getToken().getUserId() == (user.getId());

        message.setRead(false);
        message.setOut(!fromMe);
        message.setDeleted(false);
        message.setFromChat(chat != null);

        if (user != null && user.getId() == (Vika.api().getToken().getUserId())) {
            message.setRead(true);
        }

        message.setTitle("");
        message.setBody(gui.text.getText().trim());

        assignAttachments(message);
        assignForwardedMessages(message);

        return message.build();
    }

    public Pair loadMessagesSync(String storageKey) throws APIException {
        Pair local = loadLocalMessagesSync(storageKey);
        if (local != null) {
            return local;
        }

        Messages _ret = Vika.api().messages
                .getHistory(null, userId, chatId, 0, 20);
        RichVector ret = _ret.copyInvert();
        MessagesStorage.instance.put(storageKey, new Messages(ret.last(ConversationScreen.MAX_ITEMS)));
        RichVector listItems = MessageItem
                .insertDateMarkers(MessageItem.fromMessages(ret));
        return new Pair(_ret.copy(), listItems);
    }

    public void send() {
        if (!checkAttachmentsUploadStatus()) {
            return;
        }

        new ThreadHelper() {

            Message sendingMessage;
            MessageItem messageItem;
            String attachmentsString, forwardedString;
            double latitude, longitude;

            public void after(Object o) {
                gui.text.setText("");
                attachments.removeAllElements();
                ConversationScreenManager.forwardedItems.removeAllElements();
                screen.updateAttachmentsBar();

                screen.getCurrentItems().addElement(messageItem.getMessage());

                sendingMessages.addElement(messageItem);
                Vector newListItem = new Vector();
                newListItem.addElement(messageItem);
                gui.list.appendItems(newListItem);
                gui.list.scrollToBottom();

                final String guid = RandomUtils.instance.nextIntString(7);

                new APIHelper() {

                    public void after(Object o) {

                    }

                    public void error(int error) {

                    }

                    public Object task() throws APIException {
                        return Vika.api().messages.send(captcha(), userId, chatId,
                                sendingMessage.getBody(), attachmentsString, forwardedString, null,
                                1, guid, latitude, longitude);
                    }
                }.start();
            }

            public Object task() {
                int i;

                sendingMessage = buildSendingMessage();
                messageItem = new MessageItem(sendingMessage, true);

                double _latitude = Double.MIN_VALUE;
                double _longitude = Double.MIN_VALUE;

                for (i = 0; i < attachments.size(); ++i) {
                    if (attachments.elementAt(i) instanceof PendingLocation) {
                        PendingLocation pl = (PendingLocation) attachments.elementAt(i);
                        _latitude = pl.getLatitude();
                        _longitude = pl.getLongitude();
                        break;
                    }
                }

                latitude = _latitude;
                longitude = _longitude;

                String _attachmentsString = "";
                String _forwardedString = "";

                for (i = 0; i < attachments.size(); ++i) {
                    Object o = attachments.elementAt(i);
                    if (o instanceof PendingPhoto) {
                        String s = ((PendingPhoto) o).filename;
                        long id = ((PhotoAttachment) uploadedFiles.get(s)).getId();
                        if (_attachmentsString.length() == 0) {
                            _attachmentsString = Long.toString(id);
                        } else {
                            _attachmentsString += "," + id;
                        }
                    }
                }

                for (i = 0; i < ConversationScreenManager.forwardedItems.size(); ++i) {
                    Message message = (Message) ConversationScreenManager.forwardedItems.elementAt(i);
                    if (_forwardedString.length() == 0) {
                        _forwardedString = Long.toString(message.getMid());
                    } else {
                        _forwardedString += "," + message.getMid();
                    }
                }

                attachmentsString = _attachmentsString;
                forwardedString = _forwardedString;
                final boolean hasGeo = latitude != Double.MIN_VALUE && longitude != Double.MIN_VALUE;

                if (!hasGeo && attachmentsString.length() == 0 && forwardedString.length() == 0
                        && sendingMessage.getBody().length() == 0) {
                    return null;
                }

                return Boolean.TRUE;
            }
        }.start();
    }

    public void notifyTyping() {
        if (nextTypingTime != 0 && System.currentTimeMillis() < nextTypingTime) {
            return;
        }

        nextTypingTime = System.currentTimeMillis() + 5000;

        new APIHelper() {
            public void after(Object o) {
            }

            public Object task() throws APIException {
                Vika.api().messages.setActivity(null, userId, chatId, null);
                return null;
            }
        }.start();
    }

    public void updateTypingLabel() {
        if (typingUids.size() == 0) {
            gui.bottomTyping.setText("");
            gui.bottomTypingWrapper.deleteAll();
            return;
        }

        RichVector names = new RichVector(typingUids.size());
        RichVector fullnames = new RichVector(typingUids.size());
        Hashtable nameTable = new Hashtable();

        boolean useOnlyFirstName = true;
        LongEnumeration uids = typingUids.keys();
        while (uids.hasMoreElements()) {
            User user = UserStorage.instance.get(uids.nextElement());

            if (user != null) {
                if (nameTable.get(user.getFirstName()) != null) {
                    useOnlyFirstName = false;
                } else {
                    nameTable.put(user.getFirstName(), Boolean.TRUE);
                }
                names.addElement(user.getFirstName());
                fullnames.addElement(user.getFullName());
            }
        }

        String typing = useOnlyFirstName ? names.join(", ") + " " + tr(VikaResource.typing3) :
                fullnames.join(", ") + " " + tr(VikaResource.typing23);

        gui.bottomTyping.setText(typing);
        gui.bottomTypingWrapper.add(gui.bottomTyping);
    }

    public String getStorageKey() {
        return user != null ? "user" + userId : "chat" + chatId;
    }

    public void longPollUpdate(RichVector updates) {
        int addedItems = 0;

        for (int i = 0; i < updates.size(); ++i) {
            LongPollUpdate update = (LongPollUpdate) updates.elementAt(i);

            if (update instanceof AddMessageUpdate) {
                addedItems += handleAddMessageUpdate((AddMessageUpdate) update);
            } else if (update instanceof ReceivedMessageUpdate) {
                addedItems += handleReceivedMessageUpdate((ReceivedMessageUpdate) update);
            } else if (update instanceof ReplaceFlagsUpdate) {
                handleReplaceFlagsUpdate((ReplaceFlagsUpdate) update);
            } else if (update instanceof SetFlagsUpdate) {
                handleSetFlagsUpdate((SetFlagsUpdate) update);
            } else if (update instanceof DropFlagsUpdate) {
                handleDropFlagsUpdate((DropFlagsUpdate) update);
            } else if (update instanceof TypingUpdate) {
                TypingUpdate tu = (TypingUpdate) update;
                if (user != null && user.getId() == (tu.uid)) {
                    gui.title.typing(tu.uid);
                }
            } else if (chat != null && update instanceof ChatTypingUpdate) {
                ChatTypingUpdate tu = (ChatTypingUpdate) update;
                if (chat.getChatId() == (tu.chatId)) {
                    handleChatTyping(tu.uid);
                }
            } else if (user != null && update instanceof FriendOnlineUpdate) {
                FriendOnlineUpdate fou = (FriendOnlineUpdate) update;
                if (user.getId() == (fou.uid)) {
                    user = new User(user, true, System.currentTimeMillis() / 1000);
                    gui.title.update();
                }
            } else if (user != null && update instanceof FriendOfflineUpdate) {
                FriendOfflineUpdate fou = (FriendOfflineUpdate) update;
                if (user.getId() == (fou.uid)) {
                    user = new User(user, false, System.currentTimeMillis() / 1000);
                    gui.title.update();
                }
            } else if (chat != null && update instanceof ChatChangedUpdate) {
                ChatChangedUpdate ccu = (ChatChangedUpdate) update;
                if (chat.getChatId() == (ccu.chatId)) {
                    screen.updateChatInfo();
                }
            }
        }

        if (addedItems > 0) {
            final String storageKey = user != null ? "user" + userId : "chat" + chatId;
            Messages messages = new Messages(screen.getCurrentItems().last(ConversationScreen.MAX_ITEMS));
            MessagesStorage.instance.put(storageKey, messages);
            if (screen.getMainManager().getVerticalScroll() > gui.list.getVirtualHeight() - 2
                    * gui.list.getVisibleHeight()) {
                gui.list.scrollToBottom();
            }
        }
    }

    private void assignAttachments(Message.Builder message) {
        if (attachments.size() > 0) {
            message.setAttachments(attachments.transformNotNull(new Function1() {

                public Object apply(Object it) {
                    if (it instanceof PendingPhoto) {
                        String filename = ((PendingPhoto) it).filename;
                        return uploadedFiles.get(filename);
                    } else if (it instanceof PendingLocation) {
                        PendingLocation location = (PendingLocation) it;
                        return new Geo(location.getLatitude(), location.getLongitude());
                    } else return null;
                }
            }));
        }
    }

    private void assignForwardedMessages(Message.Builder message) {
        if (forwardedItems.size() > 0) {
            RichVector forwarder = new RichVector();
            for (int i = 0; i < forwardedItems.size(); ++i) {
                forwarder.addElement(forwardedItems.elementAt(i));
            }
            message.setForwardedMessages(forwarder);
        }
    }

    private Pair loadLocalMessagesSync(String storageKey) {
        RichVector listItems = null;
        Vector m = MessagesStorage.instance.get(storageKey).copy();
        if (m != null && m.size() > 0) {
            Message msgthis = (Message) m.lastElement();
            long lthis = (msgthis.getMid());
            Vector dialogs = MessagesStorage.instance.get("dialogs").copy();
            if (dialogs != null) {
                for (int i = 0; i < dialogs.size(); ++i) {
                    Message msg = (Message) dialogs.elementAt(i);
                    if (user != null) {
                        if (!msg.isFromChat() && msg.getUid() == (user.getId())) {
                            long lthat = (msg.getMid());
                            if (lthis < lthat) {
                                m = null;
                            }
                        }
                    } else if (chat != null) {
                        if (msg.isFromChat() && msg.getChatId() == (chat.getChatId())) {
                            long lthat = (msg.getMid());
                            if (lthis < lthat) {
                                m = null;
                            }
                        }
                    }
                }
            }
        }
        if (m != null) {
            listItems = MessageItem.insertDateMarkers(MessageItem.fromMessages(m));
            for (int i = 0; i < listItems.size(); ++i) {
                MessageItem _m = (MessageItem) listItems.elementAt(i);
                screen.getMids().put(_m.getMessage().getMid(), _m);
            }
        }

        if (m != null && listItems.size() > 0) {
            return new Pair(m, listItems);
        } else return null;
    }

    private boolean checkAttachmentsUploadStatus() {
        Vector filenames = attachments.transformNotNull(new Function1() {
            public Object apply(Object it) {
                if (it instanceof PendingPhoto && uploadedFiles.get(it) == null) {
                    return ((PendingPhoto) it).filename;
                } else return null;
            }
        });

        if (filenames.size() > 0) {
            uploadAttachments(filenames);
            return false;
        }

        return true;
    }

    private void uploadAttachments(final Vector filenames) {
        final String uploadText = tr(VikaResource.Uploading_images);

        screen.setMode(ConversationScreen.MODE_UPLOADING);
        gui.bottomLoadingText.setText(uploadText + "...");
        screen.updateSelectedItems();

        new Thread() {

            private void displayError() {
                UiApplication.getUiApplication().invokeLater(new Runnable() {
                    public void run() {
                        if (Dialog.ask(Dialog.D_YES_NO,
                                tr(VikaResource.Unable_to_upload_images_try_again)) == Dialog.YES) {
                            send();
                        }
                    }
                });
            }

            private void displayText(final String text) {
                UiApplication.getUiApplication().invokeLater(new Runnable() {
                    public void run() {
                        gui.bottomLoadingText.setText(text);
                    }
                });
            }

            private void placeBack() {
                UiApplication.getUiApplication().invokeLater(new Runnable() {
                    public void run() {
                        screen.updateSelectedItems(ConversationScreen.MODE_INPUT);
                    }
                });
            }

            private void resend() {
                UiApplication.getUiApplication().invokeLater(new Runnable() {
                    public void run() {
                        send();
                    }
                });
            }

            public void run() {
                int success = 0;

                for (int i = 0; i < filenames.size(); ++i) {
                    final String filename = (String) filenames.elementAt(i);

                    displayText(uploadText + " (" + i + " из " + filenames.size() + ")...");

                    try {
                        String server = Vika.api().photos.getMessagesUploadServer(null);
                        final PhotoUploadObject po = Vika.api().photos.upload(filename, server);
                        PhotoAttachment p = Vika.api().photos.saveMessagesPhoto(null, po.server,
                                po.photo, po.hash);
                        uploadedFiles.put(filename, p);
                        success++;
                    } catch (Exception e) {
                        // Nothing to do
                    }
                }

                placeBack();
                if (success == filenames.size()) {
                    resend();
                } else {
                    displayError();
                }
            }

        }.start();
    }

    private void handleChatTyping(long uid) {
        if (chat == null) {
            return;
        }

        typingUids.put(uid, new Long(System.currentTimeMillis() + 8500));
        updateTypingLabel();

        if (typingThread == null || !typingThread.isAlive()) {
            typingThread = new Thread() {
                public void run() {
                    while (true) {
                        try {
                            boolean changed = checkTypingIds();

                            if (changed) {
                                UiApplication.getUiApplication().invokeLater(new Runnable() {
                                    public void run() {
                                        updateTypingLabel();
                                    }
                                });
                            }

                            if (typingUids.size() == 0) {
                                break;
                            }

                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            interrupt();
                            break;
                        }
                    }
                }
            };

            typingThread.start();
        }
    }

    private int handleMessage(Message message, long fromId) {
        boolean fromMe = user != null
                && Vika.api().getToken().getUserId() == (user.getId());

        if (message.isOut() || fromMe) {
            boolean found = false;
            for (int j = sendingMessages.size() - 1; j >= 0; --j) {
                MessageItem mi = (MessageItem) sendingMessages.elementAt(j);
                Message m = mi.getMessage();

                if (m.getBody().equals(message.getBody()) &&
                        m.getAttachments().size() == message.getAttachments().size() &&
                        m.getForwardedMessages().size() == message.getForwardedMessages().size()) {
                    Message newMessage = mi.getMessage().edit().setId(message.getMid()).build();
                    screen.getMids().put(newMessage.getMid(), newMessage);
                    mi.delivered();
                    sendingMessages.removeElementAt(j);
                    found = true;
                    break;
                }
            }
            if (found) {
                RootScreen.getLastInstance().needToReloadMessages();
                return 1;
            }
        }

        RichVector currentItems = screen.getCurrentItems();
        if (chat == null) {
            if (!message.isFromChat() && fromId == userId) {
                currentItems.addElement(message);

                MessageItem mi = new MessageItem(message);
                screen.getMids().put(message.getMid(), mi);

                Vector newItem = new Vector(1);
                newItem.addElement(mi);
                gui.list.appendItems(newItem);
            }
        } else {
            if (message.isFromChat() && fromId == APIUtils.getChatId(chatId)) {
                currentItems.addElement(message);

                MessageItem mi = new MessageItem(message);
                screen.getMids().put(message.getMid(), mi);

                Vector newItem = new Vector(1);
                newItem.addElement(mi);
                gui.list.appendItems(newItem);
            }
        }

        RootScreen.getLastInstance().needToReloadMessages();
        return 1;
    }

    private int handleReceivedMessageUpdate(ReceivedMessageUpdate rmu) {
        return handleMessage(rmu.message, rmu.fromId);
    }

    private int handleAddMessageUpdate(AddMessageUpdate adu) {
        return handleMessage(adu.genMessage(), adu.fromId);
    }

    private void handleReplaceFlagsUpdate(ReplaceFlagsUpdate update) {
        RichVector currentItems = screen.getCurrentItems();
        Object o = screen.getMids().get(update.mid);
        if (o != null) {
            MessageItem mi = (MessageItem) o;
            mi.updateFlags(update.flags);
            if (currentItems.size() > 0) {
                if (currentItems.lastElement() == o) {
                    RootScreen.getLastInstance().needToReloadMessages();
                }
            }
        }
    }

    private void handleSetFlagsUpdate(SetFlagsUpdate update) {
        Object o = screen.getMids().get(update.mid);
        RichVector currentItems = screen.getCurrentItems();
        if (o != null) {
            MessageItem mi = (MessageItem) o;
            mi.updateFlags(mi.getMessage().getFlags() | update.mask);
            if (mi.getMessage().isDeleted()) {
                try {
                    gui.list.delete(mi);
                } catch (Exception ignored) {
                } // already deleted
            }
            if (currentItems.size() > 0) {
                if (currentItems.lastElement() == o) {
                    RootScreen.getLastInstance().needToReloadMessages();
                }
            }
        }
    }

    private void handleDropFlagsUpdate(DropFlagsUpdate update) {
        Object o = screen.getMids().get(update.mid);
        RichVector currentItems = screen.getCurrentItems();
        if (o != null) {
            MessageItem mi = (MessageItem) o;
            mi.updateFlags(mi.getMessage().getFlags() & ~update.mask);
            if (currentItems.size() > 0) {
                if (currentItems.lastElement() == o) {
                    RootScreen.getLastInstance().needToReloadMessages();
                }
            }
        }
    }

    private boolean checkTypingIds() {
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

        return changed;
    }

    private static String tr(int key) {
        return Local.tr(key);
    }

}
