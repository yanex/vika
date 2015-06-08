package org.yanex.vika.util.tdparty;

import com.nutiteq.components.WgsPoint;
import json.JSONObject;
import org.yanex.vika.Configuration;
import org.yanex.vika.api.http.HTTPMethods;

public class IPLocation {

    public static final IPLocation instance = new IPLocation();

    private static final String SERVER = "http://api.ipinfodb.com/v3/ip-city/" +
            "?format=json&key=" + Configuration.IPINFODB_KEY;

    public WgsPoint getPoint() {
        try {
            String response = HTTPMethods.get(IPLocation.SERVER);
            JSONObject jso = new JSONObject(response);
            String lat = jso.getString("latitude");
            String lon = jso.getString("longitude");

            double latitude = Double.parseDouble(lat);
            double longitude = Double.parseDouble(lon);

            return new WgsPoint(longitude, latitude);
        } catch (Exception e) {
            return null;
        }
    }

}
