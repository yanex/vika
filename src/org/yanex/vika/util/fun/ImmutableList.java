package org.yanex.vika.util.fun;

import net.rim.device.api.util.Persistable;
import org.yanex.vika.Configuration;
import org.yanex.vika.util.DebugException;

import java.util.Enumeration;
import java.util.Vector;

public class ImmutableList implements Persistable {

  protected static final Vector EMPTY_VECTOR = new Vector();
  private static final ImmutableList EMPTY_LIST = new ImmutableList(EMPTY_VECTOR);

  private final Vector vector;

  public static ImmutableList empty() {
    return EMPTY_LIST;
  }

  public ImmutableList(Vector vector) {
    if (vector != null) {
      this.vector = vector;
    } else {
      this.vector = EMPTY_VECTOR;
    }
  }

  public ImmutableList(Vector vector, Class[] allowedClasses) {
    this(vector);
    if (Configuration.DEBUG) {
      test(allowedClasses);
    }
  }

  public ImmutableList(Vector vector, Class allowedClass) {
    this(vector);
    if (Configuration.DEBUG) {
      test(new Class[]{allowedClass});
    }
  }

  public RichVector copy() {
    return Vectors2.clone(vector);
  }

  public RichVector copyInvert() {
    return Vectors2.invert(vector);
  }

  public RichVector filter(Class clazz) {
    return Vectors2.filter(vector, new Predicate.InstanceOf(clazz));
  }

  public RichVector filter(Predicate predicate) {
    return Vectors2.filter(vector, predicate);
  }

  public RichVector transform(Function1 fun) {
    return Vectors2.transform(vector, fun);
  }

  public void each(Action1 act) {
    Vectors2.each(vector, act);
  }

  public Object findFirst(Predicate predicate) {
    return Vectors2.findFirst(vector, predicate);
  }

  public Object getObject(int index) {
    return vector.elementAt(index);
  }

  public int size() {
    return vector.size();
  }

  public String toString() {
    return "Im".concat(Vectors2.toString(vector));
  }

  protected void test(Class[] allowedClasses) {
    if (allowedClasses == null || allowedClasses.length == 0) {
      return;
    }

    Enumeration e = vector.elements();
    while (e.hasMoreElements()) {
      Object o = e.nextElement();
      boolean ok = false;
      for (int classIndex = 0; classIndex < allowedClasses.length; ++classIndex) {
        if (allowedClasses[classIndex].isInstance(o)) {
          ok = true;
          break;
        }
      }
      if (!ok) {
        throw new DebugException("Item " + o.toString() + " of ImmutableList("
            + getClass().getName() + ") has different type from "
            + Arrays2.toString(allowedClasses));
      }
    }
  }

}
