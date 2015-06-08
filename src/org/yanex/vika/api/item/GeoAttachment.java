package org.yanex.vika.api.item;

public class GeoAttachment extends Attachment {

    private final Geo geo;

    public GeoAttachment(Message message) {
        super(message.getUid());
        this.geo = message.getGeo();
    }

    public GeoAttachment(long ownerId, Geo geo) {
        super(ownerId);
        this.geo = geo;
    }

    public Geo getGeo() {
        return geo;
    }

}
