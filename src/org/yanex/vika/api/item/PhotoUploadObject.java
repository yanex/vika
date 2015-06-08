package org.yanex.vika.api.item;

public class PhotoUploadObject {

    public final String server;
    public final String photo;
    public final String hash;

    public PhotoUploadObject(String server, String photo, String hash) {
        this.server = server;
        this.photo = photo;
        this.hash = hash;
    }

}
