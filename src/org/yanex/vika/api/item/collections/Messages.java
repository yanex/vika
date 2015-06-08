package org.yanex.vika.api.item.collections;

import net.rim.device.api.util.Persistable;
import org.yanex.vika.api.item.Message;
import org.yanex.vika.util.fun.ImmutableList;

import java.util.Vector;

public class Messages extends ImmutableList implements Persistable {

    public Messages() {
        super(EMPTY_VECTOR, Message.class);
    }

    public Messages(Vector vector) {
        super(vector, Message.class);
    }

    public Message get(int index) {
        return (Message) getObject(index);
    }
}
