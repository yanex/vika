package org.yanex.vika.api;

import org.yanex.vika.api.Authentication.Token;
import org.yanex.vika.api.http.Arguments;
import org.yanex.vika.api.util.CaptchaInfo;

public abstract class Api {

    protected Protocol protocol = Protocol.HTTP;

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
}
