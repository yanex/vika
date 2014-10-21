package org.yanex.vika.api.longpoll;

public class ChatChangedUpdate implements LongPollUpdate {

  public final long chatId;
  public final boolean byUser;

  public ChatChangedUpdate(long chatId, boolean byUser) {
    this.chatId = chatId;
    this.byUser = byUser;
  }

}
