package org.yanex.vika.api;

import json.JSONException;
import json.JSONObject;

public class APIException extends Exception {

    private final int errorCode;
    private final String captchaSid;
    private final String captchaImg;

    public APIException(APIException e) {
        super(e.toString());
        this.errorCode = e.getErrorCode();
        this.captchaImg = e.getCaptchaImg();
        this.captchaSid = e.getCaptchaSid();
    }

    public APIException(int errorCode) {
        this.errorCode = errorCode;
        captchaImg = null;
        captchaSid = null;
    }

    public APIException(JSONException e) {
        super(e.toString());
        errorCode = ErrorCodes.JSON_ERROR;
        captchaImg = null;
        captchaSid = null;
    }

    public APIException(JSONObject jso) {
        String captchaSid = null;
        String captchaImg = null;
        int errorCode;

        try {
            if (jso.has("error")) {
                JSONObject a = jso.getJSONObject("error");

                errorCode = a.optInt("error_code", ErrorCodes.UNKNOWN_ERROR);

                if (errorCode == ErrorCodes.CAPTCHA_NEEDED) {
                    captchaSid = a.getString("captcha_sid");
                    captchaImg = a.getString("captcha_img");
                }
            } else {
                errorCode = ErrorCodes.JSON_ERROR;
            }
        } catch (JSONException e) {
            errorCode = ErrorCodes.JSON_ERROR;
        }

        this.errorCode = errorCode;
        this.captchaImg = captchaImg;
        this.captchaSid = captchaSid;
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
