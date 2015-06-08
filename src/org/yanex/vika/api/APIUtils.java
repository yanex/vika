package org.yanex.vika.api;

import org.yanex.vika.api.item.Message;

public final class APIUtils {

    public static boolean isFromDialog(Message m, long id) {
        if (id >= 2000000000L) {
            return m.isFromChat() && m.getChatId() == (id - 2000000000L);
        } else {
            return !m.isFromChat() && m.getUid() == id;
        }
    }

    public static String getTalkId(long id) {
        if (id >= 2000000000) {
            return "chat" + (id - 2000000000);
        } else {
            return "user" + id;
        }
    }

    public static long getChatId(long id) {
        return (id > 2000000000) ? id : id + 2000000000;
    }

    private APIUtils() {

    }

}
