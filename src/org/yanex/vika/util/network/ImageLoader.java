package org.yanex.vika.util.network;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYDimension;
import net.rim.device.api.ui.XYRect;
import org.yanex.vika.api.util.ExtendedTaskWorker;
import org.yanex.vika.gui.util.R;
import org.yanex.vika.gui.util.RoundAngles;
import org.yanex.vika.storage.ImageCacheStorage;
import org.yanex.vika.storage.OptionsStorage;
import org.yanex.vika.util.bb.DeviceMemory;
import org.yanex.vika.util.bb.FileSystemObject;

import java.util.Hashtable;
import java.util.Vector;

public class ImageLoader {

    public static ImageLoader instance = new ImageLoader();

    private ExtendedTaskWorker worker = new ExtendedTaskWorker(3, Thread.MIN_PRIORITY,
            ExtendedTaskWorker.TYPE_MINPENDING);

    static Hashtable MEMCACHE = new Hashtable();

    private ImageLoader() {

    }

    public void load(String url, String tag, XYDimension size, ImageLoaderCallback callback) {
        load(url, tag, size.width, size.height,
                true, true, true, Bitmap.SCALE_TO_FIT, callback, 1, 3);
    }

    public void load(String url, String tag, int width, int height, boolean roundAngles,
                     boolean cache,
                     boolean cacheInMemory, int scaleType, ImageLoaderCallback callback, int downscale,
                     int roundAngle) {
        if (url == null) {
            if (callback != null) {
                callback.onError(url, tag);
            }
        }
        worker.addTask(new Loader(url, tag, width, height, roundAngles, cache, cacheInMemory,
                scaleType,
                callback, downscale, roundAngle));
    }

    public void load(String url, String tag, int width, int height, boolean roundAngles,
                     boolean cache,
                     ImageLoaderCallback callback) {
        load(url, tag, width, height, roundAngles, cache, Bitmap.SCALE_TO_FIT, callback);
    }

    public void load(String url, String tag, int width, int height, boolean roundAngles,
                     boolean cache,
                     int scaleType, ImageLoaderCallback callback) {
        load(url, tag, width, height, roundAngles, cache, false, scaleType, callback, 1, 3);
    }

    public void load(final String[] url, String tag,
                     final XYDimension size, final SeveralImageLoaderCallback callback) {
        load(url, tag, size.width, size.height, true, callback, true);
    }

    public void load(final String[] url, final String tag, final int width, final int height,
                     final boolean roundAngles, final SeveralImageLoaderCallback callback,
                     final boolean cacheInMemory) {
        final int len = Math.min(url.length, 4);

        final Bitmap[] bitmaps = new Bitmap[len];
        final XYRect[] rects = new XYRect[len];

        final int margin = R.px(1) / 2;
        if (len == 4) {
            int h = (height - margin) / 2;
            int w = (width - margin) / 2;
            rects[0] = new XYRect(0, 0, w, h);
            rects[1] = new XYRect(width - w, 0, w, h);
            rects[2] = new XYRect(0, height - h, w, h);
            rects[3] = new XYRect(width - w, height - h, w, h);
        } else if (len == 3) {
            int h = (height - margin) / 2;
            int w = (width - margin) / 2;
            rects[0] = new XYRect(0, 0, w, height);
            rects[1] = new XYRect(width - w, 0, w, h);
            rects[2] = new XYRect(width - h, height - h, w, h);
        } else if (len == 2) {
            int w = (width - margin) / 2;
            rects[0] = new XYRect(0, 0, w, height);
            rects[1] = new XYRect(width - w, 0, w, height);
        } else if (len == 1) {
            rects[0] = new XYRect(0, 0, width, height);
        }

        ImageLoaderCallback cb = new ImageLoaderCallback() {

            private int count = 0;

            private void checkCount() {
                if (count == len) {
                    boolean isError = false;

                    int i;

                    for (i = 0; i < bitmaps.length; ++i) {
                        if (bitmaps[i] == null) {
                            isError = true;
                            break;
                        }
                    }

                    if (isError) {
                        callback.onError(url, tag);
                    } else {
                        Bitmap bmp = new Bitmap(width, height);
                        Graphics g = Graphics.create(bmp);
                        for (i = 0; i < bitmaps.length; ++i) {
                            g.drawBitmap(rects[i], bitmaps[i], 0, 0);
                        }
                        if (roundAngles) {
                            RoundAngles.roundAngles(bmp, 3);
                        }
                        callback.onLoad(url, tag, bmp);
                    }
                }
            }

            public void onError(String url, String tag) {
                count++;
                checkCount();
            }

            public void onLoad(String _url, String tag, Bitmap bmp) {
                count++;

                for (int i = 0; i < len; ++i) {
                    if (url[i].equals(_url) && rects[i].width == width && rects[i].height == height) {
                        bitmaps[i] = bmp;
                        break;
                    }
                }

                checkCount();
            }
        };

        for (int i = 0; i < len; ++i) {
            load(url[i], tag, rects[i].width, rects[i].height, false, true, Bitmap.SCALE_TO_FILL, cb);
        }
    }

    static void clearCache() {
        String lc = OptionsStorage.instance.getString("imageloader_lastClear", null);
        if (lc != null) {
            long l = Long.parseLong(lc);
            if (System.currentTimeMillis() < l + 1000 * 1800) {
                return;
            }
            if (DeviceMemory.getDirectorySize(DeviceMemory.getCacheDir()) < 1000 * 1000 * 10) {
                return;
            }
        }

        OptionsStorage.instance.set("imageloader_lastClear", System.currentTimeMillis() + "");

        FileSystemObject fso = DeviceMemory.getCacheFSO();
        Vector files = DeviceMemory.listDirectory(fso, null);

        for (int i = 0; i < files.size(); ++i) {
            FileSystemObject fsoFile = (FileSystemObject) files.elementAt(i);
            String fn = fsoFile.name;
            String filename = fn;
            if (fn.startsWith("imgcache_")) {
                fn = fn.substring("imgcache_".length());
                int a = fn.indexOf('_');
                fn = fn.substring(a + 1);
                a = fn.indexOf('_');
                fn = fn.substring(0, a);

                try {
                    long l = Long.parseLong(fn);
                    if (System.currentTimeMillis() > l + 1000 * 3600 * 24) {
                        ImageCacheStorage.instance.findAndDelete(filename);
                        DeviceMemory.delete(fsoFile.where + fsoFile.name);
                    }
                } catch (Exception e) {
                }
            }
        }
    }
}
