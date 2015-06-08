package org.yanex.vika.api;

import org.yanex.vika.api.http.Arguments;
import org.yanex.vika.api.item.collections.Audios;
import org.yanex.vika.api.util.CaptchaInfo;

public class AudioAPI extends APIParser {

    private final Api api;

    AudioAPI(Api api) {
        this.api = api;
    }

    public Audios get(CaptchaInfo captcha, String ownerId) throws APIException {
        return parseAudios(api.process(captcha, "audio.get", Arguments.make()
                        .putIf(ownerId != null && ((String) ownerId).length() > 0, "owner_id", null)
        ));
    }

}
