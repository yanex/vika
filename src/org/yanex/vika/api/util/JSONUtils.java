package org.yanex.vika.api.util;

import json.JSONArray;
import json.JSONException;
import org.yanex.vika.util.fun.RichVector;

public final class JSONUtils {

    public static RichVector toList(JSONArray arr) throws JSONException {
        RichVector vector = new RichVector(arr.length());
        for (int i = 0; i < arr.length(); ++i) {
            vector.add(arr.get(i));
        }
        return vector;
    }

    private JSONUtils() {

    }
}
