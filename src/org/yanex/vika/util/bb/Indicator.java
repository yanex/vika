package org.yanex.vika.util.bb;

import net.rim.blackberry.api.messagelist.ApplicationIcon;
import net.rim.blackberry.api.messagelist.ApplicationIndicator;
import net.rim.blackberry.api.messagelist.ApplicationIndicatorRegistry;
import net.rim.device.api.system.EncodedImage;

public class Indicator {

    public static final Indicator instance = new Indicator();

    public Indicator() {
        register();
    }

    private ApplicationIndicator getIndicator() {
        try {
            ApplicationIndicatorRegistry reg = ApplicationIndicatorRegistry.getInstance();
            ApplicationIndicator indicator = reg.getApplicationIndicator();
            if (indicator == null) {
                indicator = register();
            }
            return indicator;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void show() {
        try {
            getIndicator().setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hide() {
        try {
            getIndicator().setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void incValue() {
        try {
            setValue(getIndicator().getValue() + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setValue(int value) {
        try {
            ApplicationIndicator indicator = getIndicator();
            indicator.setValue(value);
            if (value > 0) {
                if (!indicator.isVisible()) {
                    indicator.setVisible(true);
                }
            } else {
                if (indicator.isVisible()) {
                    indicator.setVisible(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unregister() {
        try {
            ApplicationIndicatorRegistry reg = ApplicationIndicatorRegistry.getInstance();
            reg.unregister();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ApplicationIndicator register() {
        try {
            ApplicationIndicatorRegistry reg = ApplicationIndicatorRegistry.getInstance();
            ApplicationIndicator indicator = reg.getApplicationIndicator();
            if (indicator == null) {
                EncodedImage mImage = EncodedImage.getEncodedImageResource("img/indicator.png");
                ApplicationIcon icon = new ApplicationIcon(mImage);
                indicator = reg.register(icon, false, true);
                indicator.setValue(0);
                indicator.setVisible(false);
            }
            return indicator;
        } catch (Exception e) {
            return null;
        }
    }

}
