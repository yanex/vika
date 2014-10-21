package org.yanex.vika.api.item;

import json.JSONException;
import json.JSONObject;
import net.rim.device.api.util.Persistable;
import org.yanex.vika.util.fun.Pair;
import org.yanex.vika.util.fun.RichVector;

public class Video implements Persistable {

  private final boolean isExternal;
  private final String url_240;
  private final String url_320;
  private final String url_360;
  private final String url_480;
  private final String url_720;

  public Video(JSONObject obj) throws JSONException {
    JSONObject files = obj.getJSONObject("files");

    if (files.has("external")) {
      isExternal = true;
      url_240 = url_320 = url_360 = url_480 = url_720 =
          files.getString("external");
    } else {
      isExternal = false;
      url_240 = files.optString("mp4_240", "");
      url_320 = files.optString("mp4_320", "");
      url_360 = files.optString("mp4_360", "");
      url_480 = files.optString("mp4_480", "");
      url_720 = files.optString("mp4_720", "");
    }
  }

  public boolean isExternal() {
    return isExternal;
  }

  public String get240pUrl() {
    return url_240;
  }

  public String get320pUrl() {
    return url_320;
  }

  public String get360pUrl() {
    return url_360;
  }

  public String get480pUrl() {
    return url_480;
  }

  public String get720pUrl() {
    return url_720;
  }

  public RichVector getLinks() {
    RichVector ret = new RichVector();

    if (!isExternal) {
      if (url_240 != null && url_240.length() > 0) {
        ret.addElement(new Pair("SD (240p)", url_240));
      }
      if (url_320 != null && url_320.length() > 0) {
        ret.addElement(new Pair("SD (320p)", url_320));
      }
      if (url_360 != null && url_360.length() > 0) {
        ret.addElement(new Pair("SD (360p)", url_360));
      }
      if (url_480 != null && url_480.length() > 0) {
        ret.addElement(new Pair("SD (480p)", url_480));
      }
      if (url_720 != null && url_720.length() > 0) {
        ret.addElement(new Pair("HD (720p)", url_720));
      }
    }

    return ret;
  }
}
