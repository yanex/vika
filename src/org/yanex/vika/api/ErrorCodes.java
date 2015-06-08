package org.yanex.vika.api;

public interface ErrorCodes {

    int FILE_SENDING_ERROR = -5;
    int UNAUTHORIZED = -4;
    int NO_NETWORK = -3;
    int NETWORK_ERROR = -1;
    int JSON_ERROR = -2;

    int UNKNOWN_ERROR = 1;
    int TOKEN_REVOKED = 5;
    int INTERNAL_SERVER_ERROR = 10;

    int CAPTCHA_NEEDED = 14;

    int PARAMETER_MISSING_OR_INVALID = 100;
    int PHONE_ALREADY_USED = 1004;

    int PROCESSING_PHONE = 1112;

}
