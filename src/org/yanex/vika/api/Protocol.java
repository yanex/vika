package org.yanex.vika.api;

import org.yanex.vika.util.fun.Enum;

public final class Protocol extends Enum {

  public static final Protocol
      HTTP = new Protocol("HTTP", 0),
      HTTPS = new Protocol("HTTPS", 1);

  private Protocol(String name, int num) {
    super(name, num);
  }

}
