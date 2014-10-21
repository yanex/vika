package org.yanex.vika.api.util;

public class CaptchaInfo {

  public final String sid;
  public final String code;

  public CaptchaInfo(String sid, String code) {
    this.sid = sid;
    this.code = code;
  }
}
