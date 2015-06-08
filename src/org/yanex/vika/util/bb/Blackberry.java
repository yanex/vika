package org.yanex.vika.util.bb;

import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.browser.BrowserSession;
import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.PhoneArguments;

public class Blackberry {

    public static boolean launch(String url) {
        if (url == null || url.length() == 0) {
            return false;
        }

        try {
            BrowserSession browserSession = Browser.getDefaultSession();
            browserSession.displayPage(url);
            browserSession.showBrowser();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
