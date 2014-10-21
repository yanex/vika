package org.yanex.vika.api.item;

import json.JSONArray;
import json.JSONException;
import json.JSONObject;
import net.rim.device.api.util.Persistable;
import org.apache.commons.lang.StringEscapeUtils;
import org.yanex.vika.Vika;
import org.yanex.vika.api.item.collections.Users;
import org.yanex.vika.api.longpoll.AddMessageUpdate;
import org.yanex.vika.storage.UserStorage;
import org.yanex.vika.util.StringUtils;
import org.yanex.vika.util.fun.ImmutableList;
import org.yanex.vika.util.fun.RichVector;

import java.util.Vector;

public class Message implements Persistable, Comparable {

  private final long id;
  private final long userId;

  private final long date;
  private final boolean read;
  private final boolean out;

  private final boolean deleted;

  private final boolean fromChat;
  private final String title;

  private final String body;
  private final ImmutableList attachments;
  private final ImmutableList forwardedMessages;

  private final long[] chatActive;
  private final long chatId;
  private final int usersCount;

  private final long adminId;

  private final Geo geo;
  private User user;
  private User myUser;

  private final Users chatActiveUsers;

  private Message(long id, long userId, long date, boolean read, boolean out, boolean deleted,
                  boolean fromChat, String title, String body, ImmutableList attachments,
                  ImmutableList forwardedMessages, long[] chatActive, long chatId, int usersCount,
                  long adminId, Geo geo, User user, User myUser, Users chatActiveUsers) {
    this.id = id;
    this.userId = userId;
    this.date = date;
    this.read = read;
    this.out = out;
    this.deleted = deleted;
    this.fromChat = fromChat;
    this.title = title == null ? "" : title;
    this.body = body == null ? "" : body;
    this.attachments = attachments == null ? ImmutableList.empty() : attachments;
    this.forwardedMessages = forwardedMessages == null ? ImmutableList.empty() : forwardedMessages;
    this.chatActive = chatActive;
    this.chatId = chatId;
    this.usersCount = usersCount;
    this.adminId = adminId;
    this.geo = geo;
    this.user = user;
    this.myUser = myUser;
    this.chatActiveUsers = chatActiveUsers;
  }

  public Message(AddMessageUpdate update, User user, User myUser) {
    id = update.mid;
    date = update.timestamp;

    read = update.isRead();
    out = update.isOut();
    deleted = update.isDeleted();

    boolean isChat = update.isChat();

    this.fromChat = isChat;

    if (isChat) {
      chatId = update.fromId;
      userId = update.from;

      user = UserStorage.instance.get(userId);
    } else {
      chatId = 0;
      userId = update.fromId;
    }

    title = decodeHTML(update.subject);
    body = decodeHTML(update.text);

    geo = update.geo;

    attachments = update.attachments == null ? ImmutableList.empty() : update.attachments;
    forwardedMessages = update.forwarded == null ? ImmutableList.empty() : update.forwarded;

    usersCount = 0;
    chatActiveUsers = new Users();
    chatActive = new long[0];
    adminId = 0;

    this.user = user;
    this.myUser = myUser;
  }

  public int compareTo(Object o2) {
    if (o2 instanceof Message) {
      Message m2 = (Message) o2;
      long l1 = date, l2 = m2.date;
      return l1 > l2 ? -1 : l1 == l2 ? 0 : 1;
    } else {
      return 0;
    }
  }

  private String decodeHTML(String str) {
    if (str == null) {
      return "";
    }
    // it's unlikely str will contain \r but anyway
    str = StringEscapeUtils.unescapeHtml(str).replace('\r', '\n');
    str = StringUtils.replace(str, "<br>", "\n");
    return str;
  }

  public boolean equals(Object obj) {
    if (obj instanceof Message) {
      Message that = (Message) obj;

      return body.equals(that.body) &&
          this.deleted == that.deleted &&
          this.fromChat == that.fromChat &&
          this.out == that.out &&
          this.read == that.read &&
          this.id == (that.id) &&
          this.userId == (that.userId) &&
          (that.chatId == this.chatId);
    } else {
      return false;
    }
  }

  public long getAdminId() {
    return adminId;
  }

  public ImmutableList getAttachments() {
    return attachments;
  }

  public String getBody() {
    return body;
  }

  public long[] getChatActive() {
    return chatActive;
  }

  public Users getChatActiveUsers() {
    return chatActiveUsers;
  }

  public long getChatId() {
    return chatId;
  }

  public String getCode() {
    if (chatId != 0) {
      return "chat" + chatId;
    } else {
      return "user" + userId;
    }
  }

  public long getDate() {
    return date;
  }

  public long getFlags() {
    long flags = 0;

    if (!read) {
      flags |= 1;
    }
    if (out) {
      flags |= 2;
    }
    if (deleted) {
      flags |= 128;
    }
    if (attachments != null && attachments.size() > 0) {
      flags |= 512;
    }

    return flags;
  }

  public ImmutableList getForwardedMessages() {
    return forwardedMessages;
  }

  public Geo getGeo() {
    return geo;
  }

  public long getMid() {
    return id;
  }

  public User getMyUser() {
    return myUser;
  }

  public long getUid() {
    return userId;
  }

  public String getTitle() {
    return title;
  }

  public User getUser() {
    return user;
  }

  public int getUsersCount() {
    return usersCount;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public boolean isFromChat() {
    return fromChat;
  }

  public boolean isOut() {
    return out;
  }

  public boolean isRead() {
    return read;
  }

  public Message(JSONObject obj) throws JSONException {
    id = obj.optLong("id");
    userId = obj.optLong("user_id");
    date = obj.optLong("date");
    read = obj.optInt("read_state") == 1;
    out = obj.optInt("out") == 1;
    title = decodeHTML(obj.optString("title"));
    body = decodeHTML(obj.optString("body"));

    if (obj.has("geo")) {
      geo = new Geo(obj.getJSONObject("geo"));
    } else geo = null;

    chatId = obj.optLong("chat_id");
    usersCount = obj.optInt("users_count");
    adminId = obj.optLong("admin_id");
    deleted = obj.optInt("deleted") == 1;

    chatActiveUsers = new Users();

    int i, l;
    JSONArray a;

    User myUser = UserStorage.instance.get(Vika.api().getToken().getUserId());
    this.myUser = myUser;

    User user1 = UserStorage.instance.get(userId);
    if (user1 != null) {
      this.user = user1;
    }

    if (obj.has("fwd_messages")) {
      a = obj.getJSONArray("fwd_messages");
      l = a.length();
      Vector forwardedMessages = new RichVector();
      Message message;
      for (i = 0; i < l; ++i) {
        message = new Message(a.getJSONObject(i));
        if (message != null) {
          message.myUser = myUser;

          User user = UserStorage.instance.get(message.getUid());
          if (user == null) {
            Users users = null;
            try {
              users = Vika.api().users.get(null, message.getUid());
            } catch (Exception e) {
              try {
                users = Vika.api().users.get(null, message.getUid());
              } catch (Exception e2) {
                e.printStackTrace();
              }
            }
            if (users != null) {
              user = users.get(0);
            }
          }

          message.user = user;
          forwardedMessages.addElement(message);
        }
      }
      this.forwardedMessages = new ImmutableList(forwardedMessages, Message.class);
    } else forwardedMessages = ImmutableList.empty();

    if (obj.has("chat_active")) {
      chatActive = Message.split(obj.getJSONArray("chat_active"));
      fromChat = true;
    } else {
      fromChat = false;
      chatActive = new long[0];
    }

    if (obj.has("attachments")) {
      a = obj.getJSONArray("attachments");
      l = a.length();
      RichVector attachments = new RichVector();
      Attachment attachment;
      for (i = 0; i < l; ++i) {
        attachment = Attachment.loadAttachment(a.getJSONObject(i));
        if (attachment != null) {
          attachments.addElement(attachment);
        }
      }
      this.attachments = new ImmutableList(attachments, Attachment.class);
    } else attachments = ImmutableList.empty();

  }

  public String toString() {
    return "{Message = uid: " + userId + ", title: " + title + ", body: " + body + "}";
  }

  private static long[] split(JSONArray arr) throws JSONException {
    long[] items = new long[arr.length()];
    for (int i = 0; i < items.length; ++i) {
      items[i] = arr.getLong(i);
    }
    return items;
  }

  public Builder edit() {
    return new Builder(this);
  }

  public static class Builder {

    private long id;
    private long userId;

    private long date;
    private boolean read;
    private boolean out;

    private boolean deleted;

    private boolean fromChat;
    private String title;

    private String body;
    private Vector attachments;
    private Vector forwardedMessages;

    private long[] chatActive;
    private long chatId;
    private int usersCount;

    private long adminId;

    private Geo geo;
    private User user;
    private User myUser;

    private Users chatActiveUsers;

    public Builder() {

    }

    public Builder(Message message) {
      id = message.id;
      userId = message.userId;

      date = message.date;
      read = message.read;
      out = message.out;

      deleted = message.deleted;
      fromChat = message.fromChat;
      title = message.title;

      body = message.body;
      attachments = message.attachments.copy();
      forwardedMessages = message.forwardedMessages.copy();

      chatActive = message.chatActive;
      chatId = message.chatId;
      usersCount = message.usersCount;

      adminId = message.adminId;
      geo = message.geo;
      user = message.user;
      myUser = message.myUser;
      chatActiveUsers = message.chatActiveUsers;
    }

    public long getId() {
      return id;
    }

    public Builder setId(long id) {
      this.id = id;
      return this;
    }

    public long getUserId() {
      return userId;
    }

    public Builder setUserId(long userId) {
      this.userId = userId;
      return this;
    }

    public long getDate() {
      return date;
    }

    public Builder setDate(long date) {
      this.date = date;
      return this;
    }

    public boolean isRead() {
      return read;
    }

    public Builder setRead(boolean read) {
      this.read = read;
      return this;
    }

    public boolean isOut() {
      return out;
    }

    public Builder setOut(boolean out) {
      this.out = out;
      return this;
    }

    public boolean isDeleted() {
      return deleted;
    }

    public Builder setDeleted(boolean deleted) {
      this.deleted = deleted;
      return this;
    }

    public boolean isFromChat() {
      return fromChat;
    }

    public Builder setFromChat(boolean fromChat) {
      this.fromChat = fromChat;
      return this;
    }

    public String getTitle() {
      return title;
    }

    public Builder setTitle(String title) {
      this.title = title;
      return this;
    }

    public String getBody() {
      return body;
    }

    public Builder setBody(String body) {
      this.body = body;
      return this;
    }

    public Vector getAttachments() {
      return attachments;
    }

    public Builder setAttachments(Vector attachments) {
      this.attachments = attachments;
      return this;
    }

    public Vector getForwardedMessages() {
      return forwardedMessages;
    }

    public Builder setForwardedMessages(Vector forwardedMessages) {
      this.forwardedMessages = forwardedMessages;
      return this;
    }

    public long[] getChatActive() {
      return chatActive;
    }

    public Builder setChatActive(long[] chatActive) {
      this.chatActive = chatActive;
      return this;
    }

    public long getChatId() {
      return chatId;
    }

    public Builder setChatId(long chatId) {
      this.chatId = chatId;
      return this;
    }

    public int getUsersCount() {
      return usersCount;
    }

    public Builder setUsersCount(int usersCount) {
      this.usersCount = usersCount;
      return this;
    }

    public long getAdminId() {
      return adminId;
    }

    public Builder setAdminId(long adminId) {
      this.adminId = adminId;
      return this;
    }

    public Geo getGeo() {
      return geo;
    }

    public Builder setGeo(Geo geo) {
      this.geo = geo;
      return this;
    }

    public User getUser() {
      return user;
    }

    public Builder setUser(User user) {
      this.user = user;
      return this;
    }

    public User getMyUser() {
      return myUser;
    }

    public Builder setMyUser(User myUser) {
      this.myUser = myUser;
      return this;
    }

    public Users getChatActiveUsers() {
      return chatActiveUsers;
    }

    public Builder setChatActiveUsers(Users chatActiveUsers) {
      this.chatActiveUsers = chatActiveUsers;
      return this;
    }

    public Builder setFlags(long flags) {
      read = (flags & 1) == 0;
      out = (flags & 2) > 0;
      deleted = (flags & 128) > 0;
      return this;
    }

    public Builder update(Message message) {
      date = message.date;
      read = message.read;
      out = message.out;
      deleted = message.deleted;
      if (message.chatActive != null && message.chatActive.length > 0) {
        chatActive = message.chatActive;
      }
      usersCount = message.usersCount;
      if (message.user != null) {
        user = message.user;
      }
      if (message.myUser != null) {
        myUser = message.myUser;
      }
      if (message.chatActiveUsers != null && message.chatActiveUsers.size() > 0) {
        chatActiveUsers = message.chatActiveUsers;
      }
      return this;
    }

    public Message build() {
      return new Message(id, userId, date, read, out, deleted, fromChat, title, body,
          new ImmutableList(attachments), new ImmutableList(forwardedMessages),
          chatActive, chatId, usersCount, adminId,
          geo, user, myUser, chatActiveUsers);
    }

  }

}
