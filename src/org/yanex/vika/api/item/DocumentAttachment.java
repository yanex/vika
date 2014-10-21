package org.yanex.vika.api.item;

import json.JSONException;
import json.JSONObject;
import net.rim.device.api.util.Persistable;

public class DocumentAttachment extends Attachment implements Persistable {

  private final long id;
  private final String title;
  private final long size;
  private final String ext;
  private final String url;

  public DocumentAttachment(JSONObject obj) throws JSONException {
    super(obj.getLong("owner_id"));
    id = obj.getLong("id");
    title = obj.getString("title");
    size = obj.getLong("size");
    ext = obj.getString("ext");
    url = obj.getString("url");
  }

  public long getId() {
    return id;
  }

  public String getExt() {
    return ext;
  }

  public long getSize() {
    return size;
  }

  public String getTitle() {
    return title;
  }

  public String getUrl() {
    return url;
  }
}
