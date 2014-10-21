package org.yanex.vika.storage;

import json.JSONArray;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.util.LongHashtable;
import org.yanex.vika.api.item.Message;
import org.yanex.vika.api.item.User;
import org.yanex.vika.api.item.collections.Messages;
import org.yanex.vika.api.item.collections.Users;
import org.yanex.vika.api.item.collections.UsersChats;

import java.util.Hashtable;
import java.util.Vector;

public class UserStorage {

  private static final long KEY = 0x1d065c7bcc022916L;

  private PersistentObject userPO;
  private LongHashtable user;

  public static final UserStorage instance = new UserStorage();

  private UserStorage() {
    userPO = PersistentStore.getPersistentObject(UserStorage.KEY);
    if (userPO.getContents() == null) {
      user = new LongHashtable();
      userPO.setContents(user);
    } else {
      Object o = userPO.getContents();
      if (o instanceof Hashtable) {
        user = (LongHashtable) o;
      } else {
        user = new LongHashtable();
        userPO.setContents(user);
      }
    }
  }

  public void clear() {
    user.clear();
    update();
  }

  public synchronized User get(long key) {
    Object o = user.get(key);
    if (o == null) {
      return null;
    } else {
      return (User) o;
    }
  }

  private synchronized void put(long key, User value) {
    user.put(key, value);
  }

  private void update() {
    userPO.setContents(user);
    userPO.commit();
  }

  void updateMessages(Messages messages) {
    for (int i = 0; i < messages.size(); ++i) {
      updateUsers(messages.get(i));
    }
    update();
  }

  public void updateOnline(JSONArray items) {
    try {
      Hashtable online = new Hashtable();
      int i;

      for (i = 0; i < items.length(); ++i) {
        long key = items.getLong(i);
        User u = get(key);
        if (u != null) {
          User newUser = new User(u, true, System.currentTimeMillis() / 1000);
          online.put(new Long(u.getId()), newUser);
          put(key, newUser);
        }
      }

      Vector allLocal = UsersStorage.instance.get("all").copy();
      boolean changed = false;
      if (allLocal != null) {
        for (i = 0; i < allLocal.size(); ++i) {
          User u = (User) allLocal.elementAt(i);
          if (online.get(new Long(u.getId())) == null) {
            u = (User) user.get(u.getId());
            if (u != null) {
              User newUser = new User(u, false, u.getLastSeen());
              changed = true;
              allLocal.setElementAt(newUser, i);
            }
          }
        }
      }
      if (changed) {
        UsersStorage.instance.put("all", new UsersChats(allLocal));
      }
      update();
    } catch (json.JSONException e) {
      e.printStackTrace();
    }
  }

  private void updateUsers(Message m) {
    if (m.getUser() != null) {
      put(m.getUser().getId(), m.getUser());
    }
    if (m.getChatActiveUsers() != null) {
      for (int j = 0; j < m.getChatActiveUsers().size(); ++j) {
        User u = m.getChatActiveUsers().get(j);
        if (u != null) {
          put(u.getId(), u);
        }
      }
    }
  }

  public void updateUsers(User user) {
    if (user != null) {
      put(user.getId(), user);
      update();
    }
  }

  public void updateUsers(Users items) {
    for (int i = 0; i < items.size(); ++i) {
      User u = items.get(i);
      put(u.getId(), u);
    }
    update();
  }
}
