package org.yanex.vika.storage;

import net.rim.device.api.util.Persistable;

class ImageObject implements Persistable {

  private final String filename;
  private final long timestamp;

  ImageObject(String filename, long timestamp) {
    this.filename = filename;
    this.timestamp = timestamp;
  }

  public String getFilename() {
    return filename;
  }

  public long getTimestamp() {
    return timestamp;
  }
}