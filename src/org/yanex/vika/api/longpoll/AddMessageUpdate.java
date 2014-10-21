package org.yanex.vika.api.longpoll;

import org.yanex.vika.Vika;
import org.yanex.vika.api.item.Geo;
import org.yanex.vika.api.item.Message;
import org.yanex.vika.api.item.User;
import org.yanex.vika.storage.UserStorage;
import org.yanex.vika.util.fun.ImmutableList;

public class AddMessageUpdate extends MessageUpdate {

  public final long mid;
  private final long flags;
  public final long fromId;
  public final long timestamp;
  public final String subject;
  public final String text;
  public final long from;

  public final ImmutableList attachments;
  public final ImmutableList forwarded;

  public final Geo geo;

  public AddMessageUpdate(
      long mid, long flags, long fromId, long timestamp, String subject,
      String text, ImmutableList attachments, ImmutableList forwarded, long from, Geo geo)
  {
    this.mid = mid;
    this.flags = flags;
    this.fromId = fromId;
    this.timestamp = timestamp;
    this.subject = subject;
    this.text = text;
    this.attachments = attachments == null ? ImmutableList.empty() : attachments;
    this.from = from;
    this.forwarded = forwarded == null ? ImmutableList.empty() : forwarded;
    this.geo = geo;
  }

  public boolean isChat() {
    return from != 0;
  }

  public boolean isDeleted() {
    return (flags & 128) > 0;
  }

  public boolean isOut() {
    return (flags & 2) > 0;
  }

  public boolean isRead() {
    return !((flags & 1) > 0);
  }

  public Message genMessage() {
    long uid = isChat() ? from : fromId;
    User user = UserStorage.instance.get(uid), myUser = UserStorage.instance.get(Vika.api()
        .getToken().getUserId());

    return new Message(this, user, myUser);
  }

  public long getFromId() {
    return fromId;
  }
}
