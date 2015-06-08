package org.yanex.vika.gui.dialog;

import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import org.yanex.vika.gui.util.GuiItem;
import org.yanex.vika.local.Local;

public abstract class VkScreen extends Screen implements GuiItem {

    public VkScreen(Manager manager) {
        super(manager);
    }

    public static String tr(int key) {
        return Local.tr(key);
    }

}
