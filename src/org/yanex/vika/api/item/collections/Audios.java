package org.yanex.vika.api.item.collections;

import net.rim.device.api.util.Persistable;
import org.yanex.vika.api.item.Audio;
import org.yanex.vika.util.fun.ImmutableList;

import java.util.Vector;

public class Audios extends ImmutableList implements Persistable {

    public Audios(Vector vector) {
        super(vector, Audio.class);
    }

    public Audio get(int index) {
        return (Audio) getObject(index);
    }

}
