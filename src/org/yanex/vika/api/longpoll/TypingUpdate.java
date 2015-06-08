package org.yanex.vika.api.longpoll;

public class TypingUpdate implements LongPollUpdate {

    public final long uid;

    public TypingUpdate(long uid) {
        this.uid = uid;
    }

}
