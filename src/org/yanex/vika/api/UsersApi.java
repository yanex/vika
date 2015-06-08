package org.yanex.vika.api;

import org.yanex.vika.api.http.Arguments;
import org.yanex.vika.api.item.collections.Users;
import org.yanex.vika.api.util.CaptchaInfo;

public class UsersApi extends APIParser {

    private final Api api;

    UsersApi(Api api) {
        this.api = api;
    }

    public Users get(CaptchaInfo captcha, long uid) throws APIException {
        return get(captcha, Long.toString(uid));
    }

    public Users get(CaptchaInfo captcha, String uids) throws APIException {
        return parseUsers(api.process(captcha, "users.get", Arguments.make()
                        .put("uids", uids)
                        .put("fields", VkApi.PROFILE_FIELDS)
        ));
    }

    public Users search(CaptchaInfo captcha, String q, int count, int offset)
            throws APIException {
        return parseUsers(api.process(captcha, "users.get", Arguments.make()
                        .put("q", q)
                        .put("fields", VkApi.PROFILE_FIELDS)
                        .putIf(offset > 0, "offset", offset)
                        .putIf(count > 0, "count", count)
        ));
    }

}
