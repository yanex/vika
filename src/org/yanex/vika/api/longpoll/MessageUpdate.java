package org.yanex.vika.api.longpoll;

import org.yanex.vika.api.item.Message;

public abstract class MessageUpdate implements LongPollUpdate {

    public abstract Message genMessage();

    public abstract long getFromId();

}
