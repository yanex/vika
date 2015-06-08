package org.yanex.vika.api;

import json.JSONException;
import json.JSONObject;
import org.yanex.vika.Configuration;
import org.yanex.vika.api.http.Arguments;
import org.yanex.vika.api.util.CaptchaInfo;

public class AuthAPI extends APIParser {

    private Api api;
    public static final AuthAPI instance = new AuthAPI();

    AuthAPI() {
        this.api = new VkApi(null);
    }

    public long checkPhone(CaptchaInfo captcha, String phone) throws APIException {
        return parseLong(api.process(captcha, "auth.checkPhone", Arguments.make()
                .put("phone", phone)
                .put("client_id", Configuration.CLIENT_ID)
                .put("client_secret", Configuration.CLIENT_SECRET), Protocol.HTTPS, false));
    }

    public String confirm(CaptchaInfo captcha, String phone, String code, String _password)
            throws APIException {
        return api.process(captcha, "auth.confirm", Arguments.make()
                .put("client_id", Configuration.CLIENT_ID)
                .put("client_secret", Configuration.CLIENT_SECRET)
                .put("phone", phone)
                .put("code", code)
                .put("password", _password), Protocol.HTTPS, false);
    }

    public String signup(CaptchaInfo captcha, String phone, String firstName, String lastName,
                         String _password, String _sid) throws APIException {
        Arguments args = Arguments.make()
                .put("first_name", firstName)
                .put("last_name", lastName)
                .put("client_id", Configuration.CLIENT_ID)
                .put("client_secret", Configuration.CLIENT_SECRET)
                .put("phone", phone)
                .put("password", _password)
                .put("sid", _sid);

        String response = api.process(captcha, "auth.signup", args, Protocol.HTTPS, false);
        try {
            JSONObject jso = new JSONObject(response);

            if (jso.has("response")) {
                JSONObject o = jso.getJSONObject("response");
                return o.getString("sid");
            } else {
                throw new APIException(jso);
            }
        } catch (JSONException e) {
            throw new APIException(ErrorCodes.JSON_ERROR);
        } catch (NumberFormatException e1) {
            throw new APIException(ErrorCodes.JSON_ERROR);
        }
    }
}
