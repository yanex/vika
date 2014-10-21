package org.yanex.vika.storage;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;

import java.util.Hashtable;

public class SafeStorage {

  private static final long KEY = 0xf648aaaf764b7e5fL;

  private PersistentObject safePO;
  private Hashtable safe;

  public static final SafeStorage instance = new SafeStorage();

  private SafeStorage() {
    refresh();
  }

  public void delete(String key) {
    safe.remove(key);
    safePO.setContents(safe);
    safePO.commit();
  }

  public String getString(String key, String defaultValue) {
    refresh();
    Object o = safe.get(key);
    if (o == null) {
      return defaultValue;
    } else {
      return (String) o;
    }
  }

  private void refresh() {
    safePO = PersistentStore.getPersistentObject(SafeStorage.KEY);
    if (safePO.getContents() == null) {
      safe = new Hashtable();
      safePO.setContents(safe);
    } else {
      Object o = safePO.getContents();
      if (o instanceof Hashtable) {
        safe = (Hashtable) o;
      } else {
        safe = new Hashtable();
        safePO.setContents(safe);
      }
    }
  }

  public void set(String key, String value) {
    safe.put(key, value);
    safePO.setContents(safe);
    safePO.commit();
  }

}
