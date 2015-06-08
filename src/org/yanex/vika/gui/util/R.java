package org.yanex.vika.gui.util;

import com.mobiata.bb.ui.decor.NinePatchBitmap;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Ui;
import org.yanex.vika.api.util.TaskWorker;

import java.lang.ref.WeakReference;
import java.util.Hashtable;

public class R {

    public static final R instance = new R();

    private final Hashtable bitmaps = new Hashtable(50);
    private final Hashtable ninepatches = new Hashtable(50);
    private TaskWorker ninepatchRenderer;

    public static int px(int pt) {
        return Ui.convertSize(pt, Ui.UNITS_pt, Ui.UNITS_px);
    }

    public Bitmap getBitmap(String resource) {

        Object _ref = bitmaps.get(resource);
        if (_ref != null) {
            WeakReference ref = (WeakReference) _ref;
            Object _bmp = ref.get();
            if (_bmp != null) {
                return (Bitmap) _bmp;
            }
        }

        Bitmap bmp = Bitmap.getBitmapResource(resource);
        WeakReference ref = new WeakReference(bmp);
        bitmaps.put(resource, ref);
        return bmp;
    }

    public NinePatchBitmap getNinepatch(String resource) {

        Object _ref = ninepatches.get(resource);
        if (_ref != null) {
            WeakReference ref = (WeakReference) _ref;
            Object _npbmp = ref.get();
            if (_npbmp != null) {
                return (NinePatchBitmap) _npbmp;
            }
        }

        Bitmap bmp = getBitmap(resource);
        NinePatchBitmap npbmp = new NinePatchBitmap(bmp);
        WeakReference ref = new WeakReference(npbmp);
        ninepatches.put(resource, ref);
        return npbmp;
    }

    public void prerenderNinepatch(NinePatchBitmap npbmp, int width, int height) {
        if (ninepatchRenderer == null) {
            synchronized (this) {
                if (ninepatchRenderer == null) {
                    ninepatchRenderer = new TaskWorker();
                }
            }
        }
        ninepatchRenderer.addTask(new NinepatchRenderTask(npbmp, width, height));
    }

    private static class NinepatchRenderTask implements Runnable {
        private final NinePatchBitmap npbmp;
        private final int width, height;

        public NinepatchRenderTask(NinePatchBitmap npbmp, int width, int height) {
            this.npbmp = npbmp;
            this.width = width;
            this.height = height;
        }

        public void run() {
            npbmp.prerender(width, height);
        }
    }

}
