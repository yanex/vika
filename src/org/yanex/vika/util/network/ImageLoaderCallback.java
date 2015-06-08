package org.yanex.vika.util.network;

import net.rim.device.api.system.Bitmap;

public interface ImageLoaderCallback {
    public void onError(String url, String tag);

    public void onLoad(String url, String tag, Bitmap bmp);
}