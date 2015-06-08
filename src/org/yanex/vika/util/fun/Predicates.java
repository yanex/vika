package org.yanex.vika.util.fun;

public final class Predicates {

  public static final NotNull notNull = new NotNull();

  public static class InstanceOf implements Predicate {

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


  public static class NotNull implements Predicate {

    public boolean pred(Object it) {
      return it != null;
    }

  }

  public static class StartsWith implements Predicate {

    private final String prefix;

    public StartsWith(String prefix) {
      this.prefix = prefix;
    }

    public boolean pred(Object it) {
      return it instanceof String && ((String) it).startsWith(prefix);
    }
  }

  public static Predicate startsWith(String prefix) {
    return new StartsWith(prefix);
  }

  public static Predicate is(Class clazz) {
    return new InstanceOf(clazz);
  }
}
