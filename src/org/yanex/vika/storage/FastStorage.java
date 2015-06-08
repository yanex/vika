package org.yanex.vika.storage;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;

import java.util.Hashtable;

public class FastStorage {

    private static final long KEY = 0xdb2b2d1bc5a009acL;

    private final PersistentObject fastPO;
    private final Hashtable fast;

    public static final FastStorage instance = new FastStorage();

    private FastStorage() {
        fastPO = PersistentStore.getPersistentObject(FastStorage.KEY);
        if (fastPO.getContents() == null) {
            fast = new Hashtable();
            fastPO.setContents(fast);
        } else {
            Object o = fastPO.getContents();
            if (o instanceof Hashtable) {
                fast = (Hashtable) o;
            } else {
                fast = new Hashtable();
                fastPO.setContents(fast);
            }
        }
    }

    public void delete(String key) {
        fast.remove(key);
    }

    public long getLong(String key) {
        Object o = fast.get(key);
        if (o == null || !(o instanceof Long)) {
            return -1;
        }

        return ((Long) o).longValue();
    }

    public String getString(String key) {
        return getString(key, "");
    }

    public String getString(String key, String defaultValue) {
        Object o = fast.get(key);
        if (o == null) {
            return defaultValue;
        } else {
            return (String) o;
        }
    }

    public void set(String key, long value) {
        fast.put(key, new Long(value));
    }

    public void set(String key, String value) {
        fast.put(key, value);
    }

    public boolean spentFromThen(String key, long interval) {
        return System.currentTimeMillis() - interval > getLong(key);
    }

    public void update() {
        fastPO.setContents(fast);
        fastPO.commit();
    }
}
