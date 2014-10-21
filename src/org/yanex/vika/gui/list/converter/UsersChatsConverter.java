package org.yanex.vika.gui.list.converter;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.SimpleSortingVector;
import org.yanex.vika.api.item.Chat;
import org.yanex.vika.api.item.User;
import org.yanex.vika.api.item.collections.Users;
import org.yanex.vika.api.item.collections.UsersChats;
import org.yanex.vika.gui.list.item.ChatItem;
import org.yanex.vika.gui.list.item.SeparatorItem;
import org.yanex.vika.gui.list.item.UserItem;
import org.yanex.vika.util.fun.RichVector;

final class UsersChatsConverter {

  static RichVector users(Users userVector) {
    final RichVector listItems = new RichVector();

    for (int i = 0; i < userVector.size(); ++i) {
      Object o = userVector.getObject(i);
      if (o instanceof User) {
        listItems.addElement(new UserItem(userVector.get(i)));
      }
    }

    return listItems;
  }

  static RichVector sortedUsers(Users users) {
    final RichVector listItems = new RichVector();
    char last = ' ';

    SimpleSortingVector v = new SimpleSortingVector();
    v.setSortComparator(new Comparator() {

      public int compare(Object o1, Object o2) {
        if (o1 instanceof User && o2 instanceof User) {
          return ((User) o1).getFullName().compareTo(((User) o2).getFullName());
        } else {
          return 0;
        }
      }
    });

    for (int i = 0; i < users.size(); ++i) {
      v.addElement(users.getObject(i));
    }

    v.reSort();

    for (int i = 0; i < v.size(); ++i) {
      if (v.elementAt(i) instanceof User) {
        User u = (User) v.elementAt(i);
        char c = u.getFullName().charAt(0);
        if (c != last) {
          listItems.addElement(new SeparatorItem(c + ""));
          last = c;
        }

        listItems.addElement(new UserItem(u));
      }
    }

    return listItems;
  }

  static RichVector usersChats(UsersChats userVector) {
    final RichVector listItems = new RichVector();

    for (int i = 0; i < userVector.size(); ++i) {
      Object o = userVector.getObject(i);
      if (o instanceof User) {
        listItems.addElement(new UserItem((User) o));
      } else if (o instanceof Chat) {
        listItems.addElement(new ChatItem((Chat) o));
      }
    }

    return listItems;
  }

}
