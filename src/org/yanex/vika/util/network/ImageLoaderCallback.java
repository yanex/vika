package org.yanex.vika.util.network;

import net.rim.device.api.system.Bitmap;

public interface ImageLoaderCallback {
    void onError(String url, String tag);

    void onLoad(String url, String tag, Bitmap bmp);
}