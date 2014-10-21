package org.yanex.vika.util.fun;

public interface Predicate {

  public static final NotNull notNull = new NotNull();

  static class NotNull implements Predicate {

    public boolean pred(Object it) {
      return it != null;
    }

  }

  class InstanceOf implements Predicate {

    private final Class clazz;

    public InstanceOf(Class clazz) {
      if (clazz == null) {
        throw new IllegalArgumentException("Class variable can't be null");
      }
      this.clazz = clazz;
    }

    public boolean pred(Object it) {
      return clazz.isInstance(it);
    }

  }

  boolean pred(Object it);

}
