package org.yanex.vika.api.longpoll;

public class MessageDeleteUpdate implements LongPollUpdate {

  public final String mid;

  public MessageDeleteUpdate(String mid) {
    this.mid = mid;
  }

}
