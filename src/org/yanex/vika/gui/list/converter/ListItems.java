package org.yanex.vika.gui.list.converter;

import org.yanex.vika.api.item.Audio;
import org.yanex.vika.api.item.Message;
import org.yanex.vika.api.item.collections.Audios;
import org.yanex.vika.api.item.collections.Messages;
import org.yanex.vika.api.item.collections.Users;
import org.yanex.vika.gui.list.item.DialogItem;
import org.yanex.vika.gui.list.item.SongItem;
import org.yanex.vika.util.fun.Function1;
import org.yanex.vika.util.fun.Predicates;
import org.yanex.vika.util.fun.RichVector;

import java.util.Hashtable;
import java.util.Vector;

public class ListItems {
    // do not modify
    private static final Hashtable EMPTY_HASHTABLE = new Hashtable(1);

    public static RichVector users(Users userVector) {
        return UsersChatsConverter.users(userVector);
    }

    public static RichVector sortedUsers(Users users) {
        return UsersChatsConverter.sortedUsers(users);
    }

    public static Vector fileSystemObjects(RichVector items) {
        return FileSystemObjectConverter.fileSystemObjects(items);
    }

    public static RichVector itemsFromAudios(Audios audios) {
        return audios.transform(new Function1() {
            public Object apply(Object it) {
                return new SongItem((Audio) it);
            }
        });
    }

    public static RichVector messages(Messages m) {
        return ListItems.messages(m, null);
    }

    public static RichVector messages(Messages m, Hashtable previousItems) {
        if (m == null) {
            return new RichVector();
        }

        final Hashtable prev = (previousItems == null) ? EMPTY_HASHTABLE : previousItems;
        return m.transform(new Function1() {
            public Object apply(Object it) {
                Message message = (Message) it;
                DialogItem previous = (DialogItem) prev.get(message.getCode());
                if (previous != null && previous.getMessage().equals(message)) {
                    return previous;
                } else if (message.getUser() != null || message.isFromChat()) {
                    return new DialogItem(message);
                } else return null;
            }
        }).filter(Predicates.notNull);
    }

}
