package org.yanex.vika.api.longpoll;

public class FriendOnlineUpdate implements LongPollUpdate {

    public final long uid;

    public FriendOnlineUpdate(long uid) {
        this.uid = uid;
    }

}
