package org.yanex.vika.api.item;

import json.JSONException;
import json.JSONObject;
import net.rim.device.api.util.Persistable;

public class Geo implements Persistable {

    private final double latitude;
    private final double longitude;
    private final String type;

    public Geo(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = "";
    }

    public Geo(JSONObject jso) throws JSONException {
        type = jso.getString("type");
        String coordinates = jso.getString("coordinates");

        int i = coordinates.indexOf(" ");
        if (i < 0) {
            throw new JSONException("Bad Geo coordinates.");
        }

        String _lat = coordinates.substring(0, i);
        String _lon = coordinates.substring(i + 1);

        try {
            latitude = Double.parseDouble(_lat);
            longitude = Double.parseDouble(_lon);
        } catch (Exception e) {
            throw new JSONException("Bad Geo coordinates.");
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getType() {
        return type;
    }
}
