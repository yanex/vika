package org.yanex.vika.api.longpoll;

import org.yanex.vika.api.item.Message;

public class SetFlagsUpdate extends FlagsUpdate {

  public SetFlagsUpdate(long mid, long mask, long uid) {
    super(mid, mask, uid);
  }

  public Message modify(Message message) {
    return message.edit().setFlags(message.getFlags() | mask).build();
  }
}
