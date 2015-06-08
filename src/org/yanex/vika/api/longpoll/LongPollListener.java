package org.yanex.vika.api.longpoll;

import org.yanex.vika.util.fun.RichVector;

public interface LongPollListener {

    void longPollUpdate(RichVector updates);

}
