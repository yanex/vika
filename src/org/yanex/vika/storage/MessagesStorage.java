//#preprocess

package org.yanex.vika.storage;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import org.yanex.vika.Vika;
import org.yanex.vika.api.item.Message;
import org.yanex.vika.api.item.User;
import org.yanex.vika.api.item.collections.Messages;
import org.yanex.vika.api.item.collections.Users;
import org.yanex.vika.util.fun.Function1;
import org.yanex.vika.util.fun.RichVector;
import org.yanex.vika.util.fun.SortingVector;

import java.util.Hashtable;
import java.util.Vector;

public class MessagesStorage {

  private static final long KEY = 0x33ac2abf69993181L;

  private final PersistentObject messagesPO;
  private final Hashtable messages;

  public static final String DIALOGS = "dialogs";

  public static final MessagesStorage instance = new MessagesStorage();

  private MessagesStorage() {
    messagesPO = PersistentStore.getPersistentObject(MessagesStorage.KEY);
    if (messagesPO.getContents() == null) {
      messages = new Hashtable();
      messagesPO.setContents(messages);
    } else {
      Object o = messagesPO.getContents();
      if (o instanceof Hashtable) {
        messages = (Hashtable) o;
      } else {
        messages = new Hashtable();
        messagesPO.setContents(messages);
      }
    }
  }

  public void clear() {
    messages.clear();
    update();
  }

  public void delete(String key) {
    if (messages.remove(key) != null) {
      update();
    }
  }

  private User getMyUser() {
    User myUser = null;
    try {
      myUser = UserStorage.instance.get(Vika.api().getToken().getUserId());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return myUser;
  }

  public synchronized Messages get(String key) {
    Object o = messages.get(key);
    if (o == null) {
      return new Messages();
    }

    Messages v = (Messages) o;
    final User myUser = getMyUser();

    return new Messages(v.transform(new Function1() {

      public Object apply(Object it) {
        Message.Builder m = ((Message) it).edit();

        m.setUser(UserStorage.instance.get(m.getUser() != null ? m.getUser().getId() : m
            .getUserId()));

        if (m.getMyUser() != null) {
          m.setMyUser(myUser);
        }

        m.setChatActiveUsers(new Users(m.getChatActiveUsers().transform(new Function1() {
          public Object apply(Object it) {
            User u = UserStorage.instance.get(((User) it).getId());
            return u != null ? u : it;
          }
        })));

        return m.build();
      }
    }));
  }

  private synchronized Messages getFast(String key) {
    Object o = messages.get(key);
    return o == null ? null : (Messages) o;
  }

  public synchronized long getLastMid() {
    Object o = messages.get("dialogs");
    if (o == null) {
      return -1;
    }

    Messages m = (Messages) o;
    return m.get(0).getMid();
  }

  public synchronized void put(String key, Messages value) {
    putFast(key, value);
    update();
  }

  public synchronized void putFast(String key, Messages value) {
    messages.put(key, value);
    UserStorage.instance.updateMessages(value);
  }

  private void update() {
    messagesPO.setContents(messages);
    messagesPO.commit();
  }

  public synchronized void updateDialogs() {
    Object o = getFast("dialogs");
    if (o == null) {
      return;
    }

    int i;

    RichVector dialogs = ((Messages) o).copy();
    for (i = 0; i < dialogs.size(); ++i) {
      Message dm = (Message) dialogs.get(i);
      String key = null;
      if (dm.isFromChat()) {
        key = "chat" + dm.getChatId();
      } else {
        key = "user" + dm.getUid();
      }
      Messages history = getFast(key);
      if (history != null && history.size() > 0) {
        Message lm = history.get(history.size() - 1);
        dialogs.setElementAt(dm.edit().update(lm).build(), i);
      }
    }

    putFast("dialogs", new Messages(new SortingVector(dialogs).sort().copy()));

    new Thread() {
      public void run() {
        update();
      }
    }.start();
  }
}
