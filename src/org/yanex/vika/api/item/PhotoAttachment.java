package org.yanex.vika.api.item;

import json.JSONException;
import json.JSONObject;
import net.rim.device.api.util.Persistable;

public class PhotoAttachment extends Attachment implements Persistable {

  private final long id;
  private final long date;
  private final String src;
  private final String srcBig;

  public PhotoAttachment(JSONObject obj) throws JSONException {
    super(obj.optLong("owner_id"));
    id = obj.getLong("id");
    date = obj.optLong("created");
    src = obj.optString("src");
    srcBig = obj.optString("src_big");
  }

  public long getId() {
    return id;
  }

  public long getDate() {
    return date;
  }

  public String getSrc() {
    return src;
  }

  public String getSrcBig() {
    return srcBig;
  }
}
