package org.yanex.vika.api.longpoll;

public class ReplaceFlagsUpdate implements LongPollUpdate {

    public final long mid;
    public final long flags;

    public ReplaceFlagsUpdate(long mid, long flags) {
        this.mid = mid;
        this.flags = flags;
    }

}
