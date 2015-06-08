package org.yanex.vika.api.item;

public class ActivityStatus {

    private final boolean isOnline;
    private final long lastActivity;

    public ActivityStatus(boolean isOnline, long lastActivity) {
        this.isOnline = isOnline;
        this.lastActivity = lastActivity;
    }

    public long getLastActivity() {
        return lastActivity;
    }

    public boolean isOnline() {
        return isOnline;
    }

}
