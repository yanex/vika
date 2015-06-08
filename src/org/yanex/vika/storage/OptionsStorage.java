package org.yanex.vika.storage;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;

import java.util.Hashtable;

public class OptionsStorage {

    private static final long KEY = 0x8789e43ac61919cL;

    private PersistentObject optionsPO;
    private Hashtable options;

    public static final OptionsStorage instance = new OptionsStorage();

    private OptionsStorage() {
        optionsPO = PersistentStore.getPersistentObject(OptionsStorage.KEY);
        if (optionsPO.getContents() == null) {
            options = new Hashtable();
            optionsPO.setContents(options);
        } else {
            Object o = optionsPO.getContents();
            if (o instanceof Hashtable) {
                options = (Hashtable) o;
            } else {
                options = new Hashtable();
                optionsPO.setContents(options);
            }
        }
    }

    public void delete(String key) {
        options.remove(key);
        update();
    }

    public String getString(String key) { // NO_UCD (unused code)
        return getString(key, "");
    }

    public String getString(String key, String defaultValue) {
        Object o = options.get(key);
        if (o == null) {
            return defaultValue;
        } else {
            return (String) o;
        }
    }

    public void set(String key, String value) {
        options.put(key, value);
        update();
    }

    private void update() {
        optionsPO.setContents(options);
        optionsPO.commit();
    }
}
