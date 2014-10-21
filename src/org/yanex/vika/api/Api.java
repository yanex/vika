package org.yanex.vika.api;

import net.rim.device.api.crypto.MD5Digest;
import org.yanex.vika.api.Authentication.Token;
import org.yanex.vika.api.http.Arguments;
import org.yanex.vika.api.util.CaptchaInfo;

public abstract class Api {

  private static char[] hexChars =
      {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

  protected Protocol protocol = Protocol.HTTP;

  String md5(String str) {
    try {
      MD5Digest digest = new MD5Digest();
      digest.update(str.getBytes("UTF-8"));
      return Api.byteArrayToString(digest.getDigest());
    } catch (Exception e) {
      return "";
    }
  }

  String process(CaptchaInfo captcha, String method, Arguments args) throws APIException {
    return process(captcha, method, args, protocol, true);
  }

  abstract String process(
      CaptchaInfo captcha,
      String method,
      Arguments args,
      Protocol protocol,
      boolean generateSig) throws APIException;

  abstract String execute(CaptchaInfo captcha, String code) throws APIException;

  abstract Token getToken();

  private static String byteArrayToString(byte[] bytes) {
    StringBuffer sb = new StringBuffer(bytes.length * 2);

    for (int i = 0; i < bytes.length; i++) {
      int b = bytes[i] < 0 ? bytes[i] + 256 : bytes[i];
      sb.append(Api.hexChars[b >> 4]).append(Api.hexChars[b & 0xF]);
    }

    return sb.toString();
  }
}
