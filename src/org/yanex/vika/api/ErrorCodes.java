package org.yanex.vika.api;

public interface ErrorCodes {

  public static final int FILE_SENDING_ERROR = -5;
  public static final int UNAUTHORIZED = -4;
  public static final int NO_NETWORK = -3;
  public static final int NETWORK_ERROR = -1;
  public static final int JSON_ERROR = -2;

  public static final int UNKNOWN_ERROR = 1;
  public static final int TOKEN_REVOKED = 5;
  public static final int INTERNAL_SERVER_ERROR = 10;

  public static final int CAPTCHA_NEEDED = 14;

  public static final int PARAMETER_MISSING_OR_INVALID = 100;
  public static final int PHONE_ALREADY_USED = 1004;

  public static final int PROCESSING_PHONE = 1112;

}
