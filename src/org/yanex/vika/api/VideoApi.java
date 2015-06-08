package org.yanex.vika.api;

import json.JSONArray;
import json.JSONException;
import json.JSONObject;
import org.yanex.vika.api.http.Arguments;
import org.yanex.vika.api.item.Video;
import org.yanex.vika.api.util.CaptchaInfo;

public class VideoApi {

    private final Api api;

    VideoApi(Api api) {
        this.api = api;
    }

    public Video getVideo(CaptchaInfo captcha, String vid) throws APIException {
        String response = api.process(captcha, "video.get", Arguments.make()
                        .put("videos", vid)
        );

        try {
            JSONObject jso = new JSONObject(response);
            if (jso.has("response")) {
                JSONArray arr = jso.getJSONArray("response");
                return new Video(arr.getJSONObject(1));
            } else {
                throw new APIException(jso);
            }
        } catch (JSONException e1) {
            throw new APIException(ErrorCodes.JSON_ERROR);
        }
    }

}
