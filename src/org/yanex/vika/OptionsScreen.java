package org.yanex.vika;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.Dialog;
import org.yanex.vika.api.http.LinkHelper;
import org.yanex.vika.gui.screen.OptionsScreenGui;
import org.yanex.vika.gui.screen.VkMainScreen;
import org.yanex.vika.local.VikaResource;
import org.yanex.vika.storage.FastStorage;
import org.yanex.vika.util.StringUtils;

class OptionsScreen extends VkMainScreen {

    private final OptionsScreenGui gui;

    public OptionsScreen() {
        gui = new OptionsScreenGui(this);
        gui.update(FastStorage.instance.getLong("disable"));
    }

    public void fieldChanged(Field field, int context) {
        if (field == gui.disableForHour) {
            long silenceUntil = System.currentTimeMillis() + 1000 * 3600;
            FastStorage.instance.set("disable", silenceUntil);
            gui.update(silenceUntil);
            gui.enableNotifications.setFocus();
        } else if (field == gui.disableFor8Hours) {
            long silenceUntil = System.currentTimeMillis() + 1000 * 3600 * 8;
            FastStorage.instance.set("disable", silenceUntil);
            gui.update(silenceUntil);
            gui.enableNotifications.setFocus();
        } else if (field == gui.enableNotifications) {
            gui.update(0);
            gui.disableForHour.setFocus();
        } else if (field == gui.logout) {
            Vika.logout();
        } else if (field == gui.exit) {
            System.exit(0);
        } else if (field == gui.about) {
            about();
        } else if (field == gui.connectionType) {
            LinkHelper.selectConnectionType();
        }
    }

    private void about() {
        String version = ApplicationDescriptor.currentApplicationDescriptor().getVersion();
        String about = StringUtils.
                replace(tr(VikaResource.AboutText), "[version]", version);
        Dialog.alert(about);
    }

}
