package org.yanex.vika.api;

import org.yanex.vika.Configuration;
import org.yanex.vika.Vika;
import org.yanex.vika.api.Authentication.Token;
import org.yanex.vika.api.http.Arguments;
import org.yanex.vika.api.http.Arguments.ArgumentPair;
import org.yanex.vika.api.http.HTTPMethods;
import com.nokia.example.rlinks.util.URLEncoder;
import org.yanex.vika.api.util.CaptchaInfo;
import org.yanex.vika.util.network.Network;

public class VkApi extends Api {

  static final String PROFILE_FIELDS = "photo_50,online,last_seen,sex";

  private final Token token;

  public final FriendsAPI friends;
  public final UsersApi users;
  public final MessagesAPI messages;
  public final AccountAPI account;
  public final LongPoll longpoll;
  public final PhotoAPI photos;
  public final VideoApi video;
  public final AudioAPI audio;

  public VkApi(Token token) {
    this.token = token;

    friends = new FriendsAPI(this);
    users = new UsersApi(this);
    messages = new MessagesAPI(this);
    account = new AccountAPI(this);
    longpoll = new LongPoll(this);
    photos = new PhotoAPI(this);
    video = new VideoApi(this);
    audio = new AudioAPI(this);
  }

  String execute(CaptchaInfo captcha, String code) throws APIException {
    return process(captcha, "execute", Arguments.with("code", code));
  }

  public Token getToken() {
    return token;
  }

  String process(
      CaptchaInfo captcha,
      String method,
      Arguments args,
      Protocol protocol,
      boolean generateSig) throws APIException
  {
    if (!Network.test()) {
      throw new APIException(ErrorCodes.NO_NETWORK);
    }

    args.put("api_id", Configuration.CLIENT_ID);
    args.put("v", Configuration.API_VERSION);

    if (captcha != null) {
      args.put("captcha_sid", captcha.sid);
      args.put("captcha_key", captcha.code);
    }

    String postData = "";
    String encodedData = "";

    for (int i = 0; i < args.size(); ++i) {
      ArgumentPair pair = args.byId(i);
      if (postData.length() == 0) {
        postData = pair.name + "=" + pair.value;
        encodedData = pair.name + "=" + URLEncoder.urlEncode(pair.value);
      } else {
        postData += "&" + pair.name + "=" + pair.value;
        encodedData += "&" + pair.name + "=" + URLEncoder.urlEncode(pair.value);
      }
    }

    if (token != null) {
      if (postData.length() == 0) {
        postData = "access_token=" + token.getToken();
        encodedData = "access_token=" + token.getToken();
      } else {
        postData += "&" + "access_token=" + token.getToken();
        encodedData += "&" + "access_token=" + token.getToken();
      }
    }

    byte[] data = encodedData.getBytes();

    //String protocolName==Protocol.HTTP ? "http" : "https";
    String protocolName = "https";
    String url = protocolName + "://api.vk.com/method/" + method;

    if (generateSig && token != null) {
      String sig = md5("/method/" + method + "?" + postData + token.getSecret());
      url += "?sig=" + sig;
    }

    if (Configuration.DEBUG) {
      Vika.log("API query(" + url + "): " + encodedData);
    }

    String ret = HTTPMethods.post(url, data);

    if (ret == null) {
      throw new APIException(ErrorCodes.NETWORK_ERROR);
    }

    if (Configuration.DEBUG) {
      Vika.log("API answer: " + ret);
    }

    return ret;
  }
}
