package org.yanex.vika.api;

import net.rim.device.api.crypto.MD5Digest;
import org.yanex.vika.Configuration;
import org.yanex.vika.Vika;
import org.yanex.vika.api.Authentication.Token;
import org.yanex.vika.api.http.Arguments;
import org.yanex.vika.api.http.HTTPMethods;
import org.yanex.vika.api.util.CaptchaInfo;
import org.yanex.vika.util.network.Network;

public class VkApi extends Api {

  static final String PROFILE_FIELDS = "photo_50,online,last_seen,sex";

  private static char[] HEX_CHARS =
      {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

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

    String encodedData = args.toEncodedParameterString();

    if (token != null) {
      if (encodedData.length() == 0) {
        encodedData = "access_token=" + token.getToken();
      } else {
        encodedData += "&" + "access_token=" + token.getToken();
      }
    }

    byte[] data = encodedData.getBytes();

    String url = "https://api.vk.com/method/" + method;

    if (Configuration.DEBUG) {
      Vika.log("API query(" + url + "): " + encodedData);
    }

    if (generateSig && token != null && token.getSecret() != null) {
      String parameterString = args.toParameterString() + "&access_token=" + token.getToken();
      String sigData = "/method/" + method + "?" + parameterString + token.getSecret();
      String sig = md5(sigData);
      url+="?sig=" + sig;
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

  private static String byteArrayToString(byte[] bytes) {
    StringBuffer sb = new StringBuffer(bytes.length * 2);

    for (int i = 0; i < bytes.length; i++) {
      int b = bytes[i] < 0 ? bytes[i] + 256 : bytes[i];
      sb.append(HEX_CHARS[b >> 4]).append(HEX_CHARS[b & 0xF]);
    }

    return sb.toString();
  }

  private String md5(String str) {
    try {
      MD5Digest digest = new MD5Digest();
      digest.update(str.getBytes("UTF-8"));
      return byteArrayToString(digest.getDigest());
    } catch (Exception e) {
      return "";
    }
  }
}
