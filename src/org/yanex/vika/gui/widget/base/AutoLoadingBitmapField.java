package org.yanex.vika.gui.widget.base;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYDimension;
import org.yanex.vika.util.network.ImageLoader;
import org.yanex.vika.util.network.ImageLoaderCallback;
import org.yanex.vika.util.network.State;

public class AutoLoadingBitmapField extends ImageField implements ImageLoaderCallback {

  private final int scaleMode, downscale;

  private String url;
  private State photoState = State.None;
  private Bitmap bitmap;

  public AutoLoadingBitmapField(XYDimension size, long style, boolean scale) {
    this(size, style, scale, Bitmap.SCALE_TO_FIT, 1);
  }

  public AutoLoadingBitmapField(XYDimension size, long style, boolean scale, int scaleMode,
                                int downscale) {
    super(null, size.width, size.height, style, scale);
    this.downscale = downscale;
    this.scaleMode = scaleMode;
  }

  // uiThread
  private void load() {
    photoState = State.Loading;
    ImageLoader.instance.load(url, null, getPreferredHeight(), getPreferredHeight(),
        true, // roundAngles
        true, // cache
        false, // cache in memory
        scaleMode, this, downscale, 3); // rounded pixels
  }

  public void onError(String url, String tag) {
    if (url.equals(this.url)) {
      photoState = State.Error;
    }
  }

  public void onLoad(String url, String tag, Bitmap bmp) {
    if (url.equals(this.url)) {
      this.bitmap = bmp;
      setBitmap(bmp);
      invalidate();
      photoState = State.Complete;
    }
  }

  protected void paint(Graphics g) {
    super.paint(g);

    if (bitmap == null && photoState == State.None) {
      load();
    }
  }

  public void setURL(String url) {
    this.url = url;
    photoState = State.None;
    load();
  }
}
