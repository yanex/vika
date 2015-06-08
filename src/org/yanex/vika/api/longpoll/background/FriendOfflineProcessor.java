package org.yanex.vika.api.longpoll.background;

import org.yanex.vika.api.item.User;
import org.yanex.vika.api.longpoll.FriendOfflineUpdate;
import org.yanex.vika.api.longpoll.LongPollProcessor;
import org.yanex.vika.api.longpoll.LongPollProcessorFactory;
import org.yanex.vika.api.longpoll.LongPollUpdate;
import org.yanex.vika.storage.UserStorage;

public class FriendOfflineProcessor implements LongPollProcessor {

    public final FriendOfflineUpdate update;

    public FriendOfflineProcessor(FriendOfflineUpdate update) {
        this.update = update;
    }

    public boolean process() {
        User u = UserStorage.instance.get(update.uid);
        if (u != null) {
            User newUser = new User(u, false, System.currentTimeMillis() / 1000);
            UserStorage.instance.updateUsers(newUser);
        }
        return false;
    }

    public static class Factory implements LongPollProcessorFactory {

        public LongPollProcessor create(LongPollUpdate update) {
            return new FriendOfflineProcessor((FriendOfflineUpdate) update);
        }

        public Class processorFor() {
            return FriendOfflineUpdate.class;
        }

    }

}
