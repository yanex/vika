package org.yanex.vika.api.longpoll;

import org.yanex.vika.api.item.Message;

public abstract class FlagsUpdate implements LongPollUpdate {

  public final long mid;
  public final long mask;
  public final long uid;

  public FlagsUpdate(long mid, long mask, long uid) {
    this.mid = mid;
    this.mask = mask;
    this.uid = uid;
  }

  public abstract Message modify(Message message);

}
