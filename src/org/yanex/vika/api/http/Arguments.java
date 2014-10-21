package org.yanex.vika.api.http;

import org.yanex.vika.util.StringUtils;
import org.yanex.vika.util.fun.SortingVector;

public class Arguments {

  private final SortingVector vector = new SortingVector().setSort(true);

  private Arguments() {
  }

  public static Arguments make() {
    return new Arguments();
  }

  public static Arguments with(String name, String value) {
    Arguments a = new Arguments();
    a.put(name, value);
    return a;
  }

  public static Arguments with(String name, int value) {
    return with(name, Integer.toString(value));
  }

  public static Arguments with(String name, long value) {
    return with(name, Long.toString(value));
  }

  public static Arguments with(String name, double value) {
    return with(name, Double.toString(value));
  }

  public ArgumentPair byId(int id) {
    return (ArgumentPair) vector.get(id);
  }

  public Arguments put(String name, int value) {
    return put(name, Integer.toString(value));
  }

  public Arguments putIf(boolean condition, String name, int value) {
    return putIf(condition, name, Integer.toString(value));
  }

  public Arguments put(String name, long value) {
    return put(name, Long.toString(value));
  }

  public Arguments putIf(boolean condition, String name, long value) {
    return putIf(condition, name, Long.toString(value));
  }

  public Arguments put(String name, double value) {
    return put(name, Double.toString(value));
  }

  public Arguments putIf(boolean condition, String name, double value) {
    return putIf(condition, name, Double.toString(value));
  }

  public Arguments put(String name, String value) {
    if (value != null) {
      vector.addElement(new ArgumentPair(name, value));
    }
    return this;
  }

  public Arguments putIf(boolean condition, String name, String value) {
    if (condition) {
      vector.addElement(new ArgumentPair(name, value));
    }
    return this;
  }

  public int size() {
    return vector.size();
  }

  public String toString() {
    return vector.toString();
  }

  // key and value strings must not contain " symbol
  public String toJsonString() {
    StringBuffer sb = new StringBuffer("{");
    for (int i = 0; i < vector.size(); ++i) {
      if (sb.length() > 1) {
        sb.append(", ");
      }
      ArgumentPair p = (ArgumentPair) vector.get(i);
      sb.append('"');
      sb.append(p.name);
      sb.append("\": ");
      if (StringUtils.isNumeric(p.value)) {
        sb.append(p.value);
      } else {
        sb.append('"');
        sb.append(p.value);
        sb.append('"');
      }
    }
    return sb.append('}').toString();
  }

  public static class ArgumentPair implements Comparable {
    public final String name;
    public final String value;

    private ArgumentPair(String name, String value) {
      super();
      this.name = name;
      this.value = value;
    }

    public int compareTo(Object obj) {
      if (!(obj instanceof ArgumentPair)) {
        return 0;
      }
      return name.compareTo(((ArgumentPair) obj).name);
    }

    public String toString() {
      return "{" + name + ", " + value + "}";
    }
  }
}
