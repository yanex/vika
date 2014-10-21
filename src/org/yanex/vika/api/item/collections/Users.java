package org.yanex.vika.api.item.collections;

import net.rim.device.api.util.Persistable;
import org.yanex.vika.api.item.User;
import org.yanex.vika.util.fun.ImmutableList;

import java.util.Vector;

public class Users extends ImmutableList implements Persistable {

  public Users() {
    super(EMPTY_VECTOR, User.class);
  }

  public Users(Vector vector) {
    super(vector, User.class);
  }

  public User get(int index) {
    return (User) getObject(index);
  }

}
