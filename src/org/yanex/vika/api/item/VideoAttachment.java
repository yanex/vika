package org.yanex.vika.api.item;

import json.JSONException;
import json.JSONObject;
import net.rim.device.api.util.Persistable;

public class VideoAttachment extends Attachment implements Persistable {

    private final long id;
    private final String title;
    private final String description;
    private final long duration;
    private final String photo130;
    private final String photo320;
    private final String photo640;
    private final long date;

    public VideoAttachment(JSONObject obj) throws JSONException {
        super(obj.optLong("owner_id"));
        id = obj.optLong("id");
        title = obj.optString("title");
        description = obj.optString("description");
        duration = obj.optLong("duration");
        photo130 = obj.optString("photo_130", "");
        photo320 = obj.optString("photo_320", "");
        photo640 = obj.optString("photo_640", "");
        date = obj.optLong("date");
    }

    public long getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public long getDuration() {
        return duration;
    }

    public String getPhoto130() {
        return photo130;
    }

    public String getPhoto640() {
        return photo640;
    }

    public String getPhoto320() {
        return photo320;
    }

    public String getTitle() {
        return title;
    }

    public long getId() {
        return id;
    }
}
