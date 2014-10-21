package org.yanex.vika.api.longpoll.background;

import net.rim.device.api.ui.UiApplication;
import org.yanex.vika.ConversationScreen;
import org.yanex.vika.RootScreen;
import org.yanex.vika.api.APIUtils;
import org.yanex.vika.api.item.Message;
import org.yanex.vika.api.item.collections.Messages;
import org.yanex.vika.api.longpoll.LongPollProcessor;
import org.yanex.vika.api.longpoll.LongPollProcessorFactory;
import org.yanex.vika.api.longpoll.LongPollUpdate;
import org.yanex.vika.api.longpoll.MessageUpdate;
import org.yanex.vika.storage.MessagesStorage;
import org.yanex.vika.util.bb.Indicator;
import org.yanex.vika.util.fun.RichVector;

public class MessageProcessor implements LongPollProcessor {

  private final MessageUpdate update;

  public MessageProcessor(MessageUpdate update) {
    this.update = update;
  }

  public boolean process() {
    boolean notify = false;

    final Message message = update.genMessage();
    final long fromId = update.getFromId();
    final String realUid = APIUtils.getTalkId(fromId);
    final Object displayedUid = ConversationScreen.OPENTALKS.get(realUid);

    if (displayedUid == null) { // opened conversation will handle this by itself
      // if already added
      if (updateConversation(realUid, message)) {
        return false;
      }

      if (message.getUser() != null) {
        boolean found = false, lastDialog = false;
        Message foundDialog = null;
        RichVector dialogs = MessagesStorage.instance.get(MessagesStorage.DIALOGS).copy();

        for (int i = 0; i < dialogs.size(); ++i) {
          foundDialog = (Message) dialogs.elementAt(i);
          if (APIUtils.isFromDialog(foundDialog, fromId)) {
            found = true;
            foundDialog = foundDialog.edit().update(message).build();
            if (i > 0) {
              dialogs.removeElementAt(i);
              dialogs.insertElementAt(foundDialog, 0);
              lastDialog = false;
            } else {
              lastDialog = true;
            }
            break;
          }
        }

        if (!found) {
          MessagesStorage.instance.delete("dialogs");
        } else {
          MessagesStorage.instance.put("dialogs", new Messages(dialogs));
        }

        notifyMessageReload();
      }
    } else {
      justUpdateDialogs();
    }

    if (!UiApplication.getUiApplication().isForeground()) {
      if (!message.isOut()) {
        notify = true;
        Indicator.instance.incValue();
      }
    }

    return notify;
  }

  private void notifyMessageReload() {
    if (UiApplication.getUiApplication().getActiveScreen() instanceof RootScreen) {
      RootScreen screen = (RootScreen) UiApplication.getUiApplication()
          .getActiveScreen();
      screen.updateMessages();
    } else {
      RootScreen screen = RootScreen.getLastInstance();
      if (screen != null) {
        screen.needToReloadMessages();
      }
    }
  }

  private void justUpdateDialogs() {
    notifyMessageReload();
  }

  private boolean updateConversation(String realUid, Message newMessage) {
    final RichVector conversation = MessagesStorage.instance.get(realUid).copy();
    if (conversation.size() > 0) {
      Message lastMessage = (Message) conversation.lastElement();
      if (newMessage.getMid() == (lastMessage.getMid())) {
        return true;
      }
    }
    conversation.addElement(newMessage);
    MessagesStorage.instance.put(realUid, new Messages(conversation));
    return false;
  }

  public static class Factory implements LongPollProcessorFactory {

    public LongPollProcessor create(LongPollUpdate update) {
      return new MessageProcessor((MessageUpdate) update);
    }

    public Class processorFor() {
      return MessageUpdate.class;
    }

  }

}
