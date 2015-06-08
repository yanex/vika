package org.yanex.vika.api.longpoll;

import org.yanex.vika.api.item.Message;

public class ReceivedMessageUpdate extends MessageUpdate {

    public final Message message;
    public final long fromId;

    public ReceivedMessageUpdate(Message message, long fromId) {
        this.message = message;
        this.fromId = fromId;
    }

    public Message genMessage() {
        return message;
    }

    public long getFromId() {
        return fromId;
    }

}
