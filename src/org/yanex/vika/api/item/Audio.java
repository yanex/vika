package org.yanex.vika.api.item;

import json.JSONException;
import json.JSONObject;
import net.rim.device.api.util.Persistable;

public class Audio implements Persistable {

    private final long id;
    private final String title;
    private final String artist;
    private final long ownerId;
    private final int duration;
    private final String url;

    public Audio(JSONObject obj) throws JSONException {
        id = obj.getLong("id");
        title = obj.getString("title");
        artist = obj.getString("artist");
        ownerId = obj.getLong("owner_id");
        duration = obj.getInt("duration");
        url = obj.getString("url");
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public int getDuration() {
        return duration;
    }

    public String getUrl() {
        return url;
    }

}
