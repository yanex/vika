package org.yanex.vika.api;

import org.yanex.vika.api.http.Arguments;
import org.yanex.vika.api.util.CaptchaInfo;

public class AccountAPI extends APIParser {

    private final Api api;

    AccountAPI(Api api) {
        this.api = api;
    }

    public void online(CaptchaInfo captcha) throws APIException {
        parseBulk(api.process(captcha, "account.setOnline", Arguments.with("voip", 0)));
    }
}
