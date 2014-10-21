package org.yanex.vika.api.item;

import json.JSONException;
import json.JSONObject;
import net.rim.device.api.util.Persistable;

public class AudioAttachment extends Attachment implements Persistable {

  private final long id;
  private final String title;
  private final String performer;
  private final int duration;
  private final String url;

  public AudioAttachment(JSONObject obj) throws JSONException {
    super(obj.getLong("owner_id"));
    id = obj.getLong("id");
    title = obj.getString("title");
    performer = obj.getString("artist");
    duration = obj.getInt("duration");
    url = obj.getString("url");
  }

  public long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getPerformer() {
    return performer;
  }

  public long getDuration() {
    return duration;
  }

  public String getUrl() {
    return url;
  }

}
