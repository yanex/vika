package org.yanex.vika.util.network;

import net.rim.device.api.system.CoverageInfo;
import net.rim.device.api.system.RadioInfo;

public class Network {

  private static boolean hasSignal() {
    if (RadioInfo.getState() == RadioInfo.STATE_OFF
        || RadioInfo.getSignalLevel() == RadioInfo.LEVEL_NO_COVERAGE) {
      return false;
    } else {
      return true;
    }
  }

  private static boolean hasWifi() {
    if ((RadioInfo.getActiveWAFs() & RadioInfo.WAF_WLAN) != 0) {
      return CoverageInfo.isCoverageSufficient(CoverageInfo.COVERAGE_DIRECT,
          RadioInfo.WAF_WLAN, true);
    } else {
      return false;
    }
  }

  public static boolean test() {
    return RadioInfo.isDataServiceOperational() || Network.hasWifi() || Network.hasSignal();
  }

}
