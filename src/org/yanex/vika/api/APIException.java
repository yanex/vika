package org.yanex.vika.api;

import json.JSONException;
import json.JSONObject;

public class APIException extends Exception {

  private int errorCode;
  private String captchaSid;
  private String captchaImg;

  public APIException() {
    this.errorCode = ErrorCodes.UNKNOWN_ERROR;
  }

  public APIException(APIException e) {
    super(e.toString());
    this.errorCode = e.getErrorCode();
    this.captchaImg = e.getCaptchaImg();
    this.captchaSid = e.getCaptchaSid();
  }

  public APIException(int errorCode) {
    this.errorCode = errorCode;
  }

  public APIException(JSONException e) {
    super(e.toString());
    this.errorCode = ErrorCodes.JSON_ERROR;
  }

  public APIException(JSONObject jso) {
    try {
      if (jso.has("error")) {
        JSONObject a = jso.getJSONObject("error");

        errorCode = a.optInt("error_code", ErrorCodes.UNKNOWN_ERROR);

        if (errorCode == ErrorCodes.CAPTCHA_NEEDED) {
          captchaSid = a.getString("captcha_sid");
          captchaImg = a.getString("captcha_img");
        }
      } else {
        this.errorCode = ErrorCodes.JSON_ERROR;
      }
    } catch (JSONException e) {
      this.errorCode = ErrorCodes.JSON_ERROR;
    }
  }

  public String getCaptchaImg() {
    return captchaImg;
  }

  public String getCaptchaSid() {
    return captchaSid;
  }

  public int getErrorCode() {
    return errorCode;
  }

  public String getMessage() {
    return toString();
  }

  public String toString() {
    return "APIException{" +
        "errorCode=" + errorCode +
        '}';
  }
}
