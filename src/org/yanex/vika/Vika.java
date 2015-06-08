//#preprocess

package org.yanex.vika;

import net.rim.device.api.applicationcontrol.ApplicationPermissions;
import net.rim.device.api.applicationcontrol.ApplicationPermissionsManager;
import net.rim.device.api.ui.UiApplication;
import org.yanex.vika.api.Authentication.Token;
import org.yanex.vika.api.VkApi;
import org.yanex.vika.gui.screen.VkMainScreen;
import org.yanex.vika.storage.*;
import org.yanex.vika.util.bb.Indicator;
import org.yanex.vika.util.bb.Notifications;

public class Vika extends UiApplication {

    private static VkApi api;

    private static final int[] needed_permissions = {
            ApplicationPermissions.PERMISSION_INPUT_SIMULATION,
            ApplicationPermissions.PERMISSION_FILE_API,
            ApplicationPermissions.PERMISSION_ORGANIZER_DATA,
            ApplicationPermissions.PERMISSION_INTERNET,
            ApplicationPermissions.PERMISSION_LOCATION_DATA,
            ApplicationPermissions.PERMISSION_WIFI,
            ApplicationPermissions.PERMISSION_MEDIA,
            ApplicationPermissions.PERMISSION_EMAIL,
            ApplicationPermissions.PERMISSION_PHONE
    };

    public Vika() {
        checkPerm();
        Notifications.getInstance().init();
        OptionsStorage.instance.delete("last_active");

        String accessToken = OptionsStorage.instance.getString("account.access_token", null);
        String userIdString = OptionsStorage.instance.getString("account.user_id", "0");
        String secret = OptionsStorage.instance.getString("account.secret", null);

        long userId;
        try {
            userId = Long.parseLong(userIdString);
        } catch (NumberFormatException e) {
            userId = 0;
        }

        if (accessToken != null && userId > 0) {
            Vika.createAPI(accessToken, userId, secret);
            Vika.api.longpoll.start();
            pushScreen(new RootScreen());
        } else {
            pushScreen(createLoginScreen());
        }
    }

    public static void main(String[] args) {
        new Vika().enterEventDispatcher();
    }

    public static VkApi api() {
        return Vika.api;
    }

    public static VkMainScreen createLoginScreen() {
        if (Configuration.USE_DIRECT_AUTH) {
            return new LoginScreen();
        } else {
            return new OAuthScreen();
        }
    }

    static void createAPI(String accessToken, long userId, String secret) {
        Token t = new Token(accessToken, userId, secret);
        Vika.api = new VkApi(t);
    }

    static void createAPI(String accessToken, long userId) {
        Token t = new Token(accessToken, userId, null);
        Vika.api = new VkApi(t);
    }

    public static void log(String s) {
        if (Configuration.DEBUG) {
            System.err.println(s);
        }
    }

    public void activate() {
        Indicator.instance.setValue(0);
        OptionsStorage.instance.delete("last_active");
        Notifications.getInstance().cancel();
    }

    public void checkPerm() {
        ApplicationPermissionsManager apm = ApplicationPermissionsManager.getInstance();
        ApplicationPermissions original = apm.getApplicationPermissions();

        if (!permissionsPresent(original)) {
            permissionsRequest();
        }
    }

    public boolean requestClose() {
        Indicator.instance.hide();
        Indicator.instance.unregister();
        FastStorage.instance.update();
        return super.requestClose();
    }

    public static void logout() {
        api.longpoll.stop();

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

    public void deactivate() {
        OptionsStorage.instance.set("last_active", "" + System.currentTimeMillis());
    }

    private boolean permissionsPresent(ApplicationPermissions original) {
        for (int i = 0; i < needed_permissions.length; i++) {
            if (original.getPermission(needed_permissions[i]) != ApplicationPermissions.VALUE_ALLOW) {
                return false;
            }
        }
        return true;
    }

    private void permissionsRequest() {
        ApplicationPermissions permRequest = new ApplicationPermissions();
        for (int i = 0; i < needed_permissions.length; i++) {
            permRequest.addPermission(needed_permissions[i]);
        }
    }
}