package org.yanex.vika.api.longpoll.background;

import net.rim.device.api.ui.UiApplication;
import org.yanex.vika.RootScreen;
import org.yanex.vika.api.APIUtils;
import org.yanex.vika.api.item.Message;
import org.yanex.vika.api.item.collections.Messages;
import org.yanex.vika.api.longpoll.FlagsUpdate;
import org.yanex.vika.api.longpoll.LongPollProcessor;
import org.yanex.vika.api.longpoll.LongPollProcessorFactory;
import org.yanex.vika.api.longpoll.LongPollUpdate;
import org.yanex.vika.storage.MessagesStorage;
import org.yanex.vika.util.fun.RichVector;

public class FlagsProcessor implements LongPollProcessor {

  private final FlagsUpdate update;

  public FlagsProcessor(FlagsUpdate update) {
    this.update = update;
  }

  public boolean process() {
    String realUid = APIUtils.getTalkId(update.uid);
    final RichVector conversation = MessagesStorage.instance.get(realUid).copy();

    int foundIndex = Integer.MIN_VALUE;
    Message foundMessage = null;

    for (int i = 0; i < conversation.size(); ++i) {
      foundMessage = (Message) conversation.elementAt(i);
      if (foundMessage.getMid() == (update.mid)) {
        conversation.replace(foundIndex = i, update.modify(foundMessage));
        foundIndex = i;
        break;
      }
    }

    if (foundIndex >= 0) {
      MessagesStorage.instance.put(realUid, new Messages(conversation));
      if (foundMessage.getUser() == null) {
        return false;
      }
      updateDialogs(foundMessage);
    }

    return false;
  }

  private void updateDialogs(Message foundMessage) {
    RichVector dialogs = MessagesStorage.instance.get(MessagesStorage.DIALOGS).copy();
    for (int i = 0; i < dialogs.size(); ++i) {
      Message dialogMessage = (Message) dialogs.get(i);
      if (APIUtils.isFromDialog(dialogMessage, update.uid)) {
        dialogMessage = dialogMessage.edit().update(foundMessage).build();
        boolean first = i > 0;
        if (first) {
          dialogs = dialogs.remove(i).add(0, dialogMessage);
        }

        MessagesStorage.instance.put("dialogs", new Messages(dialogs));

        if (UiApplication.getUiApplication().getActiveScreen() instanceof RootScreen) {
          RootScreen screen = (RootScreen) UiApplication
              .getUiApplication().getActiveScreen();
          screen.updateMessages();
        } else {
          RootScreen screen = RootScreen.getLastInstance();
          if (screen != null) {
            screen.needToReloadMessages();
          }
        }

        break;
      }
    }
  }

  public static class Factory implements LongPollProcessorFactory {

    public LongPollProcessor create(LongPollUpdate update) {
      return new FlagsProcessor((FlagsUpdate) update);
    }

    public Class processorFor() {
      return FlagsUpdate.class;
    }

  }

}
