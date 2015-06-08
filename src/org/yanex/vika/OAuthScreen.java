package org.yanex.vika;

import net.rim.device.api.browser.field.ContentReadEvent;
import net.rim.device.api.browser.field2.BrowserField;
import net.rim.device.api.browser.field2.BrowserFieldConfig;
import net.rim.device.api.browser.field2.BrowserFieldListener;
import net.rim.device.api.browser.field2.debug.BrowserFieldDebugger;
import net.rim.device.api.io.transport.ConnectionFactory;
import net.rim.device.api.io.transport.TransportInfo;
import net.rim.device.api.script.ScriptEngine;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;

import org.w3c.dom.Document;
import org.yanex.vika.api.Authentication;
import org.yanex.vika.gui.screen.VkMainScreen;
import org.yanex.vika.gui.util.Fonts;
import org.yanex.vika.storage.OptionsStorage;
import org.yanex.vika.util.StringUtils;
import org.yanex.vika.util.fun.Action1;
import org.yanex.vika.util.fun.Array;
import org.yanex.vika.util.fun.Predicate;
import org.yanex.vika.util.fun.Predicates;

public class OAuthScreen extends VkMainScreen {

  private final static long STYLE = Manager.HORIZONTAL_SCROLL | Manager.VERTICAL_SCROLL |
      Manager.HORIZONTAL_SCROLLBAR | Manager.VERTICAL_SCROLLBAR;

  public OAuthScreen() {
    super(STYLE);

    setFont(Fonts.defaultFont);
    setTitle((Field) null);

    BrowserFieldConfig mConfig = new BrowserFieldConfig();
    mConfig.setProperty(BrowserFieldConfig.ALLOW_CS_XHR, Boolean.TRUE);
    mConfig.setProperty(BrowserFieldConfig.JAVASCRIPT_ENABLED, Boolean.TRUE);               
    mConfig.setProperty(BrowserFieldConfig.USER_SCALABLE, Boolean.TRUE);
    mConfig.setProperty(BrowserFieldConfig.NAVIGATION_MODE, BrowserFieldConfig.NAVIGATION_MODE_POINTER);
    mConfig.setProperty(BrowserFieldConfig.VIEWPORT_WIDTH, new Integer(Display.getWidth()));

    mConfig.setProperty(BrowserFieldConfig.CONNECTION_FACTORY, createConnectionFactory());
    
    BrowserField browser = new BrowserField(mConfig);
    add(browser);

    String oauthUrl = "https://oauth.vk.com/authorize?" +
        "client_id="+ Configuration.CLIENT_ID + "&" +
        "scope=" + Configuration.SCOPE + "&" +
        "redirect_uri=" + Configuration.REDIRECT_URL + "&" +
        "display=wap&" +
        "v=" + Configuration.API_VERSION + "&" +
        "response_type=token"; // + (DeviceInfo.isSimulator() ? ";deviceside=true" : "");

    final String accessTokenParamName = "access_token";
    final String userIdParamName = "user_id";
    final String secretParamName = "secret";

    browser.addListener(new BrowserFieldListener() {

      public void documentLoaded(BrowserField browserField, Document document) throws Exception {
        String url = document.getDocumentURI();
        if (url.startsWith(Configuration.REDIRECT_URL)) {
          Array params = new Array(StringUtils.split(url.substring(Configuration.REDIRECT_URL.length() + 1), "&"));

          String accessTokenParam = (String) params.firstOrNull(Predicates.startsWith(accessTokenParamName));
          String userIdParam = (String) params.firstOrNull(Predicates.startsWith(userIdParamName));
          String secretParam = (String) params.firstOrNull(Predicates.startsWith(secretParamName));

          if (accessTokenParam != null && userIdParam != null) {
            String accessToken = accessTokenParam.substring(accessTokenParamName.length() + 1);
		            String userId = userIdParam.substring(userIdParamName.length() + 1);
		            String secret = secretParam.substring(secretParamName.length() + 1);
		            accessTokenReceived(accessToken, userId, secret);
		          }
	        }
		}
    	
	});

  browser.requestContent(oauthUrl);

    /*browser.setDebugger(new BrowserFieldDebugger() {

      public void pageCompletedLoading(String url) {
        

        super.pageCompletedLoading(url);
      }
    });*/
  }

  private ConnectionFactory createConnectionFactory() {
    ConnectionFactory factory = new ConnectionFactory();

    factory.setPreferredTransportTypes(new int[]{
        TransportInfo.TRANSPORT_TCP_WIFI,
        TransportInfo.TRANSPORT_TCP_CELLULAR,
        TransportInfo.TRANSPORT_MDS,
        TransportInfo.TRANSPORT_WAP2
    });

    factory.setDisallowedTransportTypes(new int[]{
        TransportInfo.TRANSPORT_WAP,
        TransportInfo.TRANSPORT_BIS_B
    });

    factory.setAttemptsLimit(3);

    factory.setTimeLimit(10000);
    factory.setConnectionTimeout(10000);
    
    return factory;
  }

  public boolean isDirty() {
    return false;
  }

  private void accessTokenReceived(String accessToken, String userId, String secret) {
    OptionsStorage.instance.set("account.access_token", accessToken);
    OptionsStorage.instance.set("account.user_id", userId);
    OptionsStorage.instance.set("account.secret", secret);
    
    Vika.createAPI(accessToken, Long.parseLong(userId), secret);

    Vika.api().longpoll.start();
    new RootScreen().show();
    close();
  }
}
