package org.yanex.vika.util.network;

import com.patchou.ui.GPATools;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.JPEGEncodedImage;
import net.rim.device.api.ui.UiApplication;
import org.yanex.vika.api.http.HTTPMethods;
import org.yanex.vika.gui.util.RoundAngles;
import org.yanex.vika.storage.ImageCacheStorage;
import org.yanex.vika.util.bb.DeviceMemory;

import java.lang.ref.WeakReference;

class Loader implements Runnable {

  private final String url;
  private final String tag;
  private final int width;
  private final int height;
  private final boolean roundAngles;
  private final boolean cache;
  private final boolean cacheInMemory;
  private final ImageLoaderCallback callback;
  private final int scaleType;
  private final int downscale;
  private final int roundAngle;

  public Loader(String url, String tag, int width, int height, boolean roundAngles, boolean cache,
                boolean cacheInMemory, int scaleType, ImageLoaderCallback callback, int downscale,
                int roundAngle) {
    super();
    this.url = url;
    this.tag = tag;
    this.width = width;
    this.height = height;
    this.roundAngles = roundAngles;
    this.cache = cache;
    this.scaleType = scaleType;
    this.callback = callback;
    this.cacheInMemory = cacheInMemory;
    this.downscale = downscale;
    this.roundAngle = roundAngle;
  }

  private void complete(final Bitmap b) {
    UiApplication.getUiApplication().invokeLater(new Runnable() {

      public void run() {
        callback.onLoad(url, tag, b);
      }

    });
  }

  private void error() {
    UiApplication.getUiApplication().invokeLater(new Runnable() {

      public void run() {
        callback.onError(url, tag);
      }

    });
  }

  public void run() {
    byte[] bytes = null;

    boolean fromNetwork = false;

    final String key = url + ":" + width + "," + height;

    Bitmap b = null;
    Object _b = ImageLoader.MEMCACHE.get(key);

    if (_b != null) {
      WeakReference w = (WeakReference) _b;
      _b = w.get();
      if (_b != null) {
        b = (Bitmap) _b;
      }
    }

    if (b == null) {
      boolean localFile = url.startsWith("file://");

      String filename = null;

      if (localFile) {
        filename = url;
      } else {
        filename = ImageCacheStorage.instance.getFilename(key);
      }

      if (filename != null) {
        if (localFile) {
          bytes = DeviceMemory.read(filename);
        } else {
          bytes = DeviceMemory.readRelative(filename);
        }

        if (localFile && bytes == null) {
          error();
          return;
        }
      }

      if (bytes == null || filename == null) {
        fromNetwork = true;
        bytes = HTTPMethods.downloadFile(url);
        if (bytes == null) {
          error();
          return;
        }
      }

      try {
        b = Bitmap.createBitmapFromBytes(bytes, 0, bytes.length, downscale);
      } catch (Exception e) {
        error();
        return;
      }

      if (b == null) {
        error();
        return;
      }

      if (cacheInMemory) {
        ImageLoader.MEMCACHE.put(url + ":" + width + "," + height, new WeakReference(b));
      }
    }

    int scaleWidth = width > 0 ? width : b.getWidth();
    int scaleHeight = height > 0 ? height : b.getHeight();

    Bitmap bResized = GPATools.ResizeTransparentBitmap(b, scaleWidth, scaleHeight,
        Bitmap.FILTER_LANCZOS, scaleType);

    if (bResized == null) {
      error();
      return;
    }

    if (fromNetwork) {
      if (cache) {
        JPEGEncodedImage img = JPEGEncodedImage.encode(bResized, 90);
        bytes = img.getData();

        String filename = "imgcache_" + url.hashCode() + "_" + System.currentTimeMillis() + "_"
            + width + "_" + height;
        boolean saveResult = DeviceMemory.save(bytes, filename);
        if (saveResult) {
          ImageCacheStorage.instance.put(key, filename);
        }
      }
    }

    if (roundAngles) {
      RoundAngles.roundAngles(bResized, roundAngle);
    }

    ImageLoader.clearCache();
    complete(bResized);
  }

}