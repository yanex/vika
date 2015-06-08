package org.yanex.vika.util.tdparty;

public class GoogleMaps {

    private static final String HTTP = "http://maps.googleapis.com/maps/api/staticmap?";

    private static final String HTTPS = "https://maps.googleapis.com/maps/api/staticmap?";
    private static final String CENTER = "center=";
    private static final String SIZE = "size=";
    private static final String SENSOR = "sensor=";
    private static final String SCALE = "scale=";
    private static final String ZOOM = "zoom=";

    private static final String MARKERS = "markers=icon:http://storage-63901-1.cs.clodoserver.ru/Location.png%7Ccolor:gray%7C";
    private static final String THUMB_SIZE = GoogleMaps.SIZE + "56x56";
    private static final String SMALL_SIZE = GoogleMaps.SIZE + "120x120";
    private static final String MEDIUM_SIZE = GoogleMaps.SIZE + "640x480";

    private static final String BIG_SIZE = GoogleMaps.SIZE + "1280x1024";
    private static final String SENSOR_FALSE = GoogleMaps.SENSOR + "false";
    private static final String SCALE_2 = GoogleMaps.SCALE + "2";

    private static final String ZOOM_9 = GoogleMaps.ZOOM + "9";

    public static String getBig(double latitude, double longitude) {
        return GoogleMaps.HTTP + GoogleMaps.CENTER + latitude + "," + longitude + "&"
                + GoogleMaps.BIG_SIZE + "&"
                + GoogleMaps.SENSOR_FALSE + "&" + GoogleMaps.ZOOM_9 + "&" + GoogleMaps.MARKERS + latitude
                + ","
                + longitude;
    }

    public static String getBigHTTPS(double latitude, double longitude) {
        return GoogleMaps.HTTPS + GoogleMaps.CENTER + latitude + "," + longitude + "&"
                + GoogleMaps.BIG_SIZE
                + "&" + GoogleMaps.SENSOR_FALSE + "&" + GoogleMaps.ZOOM_9 + "&" + GoogleMaps.MARKERS
                + latitude
                + "," + longitude;
    }

    public static String getMedium(double latitude, double longitude) {
        return GoogleMaps.HTTP + GoogleMaps.CENTER + latitude + "," + longitude + "&"
                + GoogleMaps.MEDIUM_SIZE
                + "&" + GoogleMaps.SENSOR_FALSE + "&" + GoogleMaps.ZOOM_9 + "&" + GoogleMaps.MARKERS
                + latitude
                + "," + longitude;
    }

    public static String getMediumHTTPS(double latitude, double longitude) {
        return GoogleMaps.HTTPS + GoogleMaps.CENTER + latitude + "," + longitude + "&"
                + GoogleMaps.MEDIUM_SIZE
                + "&" + GoogleMaps.SENSOR_FALSE + "&" + GoogleMaps.ZOOM_9 + "&" + GoogleMaps.MARKERS
                + latitude
                + "," + longitude;
    }

    public static String getSmall(double latitude, double longitude) {
        return GoogleMaps.HTTP + GoogleMaps.CENTER + latitude + "," + longitude + "&"
                + GoogleMaps.SMALL_SIZE
                + "&" + GoogleMaps.SCALE_2 + "&" + GoogleMaps.SENSOR_FALSE + "&" + GoogleMaps.ZOOM_9 + "&"
                + GoogleMaps.MARKERS + latitude + "," + longitude;
    }

    public static String getSmallHTTPS(double latitude, double longitude) {
        return GoogleMaps.HTTPS + GoogleMaps.CENTER + latitude + "," + longitude + "&"
                + GoogleMaps.SMALL_SIZE
                + "&" + GoogleMaps.SCALE_2 + "&" + GoogleMaps.SENSOR_FALSE + "&" + GoogleMaps.ZOOM_9 + "&"
                + GoogleMaps.MARKERS + latitude + "," + longitude;
    }

    public static String getThumb(double latitude, double longitude) {
        return GoogleMaps.HTTP + GoogleMaps.CENTER + latitude + "," + longitude + "&"
                + GoogleMaps.THUMB_SIZE
                + "&" + GoogleMaps.SCALE_2 + "&" + GoogleMaps.SENSOR_FALSE + "&" + GoogleMaps.ZOOM_9 + "&"
                + GoogleMaps.MARKERS + latitude + "," + longitude;
    }

    public static String getThumbHTTPS(double latitude, double longitude) {
        return GoogleMaps.HTTPS + GoogleMaps.CENTER + latitude + "," + longitude + "&"
                + GoogleMaps.THUMB_SIZE
                + "&" + GoogleMaps.SCALE_2 + "&" + GoogleMaps.SENSOR_FALSE + "&" + GoogleMaps.ZOOM_9 + "&"
                + GoogleMaps.MARKERS + latitude + "," + longitude;
    }

    public boolean isDirty() {
        return false;
    }

}
