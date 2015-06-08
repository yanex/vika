package org.yanex.vika.api.longpoll;

import org.yanex.vika.api.item.Message;

public class DropFlagsUpdate extends FlagsUpdate {

    public DropFlagsUpdate(long mid, long mask, long uid) {
        super(mid, mask, uid);
    }

    public Message modify(Message message) {
        return message.edit().setFlags(message.getFlags() & ~mask).build();
    }

}
