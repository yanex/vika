package org.yanex.vika.api.item;

import json.JSONException;
import json.JSONObject;
import net.rim.device.api.util.Persistable;

public class User implements Persistable {

    private final long id;
    private final String firstName;
    private final String lastName;
    private final String photoURL;
    private final boolean online;
    private final long lastSeen;
    private final boolean female;

    public User(JSONObject obj) throws JSONException {
        id = obj.getLong("id");
        firstName = obj.getString("first_name");
        lastName = obj.getString("last_name");
        photoURL = obj.optString("photo_50", "");
        online = obj.getString("online").equals("1");
        JSONObject lastSeenObj = obj.optJSONObject("last_seen");
        lastSeen = (lastSeenObj == null) ? 0 : lastSeenObj.optLong("time");
        female = obj.optInt("sex", 0) == 2;
    }

    public User(long id, String firstName, String lastName, String photoURL, boolean online,
                long lastSeen, boolean male) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.photoURL = photoURL;
        this.online = online;
        this.lastSeen = lastSeen;
        this.female = male;
    }

    public static User opt(JSONObject obj) {
        try {
            return new User(obj);
        } catch (JSONException e) {
            return null;
        }
    }

    public User(User u) {
        id = u.getId();
        firstName = u.getFirstName();
        lastName = u.getLastName();
        photoURL = u.getPhotoURL();
        online = u.isOnline();
        lastSeen = u.getLastSeen();
        female = u.isMale();
    }

    public User(User u, boolean isOnline, long lastSeen) {
        id = u.getId();
        firstName = u.getFirstName();
        lastName = u.getLastName();
        photoURL = u.getPhotoURL();
        online = isOnline;
        this.lastSeen = lastSeen <= 0 ? u.lastSeen : lastSeen;
        female = u.isMale();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public long getId() {
        return id;
    }

    public boolean isMale() {
        return female;
    }

    public boolean isOnline() {
        return online;
    }

    public String toString() {
        return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", photoURL="
                + photoURL + ", online=" + online + ", lastSeen=" + lastSeen + ", female=" + female + "]";
    }

}
