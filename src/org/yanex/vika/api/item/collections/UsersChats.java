package org.yanex.vika.api.item.collections;

import org.yanex.vika.api.item.Chat;
import org.yanex.vika.api.item.User;
import org.yanex.vika.util.fun.ImmutableList;

import java.util.Vector;

public class UsersChats extends ImmutableList {

    private final long allCount;

    public UsersChats(long allCount, Vector users) {
        super(users, new Class[]{User.class, Chat.class});
        this.allCount = allCount;
    }

    public UsersChats(Vector users) {
        super(users, new Class[]{User.class, Chat.class});
        allCount = users.size();
    }

    public UsersChats add(int index, Object element) {
        Vector newVector = copy();
        newVector.insertElementAt(element, index);
        return new UsersChats(allCount + 1, newVector);
    }

    public UsersChats add(Object element) {
        Vector newVector = copy();
        newVector.addElement(element);
        return new UsersChats(allCount + 1, newVector);
    }

    public Users filterUsers() {
        return new Users(filter(User.class));
    }

    public Chat getChat(int index) {
        return (Chat) getObject(index);
    }

    public User getUser(int index) {
        return (User) getObject(index);
    }

}
