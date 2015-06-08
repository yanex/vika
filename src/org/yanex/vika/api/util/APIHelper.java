package org.yanex.vika.api.util;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import org.yanex.vika.LoginScreen;
import org.yanex.vika.Vika;
import org.yanex.vika.api.APIException;
import org.yanex.vika.api.ErrorCodes;
import org.yanex.vika.gui.dialog.CaptchaDialog;
import org.yanex.vika.gui.screen.VkMainScreen;
import org.yanex.vika.local.VikaResource;
import org.yanex.vika.storage.*;

public abstract class APIHelper {

  private static final int CAPTCHA_INPUT_ERROR = -1000;

  private static ExtendedTaskWorker worker =
      new ExtendedTaskWorker(6, Thread.MIN_PRIORITY, ExtendedTaskWorker.TYPE_MINPENDING);

  private volatile boolean interrupted = false;
  private CaptchaInfo captcha = null;

  public abstract void after(Object result);
  public void before() {}

  public CaptchaInfo captcha() {
    return captcha;
  }

  private void captcha(String sid, String img) {
    CaptchaDialog dialog = new CaptchaDialog();
    dialog.show(img);

    String code = dialog.getCode();
    if (code == null) {
      error(CAPTCHA_INPUT_ERROR);
      return;
    }
    code = code.trim();
    if (code.length() == 0) {
      error(CAPTCHA_INPUT_ERROR);
      return;
    }

    captcha = new CaptchaInfo(sid, code);
    start();
  }

  public void error(int error) {
    Vika.log("Unhandled error message in " + this.toString() + ": #" + error);
  }

  public void interrupt() {
    interrupted = true;
  }

  public void start() {
    before();

    Runnable r = new ApiTask();
    APIHelper.worker.addTask(r);
  }

  public abstract Object task() throws APIException;

  private void tokenRevoked() {
    if (Application.getApplication() instanceof Vika) {
      if (OptionsStorage.instance.getString("account.access_token", null) == null) {
        return;
      }

      Dialog.alert(VkMainScreen.tr(VikaResource.unlogin));

      Vika.api().longpoll.stop();

      MessagesStorage.instance.clear();
      UserStorage.instance.clear();
      UsersStorage.instance.clear();

      SafeStorage.instance.delete("ui_longpoll_lastts");
      SafeStorage.instance.delete("longpoll.ts");
      SafeStorage.instance.delete("longpoll.maxmid");

      OptionsStorage.instance.delete("account.access_token");
      OptionsStorage.instance.delete("account.user_id");
      OptionsStorage.instance.delete("account.secret");

      while (UiApplication.getUiApplication().getActiveScreen() != null) {
        UiApplication.getUiApplication().popScreen();
      }

      UiApplication.getUiApplication().pushScreen(Vika.createLoginScreen());
    }
  }

  private class ApiTask implements Runnable {

    public void run() {
      APIException e = null;
      Object result = null;

      try {
        result = task();
      } catch (APIException occured) {
        e = occured;
      }

      if (!interrupted) {
        UiApplication.getUiApplication().invokeLater(new AfterApiTask(result, e));
      }
    }
  }

  private class AfterApiTask implements Runnable {

    private final Object result;
    private final APIException exception;

    public AfterApiTask(Object result, APIException exception) {
      this.exception = exception;
      this.result = result;
    }

    public void run() {
      if (exception == null) {
        after(result);
      } else {
        if (exception.getErrorCode() == ErrorCodes.CAPTCHA_NEEDED) {
          captcha(exception.getCaptchaSid(), exception.getCaptchaImg());
        } else if (exception.getErrorCode() == ErrorCodes.TOKEN_REVOKED) {
          tokenRevoked();
        } else {
          error(exception.getErrorCode());
        }
      }
    }
  }
}
