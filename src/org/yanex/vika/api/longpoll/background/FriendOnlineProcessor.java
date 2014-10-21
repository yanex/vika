package org.yanex.vika.api.longpoll.background;

import org.yanex.vika.api.item.User;
import org.yanex.vika.api.longpoll.FriendOnlineUpdate;
import org.yanex.vika.api.longpoll.LongPollProcessor;
import org.yanex.vika.api.longpoll.LongPollProcessorFactory;
import org.yanex.vika.api.longpoll.LongPollUpdate;
import org.yanex.vika.storage.UserStorage;

public class FriendOnlineProcessor implements LongPollProcessor {

  public final FriendOnlineUpdate update;

  public FriendOnlineProcessor(FriendOnlineUpdate update) {
    this.update = update;
  }

  public boolean process() {
    User u = UserStorage.instance.get(update.uid);
    if (u != null) {
      User newUser = new User(u, true, System.currentTimeMillis() / 1000);
      UserStorage.instance.updateUsers(newUser);
    }
    return false;
  }

  public static class Factory implements LongPollProcessorFactory {

    public LongPollProcessor create(LongPollUpdate update) {
      return new FriendOnlineProcessor((FriendOnlineUpdate) update);
    }

    public Class processorFor() {
      return FriendOnlineUpdate.class;
    }

  }

}
