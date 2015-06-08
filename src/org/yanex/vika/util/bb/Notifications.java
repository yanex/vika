package org.yanex.vika.util.bb;

import net.rim.device.api.notification.NotificationsConstants;
import net.rim.device.api.notification.NotificationsManager;

public class Notifications {

    private static Notifications instance = null;
    private static final long NOTIFY_ID = 0x2073a8e5ee8b97eaL;

    private static Object NOTIFY_OBJECT = new Object() {
        public String toString() {
            return "Vika";
        }
    };

    public synchronized static Notifications getInstance() {
        if (Notifications.instance == null) {
            Notifications.instance = new Notifications();
        }
        return Notifications.instance;
    }

    private Notifications() {
        NotificationsManager.registerSource(Notifications.NOTIFY_ID, Notifications.NOTIFY_OBJECT,
                NotificationsConstants.IMPORTANT);
    }

    public void cancel() {
        NotificationsManager.cancelImmediateEvent(Notifications.NOTIFY_ID, 0, null, null);
    }

    public void init() {

    }

    public void trigger() {
        NotificationsManager.triggerImmediateEvent(Notifications.NOTIFY_ID, 0, null, null);
    }

}
