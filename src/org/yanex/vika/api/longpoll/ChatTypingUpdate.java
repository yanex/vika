package org.yanex.vika.api.longpoll;

public class ChatTypingUpdate implements LongPollUpdate {

    public final long uid;
    public final long chatId;

    public ChatTypingUpdate(long uid, long chatId) {
        this.uid = uid;
        this.chatId = chatId;
    }

}
