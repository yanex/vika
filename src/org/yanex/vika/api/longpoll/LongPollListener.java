package org.yanex.vika.api.longpoll;

import org.yanex.vika.util.fun.RichVector;

public interface LongPollListener {

    public void longPollUpdate(RichVector updates);

}
