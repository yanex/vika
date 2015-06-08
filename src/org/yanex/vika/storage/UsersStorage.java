package org.yanex.vika.storage;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import org.yanex.vika.api.item.User;
import org.yanex.vika.api.item.collections.Users;
import org.yanex.vika.api.item.collections.UsersChats;
import org.yanex.vika.util.fun.Function1;
import org.yanex.vika.util.fun.Predicate;

import java.util.Hashtable;

public class UsersStorage {

    private static final long KEY = 0x35f7517cb639dd31L;

    private final PersistentObject usersPO;
    private final Hashtable users;

    public static final UsersStorage instance = new UsersStorage();

    private UsersStorage() {
        usersPO = PersistentStore.getPersistentObject(UsersStorage.KEY);
        if (usersPO.getContents() == null) {
            users = new Hashtable();
            usersPO.setContents(users);
        } else {
            Object o = usersPO.getContents();
            if (o instanceof Hashtable) {
                users = (Hashtable) o;
            } else {
                users = new Hashtable();
                usersPO.setContents(users);
            }
        }
    }

    public void clear() {
        users.clear();
        update();
    }

    public synchronized Users get(String key) {
        Object o = users.get(key);
        if (o == null) {
            return new Users();
        } else {
            Users users = (Users) o;
            return new Users(users.transform(new Function1() {
                public Object apply(Object it) {
                    User u = UserStorage.instance.get(((User) it).getId());
                    return u != null ? u : it;
                }
            }));
        }
    }

    public synchronized Users getOnline() {
        Object o = get("all");
        if (o == null) {
            return new Users();
        } else {
            return new Users(((Users) o).filter(new Predicate() {
                public boolean pred(Object it) {
                    return ((User) it).isOnline();
                }
            }));
        }
    }

    public boolean has(String key) {
        return users.get(key) != null;
    }

    public synchronized void put(String key, UsersChats value) {
        Users u = value.filterUsers();
        users.put(key, u);
        UserStorage.instance.updateUsers(u);
        update();
    }

    public synchronized void put(String key, Users value) {
        users.put(key, value);
        UserStorage.instance.updateUsers(value);
        update();
    }

    private void update() {
        usersPO.setContents(users);
        usersPO.commit();
    }
}
