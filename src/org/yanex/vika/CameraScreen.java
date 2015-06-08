package org.yanex.vika;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.UiApplication;
import org.yanex.vika.api.util.ThreadHelper;
import org.yanex.vika.gui.screen.VkMainScreen;
import org.yanex.vika.local.VikaResource;
import org.yanex.vika.util.bb.DeviceMemory;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.GUIControl;
import javax.microedition.media.control.VideoControl;

class CameraScreen extends VkMainScreen {

    private static final char CR = 13, LF = 10, ESCAPE = 27;
    private VideoControl vc;
    private Player player;

    private final CameraListener listener;

    private static final String ERROR = tr(VikaResource.Camera_error);

    CameraScreen(CameraListener listener) {
        this.listener = listener;
    }

    void launch() {
        UiApplication.getUiApplication().pushScreen(this);

        try {
            player = Manager.createPlayer("capture://video");
            player.realize();
            vc = (VideoControl) player.getControl("VideoControl");
        } catch (Exception e) {
            dismiss();
            if (listener != null) {
                listener.onCameraError(CameraScreen.ERROR);
            }
            return;
        }

        Field viewFinder = (Field) vc.initDisplayMode(GUIControl.USE_GUI_PRIMITIVE, "net.rim.device.api.ui.Field");
        add(viewFinder);

        try {
            vc.setDisplayFullScreen(true);
            vc.setVisible(true);
            player.start();
        } catch (MediaException e) {
            dismiss();
            if (listener != null) {
                listener.onCameraError(CameraScreen.ERROR);
            }
            return;
        }
    }

    protected boolean keyChar(char c, int status, int time) {
        if (c == CameraScreen.ESCAPE) {
            dismiss();
            return true;
        } else if (c == CameraScreen.CR || c == CameraScreen.LF) {
            shot();
            return true;
        }

        return super.keyChar(c, status, time);
    }

    protected boolean touchEvent(TouchEvent message) {
        switch (message.getEvent()) {
            case TouchEvent.UNCLICK:
                shot();
                return true;
        }

        return super.touchEvent(message);
    }

    protected boolean invokeAction(int action) {
        boolean handled = super.invokeAction(action);

        if (!handled && action == ACTION_INVOKE) { // trackball click
            shot();
            handled = true;
        }
        return handled;
    }

    private void shot() {
        try {
            String imageType = null;
            final byte[] imageBytes = vc.getSnapshot(imageType);
            final String fn = "temp_camera_" + System.currentTimeMillis() + ".jpg";
            vc.setVisible(false);
            player.stop();
            player.close();

            new ThreadHelper() {

                public void after(Object o) {
                    dismiss();
                    if (listener != null) {
                        listener.onShot(DeviceMemory.getCacheDir() + fn);
                    }
                }

                public void error() {
                    dismiss();
                    if (listener != null) {
                        listener.onCameraError(CameraScreen.ERROR);
                    }
                }

                public Object task() {
                    return DeviceMemory.save(imageBytes, fn) ? Boolean.TRUE : null;
                }
            }.start();

        } catch (Exception e) {
            dismiss();
        }
    }

    private void dismiss() {
        UiApplication.getUiApplication().popScreen(this);
    }

    static interface CameraListener {
        public void onCameraError(String error);

        public void onShot(String filename);
    }

}
