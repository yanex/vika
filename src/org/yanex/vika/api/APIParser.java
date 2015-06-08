package org.yanex.vika.api;

import json.JSONArray;
import json.JSONException;
import json.JSONObject;
import org.yanex.vika.api.item.Audio;
import org.yanex.vika.api.item.Message;
import org.yanex.vika.api.item.User;
import org.yanex.vika.api.item.collections.Audios;
import org.yanex.vika.api.item.collections.Messages;
import org.yanex.vika.api.item.collections.Users;
import org.yanex.vika.api.util.JSONUtils;
import org.yanex.vika.util.fun.Function1;
import org.yanex.vika.util.fun.Predicates;
import org.yanex.vika.util.fun.RichVector;

import java.util.Vector;

class APIParser {

    protected long parseLong(String response) throws APIException {
        try {
            return Long.parseLong(parse(response));
        } catch (NumberFormatException e) {
            throw new APIException(ErrorCodes.JSON_ERROR);
        }
    }

    protected void parseBulk(String response) throws APIException {
        if (parse(response) == null) {
            throw new APIException(ErrorCodes.JSON_ERROR);
        }
    }

    protected Users parseUsers(String response) throws APIException {
        try {
            JSONArray array = parseArray(response);
            RichVector users = JSONUtils.toList(array)
                    .transform(new Function1() {
                        public Object apply(Object it) {
                            return User.opt((JSONObject) it);
                        }
                    })
                    .filter(Predicates.notNull);

            return new Users(users);
        } catch (JSONException e) {
            throw new APIException(e);
        }
    }

    protected Audios parseAudios(String response) throws APIException {
        try {
            JSONArray a = parseArray(response);
            Vector ret = new Vector(a.length());

            for (int i = 0; i < a.length(); ++i) {
                JSONObject obj = a.getJSONObject(i);
                ret.addElement(new Audio(obj));
            }
            return new Audios(ret);
        } catch (JSONException e) {
            throw new APIException(e);
        }
    }

    protected String parseString(String response) throws APIException {
        return parse(response);
    }

    private JSONArray parseArray(String response) throws JSONException {
        JSONObject root = new JSONObject(response);
        return parseArray(root.get("response"));
    }

    protected JSONArray parseArray(Object o) throws JSONException {
        if (o instanceof JSONArray) {
            return (JSONArray) o;
        } else if (o instanceof JSONObject) {
            JSONObject responseJObj = (JSONObject) o;
            if (responseJObj.has("items")) {
                return responseJObj.getJSONArray("items");
            }
        }
        throw new JSONException("Can't parse: " + o);
    }

    private String parse(String response) throws APIException {
        try {
            JSONObject jso = new JSONObject(response);

            if (jso.has("response")) {
                return jso.getString("response");
            } else {
                throw new APIException(jso);
            }
        } catch (JSONException e) {
            throw new APIException(e);
        }
    }

    protected Messages parseMessages(String response) throws APIException {
        try {
            JSONArray a = parseArray(response);
            Vector messages = new Vector();
            for (int i = 0; i < a.length(); ++i) {
                messages.addElement(new Message(a.getJSONObject(i)));
            }
            return new Messages(messages);
        } catch (JSONException e1) {
            throw new APIException(ErrorCodes.JSON_ERROR);
        }
    }

    protected Messages parseMessages(JSONArray a) throws APIException {
        try {
            Vector messages = new Vector(a.length());
            for (int i = 0; i < a.length(); ++i) {
                messages.addElement(new Message(a.getJSONObject(i)));
            }
            return new Messages(messages);
        } catch (JSONException e1) {
            throw new APIException(ErrorCodes.JSON_ERROR);
        }
    }

}
