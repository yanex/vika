package org.yanex.vika.api.item;

import json.JSONArray;
import json.JSONException;
import net.rim.device.api.util.LongHashtable;

public final class UserUtils {

    public static LongHashtable jsonToHashtable(JSONArray usersJson) throws JSONException {
        LongHashtable u = new LongHashtable();
        for (int i = 0; i < usersJson.length(); ++i) {
            User user = new User(usersJson.getJSONObject(i));
            u.put(user.getId(), user);
        }
        return u;
    }

    private UserUtils() {

    }

}
