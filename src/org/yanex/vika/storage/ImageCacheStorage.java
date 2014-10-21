package org.yanex.vika.storage;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;

import java.util.Enumeration;
import java.util.Hashtable;

public class ImageCacheStorage {

  private static final long KEY = 0xa293c5a871479f65L;

  private final PersistentObject imagesPO;
  private final Hashtable images;

  public static final ImageCacheStorage instance = new ImageCacheStorage();

  private ImageCacheStorage() {
    imagesPO = PersistentStore.getPersistentObject(ImageCacheStorage.KEY);
    if (imagesPO.getContents() == null) {
      images = new Hashtable();
      imagesPO.setContents(images);
    } else {
      Object o = imagesPO.getContents();
      if (o instanceof Hashtable) {
        images = (Hashtable) o;
      } else {
        images = new Hashtable();
        imagesPO.setContents(images);
      }
    }
  }

  public void delete(String key) {
    images.remove(key);
    update();
  }

  public void findAndDelete(String value) {
    Enumeration keys = images.keys();
    while (keys.hasMoreElements()) {
      String fn = (String) keys.nextElement();
      if (fn.startsWith(value)) {
        images.remove(fn);
      }
    }
    update();
  }

  public String getFilename(String key) {
    Object o = images.get(key);
    if (o == null) {
      return null;
    } else {
      ImageObject io = (ImageObject) o;
      return io.getFilename();
    }
  }

  public synchronized void put(String url, String filename) {
    images.put(url, new ImageObject(filename, System.currentTimeMillis()));
    update();
  }

  private void update() {
    imagesPO.setContents(images);
    imagesPO.commit();
  }
}