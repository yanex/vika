package org.yanex.vika.local;

import net.rim.device.api.i18n.ResourceBundle;

public class Local implements VikaResource {

    private static final Local instance = new Local();

    public static String tr(int key) {
        return Local.instance.res.getString(key);
    }

    private final ResourceBundle res;

    private Local() {
        res = ResourceBundle.getBundle(VikaResource.BUNDLE_ID, VikaResource.BUNDLE_NAME);
    }

}
