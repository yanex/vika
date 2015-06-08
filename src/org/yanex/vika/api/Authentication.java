package org.yanex.vika.api;

import com.nokia.example.rlinks.util.URLEncoder;
import json.JSONException;
import json.JSONObject;
import org.yanex.vika.Configuration;
import org.yanex.vika.api.http.HTTPMethods;
import org.yanex.vika.util.network.Network;

public class Authentication {

    public static class Token {
        private final String token;
        private final long userId;
        private final String secret;

        public Token(String token, long userId, String secret) {
            this.token = token;
            this.userId = userId;
            this.secret = secret;
        }

        public String getToken() {
            return token;
        }

        public String getSecret() {
            return secret;
        }

        public long getUserId() {
            return userId;
        }
    }

    public static Token getToken(String username, String password) throws APIException {
        if (!Network.test()) {
            throw new APIException(ErrorCodes.NO_NETWORK);
        }

        final String apiUrl = "https://api.vk.com/oauth/token";

        final String url = apiUrl + "?grant_type=password" + "&client_id=" + Configuration.CLIENT_ID
                + "&client_secret=" + Configuration.CLIENT_SECRET + "&username=" + URLEncoder.urlEncode(username)
                + "&password=" + URLEncoder.urlEncode(password) + "&scope=" + Configuration.SCOPE;

        String response = HTTPMethods.get(url);

        if (response == null) {
            throw new APIException(ErrorCodes.NETWORK_ERROR);
        }

        try {
            JSONObject jso = new JSONObject(response);

            if (jso.has("access_token") && jso.has("user_id")) {
                String token = jso.getString("access_token");
                long userId = jso.getLong("user_id");
                return new Token(token, userId, null);
            } else {
                throw new APIException(jso);
            }
        } catch (JSONException e) {
            throw new APIException(ErrorCodes.JSON_ERROR);
        }
    }

}
