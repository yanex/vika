package org.yanex.vika.api.longpoll;

public class FriendOfflineUpdate implements LongPollUpdate {

  public final long uid;

  public FriendOfflineUpdate(long uid) {
    this.uid = uid;
  }

}
