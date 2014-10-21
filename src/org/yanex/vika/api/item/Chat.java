package org.yanex.vika.api.item;

import net.rim.device.api.util.Persistable;
import org.yanex.vika.api.item.collections.Users;

public class Chat implements Persistable {

  private final long chatId;
  private final String title;
  private final Users activeUsers;
  private final int usersCount;

  public Chat(Message message) {
    chatId = message.getChatId();
    title = message.getTitle();
    activeUsers = message.getChatActiveUsers();
    usersCount = message.getUsersCount();
  }

  public Chat(long chatId, String title, Users activeUsers, int usersCount) {
    if (activeUsers == null) {
      throw new IllegalArgumentException("activeUsers field can't be null");
    }

    this.chatId = chatId;
    this.activeUsers = activeUsers;
    this.usersCount = usersCount;

    if (title == null) {
      StringBuffer buffer = new StringBuffer("");
      for (int i = 0; i < activeUsers.size(); ++i) {
        if (buffer.length()>0) {
          buffer.append(", ");
        }
        buffer.append(activeUsers.get(i).getFirstName());
      }
      this.title = buffer.toString();
    } else {
      this.title = title;
    }
  }

  public long getChatId() {
    return chatId;
  }

  public String getTitle() {
    return title;
  }

  public Users getActiveUsers() {
    return activeUsers;
  }

  public int getUsersCount() {
    return usersCount;
  }

}
