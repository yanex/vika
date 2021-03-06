package org.yanex.vika.util.network;

import net.rim.device.api.system.CoverageInfo;
import net.rim.device.api.system.RadioInfo;

public class Network {

    private static boolean hasSignal() {
        return !(RadioInfo.getState() == RadioInfo.STATE_OFF
                || RadioInfo.getSignalLevel() == RadioInfo.LEVEL_NO_COVERAGE);
    }

    private static boolean hasWifi() {
        return (RadioInfo.getActiveWAFs() & RadioInfo.WAF_WLAN) != 0
                && CoverageInfo.isCoverageSufficient(CoverageInfo.COVERAGE_DIRECT, RadioInfo.WAF_WLAN, true);
    }

    public static boolean test() {
        return RadioInfo.isDataServiceOperational() || Network.hasWifi() || Network.hasSignal();
    }

}
