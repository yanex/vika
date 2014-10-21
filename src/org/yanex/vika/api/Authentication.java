package org.yanex.vika.api;

import json.JSONException;
import json.JSONObject;
import org.yanex.vika.Configuration;
import org.yanex.vika.api.http.HTTPMethods;
import com.nokia.example.rlinks.util.URLEncoder;
import org.yanex.vika.util.network.Network;

public class Authentication {

  public static class Token {
    private String token;
    private long userId;
    private String secret;

    public Token(String token, long userId, String secret) {
      super();
      this.token = token;
      this.userId = userId;
      this.secret = secret;
    }

    public String getSecret() {
      return secret;
    }

    public String getToken() {
      return token;
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
        String secret = jso.getString("secret");
        return new Token(token, userId, secret);
      } else {
        throw new APIException(jso);
      }
    } catch (JSONException e) {
      throw new APIException(ErrorCodes.JSON_ERROR);
    }
  }

}
