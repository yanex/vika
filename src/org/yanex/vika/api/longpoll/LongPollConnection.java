package org.yanex.vika.api.longpoll;

public class LongPollConnection implements LongPollUpdate {

    private String key;
    private String server;
    private String ts;

    public LongPollConnection(String key, String server, String ts) {
        this.key = key;
        this.server = server;
        this.ts = ts;
    }

    public String getKey() {
        return key;
    }

    public String getServer() {
        return server;
    }

    public String getTs() {
        return ts;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

}
