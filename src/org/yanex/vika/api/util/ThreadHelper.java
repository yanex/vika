package org.yanex.vika.api.util;

import net.rim.device.api.ui.UiApplication;

public abstract class ThreadHelper {

  public abstract Object task();

  public abstract void after(Object o);

  public void before() {}

  public void error() {}

  public void start() {
    before();

    new Thread() {
      public void run() {
        invokeonUIThread(task());
      }

    }.start();
  }

  private void invokeonUIThread(final Object o) {
    UiApplication.getUiApplication().invokeLater(new Runnable() {

      public void run() {
        if (o != null) {
          after(o);
        } else {
          error();
        }
      }
    });
  }

}
