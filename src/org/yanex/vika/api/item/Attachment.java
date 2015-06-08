package org.yanex.vika.api.item;

import json.JSONException;
import json.JSONObject;
import net.rim.device.api.util.Persistable;

public abstract class Attachment implements Persistable {

    private final long ownerId;

    public Attachment(long ownerId) {
        this.ownerId = ownerId;
    }

    static Attachment loadAttachment(JSONObject obj) throws JSONException {
        String type = obj.getString("type");
        JSONObject jso = obj.getJSONObject(type);

        if ("photo".equals(type)) {
            return new PhotoAttachment(jso);
        } else if ("video".equals(type)) {
            return new VideoAttachment(jso);
        } else if ("audio".equals(type)) {
            return new AudioAttachment(jso);
        } else if ("doc".equals(type)) {
            return new DocumentAttachment(jso);
        } else {
            return null;
        }
    }

    public long getOwnerId() {
        return ownerId;
    }

}
