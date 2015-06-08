package org.yanex.vika.storage;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import org.yanex.vika.api.item.collections.Audios;

import java.util.Hashtable;

public class MusicStorage {

    private static final long KEY = 0xd91175bb841136f9L;

    private PersistentObject musicPO;
    private Hashtable music;

    public static final MusicStorage instance = new MusicStorage();

    private MusicStorage() {
        musicPO = PersistentStore.getPersistentObject(MusicStorage.KEY);
        if (musicPO.getContents() == null) {
            music = new Hashtable();
            musicPO.setContents(music);
        } else {
            Object o = musicPO.getContents();
            if (o instanceof Hashtable) {
                music = (Hashtable) o;
            } else {
                music = new Hashtable();
                musicPO.setContents(music);
            }
        }
    }

    public void clear() {
        music.clear();
        update();
    }

    public void delete(String key) {
        music.remove(key);
        update();
    }

    public synchronized Audios get(String key) {
        Object o = music.get(key);
        return (o == null) ? null : (Audios) o;
    }

    public boolean has(String key) {
        return music.get(key) != null;
    }

    public synchronized void put(String key, Audios value) {
        music.put(key, value);
        update();
    }

    private void update() {
        musicPO.setContents(music);
        musicPO.commit();
    }
}
