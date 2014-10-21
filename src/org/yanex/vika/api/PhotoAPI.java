package org.yanex.vika.api;

import json.JSONException;
import json.JSONObject;
import org.yanex.vika.api.http.Arguments;
import org.yanex.vika.api.http.HttpMultipartRequest;
import org.yanex.vika.api.item.PhotoAttachment;
import org.yanex.vika.api.item.PhotoUploadObject;
import org.yanex.vika.api.util.CaptchaInfo;
import org.yanex.vika.util.bb.DeviceMemory;

import java.util.Hashtable;

public class PhotoAPI {

  private final Api api;

  PhotoAPI(Api api) {
    this.api = api;
  }

  public String getMessagesUploadServer(CaptchaInfo captcha) throws APIException {
    String response = api.process(
        captcha, "photos.getMessagesUploadServer", Arguments.make());
    try {
      JSONObject jso = new JSONObject(response);
      if (jso.has("response")) {
        JSONObject o = jso.getJSONObject("response");
        return o.getString("upload_url");
      } else {
        throw new APIException(jso);
      }
    } catch (JSONException e1) {
      throw new APIException(ErrorCodes.JSON_ERROR);
    }
  }

  public PhotoAttachment saveMessagesPhoto(CaptchaInfo captcha, String server, String photo,
                                           String hash) throws APIException {
    String response = api.process(captcha, "photos.saveMessagesPhoto", Arguments.make()
        .put("server", server)
        .put("photo", photo)
        .put("hash", hash)
    );

    try {
      JSONObject jso = new JSONObject(response);
      if (jso.has("response")) {
        return new PhotoAttachment(jso.getJSONArray("response").getJSONObject(0));
      } else {
        throw new APIException(jso);
      }
    } catch (JSONException e1) {
      throw new APIException(ErrorCodes.JSON_ERROR);
    }
  }

  public PhotoUploadObject upload(String filename, String url) throws APIException {
    byte[] bytes = DeviceMemory.read(filename);

    if (bytes == null) {
      throw new APIException(ErrorCodes.FILE_SENDING_ERROR);
    }

    Hashtable params = new Hashtable();

    try {
      HttpMultipartRequest req = new HttpMultipartRequest(url, params, "photo",
          DeviceMemory.getRelativeFilename(filename), DeviceMemory.getMime(filename),
          bytes);
      byte[] response = req.send();
      String ret = new String(response);

      try {
        JSONObject jso = new JSONObject(ret);
        String server = jso.getString("server");
        String photo = jso.getString("photo");
        String hash = jso.getString("hash");

        return new PhotoUploadObject(server, photo, hash);
      } catch (JSONException e1) {
        throw new APIException(ErrorCodes.JSON_ERROR);
      }
    } catch (Exception e) {
      throw new APIException(ErrorCodes.NETWORK_ERROR);
    }
  }
}
