package org.yanex.vika.util.fun;

final class Arrays2 {

  static void each(Object[] array, Action1 act) {
    if (array == null) {
      return;
    }

    for (int i = 0; i < array.length; ++i) {
      act.run(array[i]);
    }
  }

  static String toString(Object[] array) {
    StringBuffer stringBuffer = new StringBuffer("[");
    for (int i = 0; i < array.length; ++i) {
      if (i > 0) {
        stringBuffer.append(", ");
      }
      Object o = array[i];
      if (o == null) {
        stringBuffer.append("<null>");
      } else {
        stringBuffer.append(o.toString());
      }
    }
    return stringBuffer.append("]").toString();
  }

}
