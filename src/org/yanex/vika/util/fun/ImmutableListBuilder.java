package org.yanex.vika.util.fun;

import java.util.Vector;

public class ImmutableListBuilder {

  private final Vector vector = new Vector();
  private final Class[] allowedClasses;

  public ImmutableListBuilder(Class[] allowedClasses) {
    this.allowedClasses = allowedClasses;
  }

  public ImmutableListBuilder() {
    this((Class[]) null);
  }

  public ImmutableListBuilder(Class clazz) {
    this(new Class[]{clazz});
  }

  public ImmutableListBuilder add(Object obj) {
    vector.addElement(obj);
    return this;
  }

  public ImmutableList build() {
    return new ImmutableList(vector, allowedClasses);
  }

}
