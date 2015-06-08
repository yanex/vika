package org.yanex.vika.api;

import json.JSONArray;
import json.JSONException;
import json.JSONObject;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.UiApplication;
import org.yanex.vika.Vika;
import org.yanex.vika.api.http.Arguments;
import org.yanex.vika.api.http.HTTPMethods;
import org.yanex.vika.api.item.*;
import org.yanex.vika.api.longpoll.*;
import org.yanex.vika.storage.OptionsStorage;
import org.yanex.vika.storage.SafeStorage;
import org.yanex.vika.util.fun.ImmutableList;
import org.yanex.vika.util.fun.RichVector;

import java.util.Hashtable;

public class LongPoll extends APIParser {

    private volatile Thread thread;
    private final Api api;

    private final RichVector listeners = new RichVector();

    LongPoll(Api api) {
        this.api = api;
        listeners.addElement(LongPollReactor.instance);
    }

    public void addListener(LongPollListener listener) {
        listeners.addElement(listener);
    }

    private void append(StringBuffer buffer, String add) {
        if (buffer.length() == 0) {
            buffer.append(add);
        } else {
            buffer.append(",").append(add);
        }
    }

    private void callListeners(final RichVector updates) {
        if (updates == null || updates.size() == 0) {
            return;
        }

        if (Application.getApplication() instanceof Vika) {
            UiApplication.getUiApplication().invokeLater(new Runnable() {

                public void run() {
                    for (int i = 0; i < listeners.size(); ++i) {
                        try {
                            ((LongPollListener) listeners.elementAt(i)).longPollUpdate(updates);
                        } catch (Exception e) {
                            // some exception on the client side, shiru ka.
                        }
                    }
                }
            });
        } else {
            try {
                LongPollReactor.instance.longPollUpdate(updates);
            } catch (Exception e) {
                // some exception, maa ii ka.
            }
        }
    }

    private RichVector getLongPollHistory(LongPollConnection connection) throws APIException {
        Arguments args = Arguments.make();

        String lastTs = SafeStorage.instance.getString("longpoll.ts", null);

        if (lastTs == null) {
            throw new APIException(ErrorCodes.INTERNAL_SERVER_ERROR);
        }

        args.put("ts", lastTs);

        String response = api.process(null, "messages.getLongPollHistory", args);

        try {
            parseBulk(response);
            RichVector ret = parseLongPoll(connection, response, true);
            if (ret == null) {
                throw new APIException(ErrorCodes.JSON_ERROR);
            } else {
                return ret;
            }
        } catch (APIException e) {
            if (e.getErrorCode() == 10) {
                return new RichVector();
            } else {
                throw new APIException(ErrorCodes.JSON_ERROR);
            }
        }
    }

    private LongPollConnection getLongPollServer() {
        String response;

        try {
            response = api.process(null, "messages.getLongPollServer",
                    Arguments.make().put("need_pts", 1));
        } catch (APIException e) {
            return null;
        }

        try {
            JSONObject jso = new JSONObject(response);
            if (jso.has("response")) {
                JSONObject o = jso.getJSONObject("response");

                String key = o.getString("key");
                String server = o.getString("server");
                String ts = o.getString("ts");

                return new LongPollConnection(key, server, ts);
            } else {
                throw new APIException(ErrorCodes.JSON_ERROR);
            }
        } catch (Exception e1) {
            return null;
        }
    }

    private RichVector loadAttachments(long messageId, JSONObject additional) {
        StringBuffer photos = new StringBuffer();
        StringBuffer videos = new StringBuffer();
        StringBuffer audio = new StringBuffer();
        StringBuffer documents = new StringBuffer();

        int attachmentsCount = 0;

        boolean hasGeo = additional.has("geo");

        int i = 1;
        while (true) {
            if (!additional.has("attach" + i + "_type")) {
                break;
            }

            try {
                String type = additional.getString("attach" + i + "_type");
                String res = additional.getString("attach" + i);

                if ("photo".equals(type)) {
                    append(photos, res);
                } else if ("video".equals(type)) {
                    append(videos, res);
                } else if ("audio".equals(type)) {
                    append(audio, res);
                } else if ("doc".equals(type)) {
                    append(documents, res);
                }

                attachmentsCount++;

            } catch (JSONException e) {
                e.printStackTrace();
            }

            ++i;
        }

        if (attachmentsCount == 0 && !hasGeo) {
            return null;
        }

        String code = "";
        if (photos.length() > 0) {
            code += "var p = API.photos.getById({\"photos\":\"" + photos.toString() + "\"}); ";
        } else {
            code += "var p = false; ";
        }
        if (videos.length() > 0) {
            code += "var v = API.video.get({\"videos\":\"" + videos.toString() + "\"}); ";
        } else {
            code += "var v = false; ";
        }
        if (audio.length() > 0) {
            code += "var a = API.audio.getById({\"audios\":\"" + audio.toString() + "\"}); ";
        } else {
            code += "var a = false; ";
        }
        if (documents.length() > 0) {
            code += "var d = API.docs.getById({\"docs\":\"" + documents.toString() + "\"}); ";
        } else {
            code += "var d = false; ";
        }

        if (hasGeo) {
            code += "var g = API.messages.getById({\"mids\":\"" + messageId + "\"})@.geo; ";
        } else {
            code += "var g = false; ";
        }

        code += "return [p, v, a, d, g];";

        String response = "";

        try {
            response = api.execute(null, code);
        } catch (APIException e) {
            if (e.getErrorCode() == ErrorCodes.NO_NETWORK || e.getErrorCode() == ErrorCodes.NETWORK_ERROR) {
                try {
                    response = api.execute(null, code);
                } catch (APIException e2) {
                    e2.printStackTrace();
                }
            }
        }

        try {
            JSONObject jso = new JSONObject(response);
            if (!jso.has("response")) {
                throw new JSONException("Invalid response.");
            }

            JSONArray arr = jso.getJSONArray("response");

            JSONArray p = null, v = null, a = null, d = null, g = null;

            try {
                p = arr.getJSONArray(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                v = arr.getJSONArray(1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                a = arr.getJSONArray(2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                d = arr.getJSONArray(3);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                g = arr.getJSONArray(4);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RichVector ret = new RichVector();

            if (p != null) {
                for (i = 0; i < p.length(); ++i) {
                    ret.addElement(new PhotoAttachment(p.getJSONObject(i)));
                }
            }
            if (v != null) {
                for (i = 1; i < v.length(); ++i) {
                    ret.addElement(new VideoAttachment(v.getJSONObject(i)));
                }
            }
            if (a != null) {
                for (i = 0; i < a.length(); ++i) {
                    ret.addElement(new AudioAttachment(a.getJSONObject(i)));
                }
            }
            if (d != null) {
                for (i = 0; i < d.length(); ++i) {
                    ret.addElement(new DocumentAttachment(d.getJSONObject(i)));
                }
            }

            if (g != null) {
                Geo geo = new Geo(g.getJSONObject(1));
                ret.addElement(new GeoAttachment(0, geo));
            }

            return ret;
        } catch (JSONException je) {
            return null;
        }
    }

    private Message loadMessage(long mid) {
        try {
            return Vika.api().messages.getByIdSingle(null, mid, null, 0).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    private RichVector longPoll(LongPollConnection connection) {
        String url = "http://" + connection.getServer() + "?act=a_check&key=" + connection.getKey()
                + "&ts=" + connection.getTs() + "&wait=25&mode=2";

        String response = null;
        try {
            response = HTTPMethods.get(url);
        } catch (APIException e) {
            e.printStackTrace();
        }
        if (response == null) {
            return null;
        }

        return parseLongPoll(connection, response, false);
    }

    private Hashtable parseMessages(JSONObject response) throws JSONException {
        JSONObject messagesObject = response.optJSONObject("messages");
        if (messagesObject == null) {
            return new Hashtable();
        }
        JSONArray messagesArray = messagesObject.optJSONArray("items");
        if (messagesArray == null) {
            return new Hashtable();
        }
        Hashtable messages = new Hashtable(messagesObject.length());

        for (int i = 1; i < messagesArray.length(); ++i) {
            Message m = new Message(messagesArray.getJSONObject(i));
            messages.put(new Long(m.getMid()), m);
        }

        return messages;
    }

    private RichVector parseLongPoll(LongPollConnection connection, String response, boolean history) {
        RichVector ret = new RichVector();
        int i;

        try {
            JSONObject jso = new JSONObject(response);

            if (jso.has("response")) {
                jso = jso.getJSONObject("response");
            }

            String ts = jso.optString("ts", null);
            if (ts != null && ts.length() > 0) {
                connection.setTs(ts);
            }

            Hashtable messages = parseMessages(jso);

            JSONArray updates = jso.getJSONArray(history ? "history" : "updates");

            for (i = 0; i < updates.length(); ++i) {
                JSONArray o = updates.getJSONArray(i);

                int code = Integer.parseInt(o.getString(0));

                LongPollUpdate update = null;

                switch (code) {
                    case 0:
                        update = new MessageDeleteUpdate(o.getString(1));
                        break;
                    case 1:
                        update = new ReplaceFlagsUpdate(o.getLong(1), o.getLong(2));
                        break;
                    case 2: {
                        long userId = 0;
                        if (o.length() >= 4) {
                            userId = Long.parseLong(o.optString(3, "0"));
                        }

                        update = new SetFlagsUpdate(o.getLong(1), o.getLong(2), userId);
                        break;
                    }
                    case 3: {
                        long userId = 0;
                        if (o.length() >= 4) {
                            userId = Long.parseLong(o.optString(3, "0"));
                        }

                        update = new DropFlagsUpdate(o.getLong(1), o.getLong(2), userId);
                        break;
                    }
                    case 4:
                        if (!history) {
                            long messageId = o.getLong(1);

                            long flags = o.getLong(2);
                            long fromId = o.getLong(3);
                            long timestamp = o.getLong(4);
                            String subject = o.getString(5);
                            String text = o.getString(6);

                            JSONObject additional = o.getJSONObject(7);
                            long from = additional.optLong("from", 0);

                            boolean handled = false;

                            RichVector attachments = null;
                            RichVector forwarded = null;
                            Geo geo = null;

                            if (additional.has("geo") || additional.has("fwd") || additional.has("attach1")) {
                                Message m = loadMessage(messageId);
                                if (m != null) {
                                    handled = true;

                                    attachments = m.getAttachments().copy();
                                    geo = m.getGeo();
                                    forwarded = m.getForwardedMessages().copy();
                                }
                            } else {
                                handled = true;
                            }

                            if (!handled) {
                                attachments = loadAttachments(messageId, additional);

                                if (attachments != null && attachments.size() > 0) {
                                    Object _geo = attachments.elementAt(attachments.size() - 1);
                                    if (_geo instanceof GeoAttachment) {
                                        geo = ((GeoAttachment) _geo).getGeo();
                                        attachments.removeElement(_geo);
                                    }
                                }
                            }

                            update = new AddMessageUpdate(messageId, flags, fromId, timestamp, subject,
                                    text, new ImmutableList(attachments, Attachment.class),
                                    new ImmutableList(forwarded, Message.class), from, geo);
                        } else {
                            long messageId = o.getLong(1);
                            long fromId = o.getLong(3);
                            Object o1 = messages.get(new Long(messageId));
                            if (o1 != null) {
                                update = new ReceivedMessageUpdate((Message) o1, fromId);
                            }
                        }

                        break;
                    case 8:
                        update = new FriendOnlineUpdate(o.getLong(1));
                        break;
                    case 9:
                        update = new FriendOfflineUpdate(o.getLong(1));
                        break;
                    case 51:
                        update = new ChatChangedUpdate(o.getLong(1), 1 == (o.getInteger(2)));
                        break;
                    case 61:
                        update = new TypingUpdate(o.getLong(1));
                        break;
                    case 62:
                        update = new ChatTypingUpdate(o.getLong(1), o.getLong(2));
                        break;
                }

                if (update != null) {
                    ret.addElement(update);
                }
            }
        } catch (Exception e) {
            return null;
        }

        return ret;
    }

    public void removeListener(LongPollListener listener) {
        listeners.removeElement(listener);
    }

    public void start() {
        if (thread != null) {
            return;
        }

        thread = new Thread() {

            public void run() {
                try {
                    LongPollConnection connection = getLongPollServer();
                    while (connection == null) {
                        connection = getLongPollServer();
                        if (connection == null) {
                            Thread.sleep(25000);
                        }
                    }

                    int errorCode = 0;
                    do {
                        try {
                            RichVector updates = getLongPollHistory(connection);
                            callListeners(updates);
                            errorCode = 0;
                        } catch (APIException e1) {
                            errorCode = e1.getErrorCode();
                            Thread.sleep(25000);
                            if (e1.getErrorCode() == ErrorCodes.TOKEN_REVOKED) {
                                interrupt();
                                return;
                            }
                        }
                    } while (errorCode < 0);

                    while (true) {
                        RichVector updates = longPoll(connection);

                        if (updates == null) {
                            RichVector updates1 = null;
                            do {
                                connection = getLongPollServer();
                                try {
                                    updates1 = getLongPollHistory(connection);
                                    callListeners(updates1);
                                } catch (APIException e) {
                                    if (e.getErrorCode() == ErrorCodes.TOKEN_REVOKED) {
                                        interrupt();
                                        return;
                                    }
                                }
                                if (connection == null || updates1 == null) {
                                    Thread.sleep(25000);
                                }
                            } while (connection == null || updates1 == null);
                        } else {
                            SafeStorage.instance.set("longpoll.ts", connection.getTs());
                            callListeners(updates);
                        }

                        String lastTime = OptionsStorage.instance.getString("last_active", "0");
                        long lt = Long.parseLong(lastTime);
                        if (System.currentTimeMillis() - lt > 1000 * 60 * 3) { // 3min
                            try {
                                int d = 0;
                                while (d < 1000 * 60 * 3) { // 3min
                                    d += 2000;
                                    Thread.sleep(2000);
                                    if (OptionsStorage.instance.getString("last_active", null) == null) {
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (InterruptedException ie) {
                    interrupt();
                }
            }

        };

        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    public void stop() {
        try {
            if (thread != null) {
                thread.interrupt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
