package org.yanex.vika.util.fun;

import java.util.Enumeration;
import java.util.Vector;

final class Vectors2 {

    static String toString(Vector vector) {
        StringBuffer stringBuffer = new StringBuffer("V[");
        Enumeration e = vector.elements();
        while (e.hasMoreElements()) {
            if (stringBuffer.length() > 0) {
                stringBuffer.append(", ");
            }
            Object o = e.nextElement();
            if (o == null) {
                stringBuffer.append("<null>");
            } else {
                stringBuffer.append(o.toString());
            }
        }
        return stringBuffer.append("]").toString();
    }

    static Object findFirst(Vector vector, Predicate predicate) {
        Enumeration e = vector.elements();
        while (e.hasMoreElements()) {
            Object o = e.nextElement();
            if (predicate.pred(o)) {
                return o;
            }
        }
        return null;
    }

    static RichVector invert(Vector vector) {
        RichVector newVector = new RichVector(vector.size());
        for (int i = vector.size() - 1; i >= 0; --i) {
            newVector.addElement(vector.elementAt(i));
        }
        return newVector;
    }

    static RichVector clone(Vector vector) {
        if (vector == null) {
            return new RichVector();
        }

        RichVector newVector = new RichVector(vector.size());
        Enumeration e = vector.elements();
        while (e.hasMoreElements()) {
            newVector.addElement(e.nextElement());
        }
        return newVector;
    }

    static RichVector first(Vector vector, int count) {
        if (vector == null) {
            return new RichVector();
        }

        RichVector newVector = new RichVector(vector.size());
        Enumeration e = vector.elements();
        while (e.hasMoreElements() && (count--) > 0) {
            newVector.addElement(e.nextElement());
        }
        return newVector;
    }

    static RichVector last(Vector vector, int count) {
        if (vector == null) {
            return new RichVector();
        }

        RichVector newVector = new RichVector(vector.size());
        for (int i = vector.size() - 1; i >= 0 && (count--) > 0; --i) {
            newVector.addElement(vector.elementAt(i));
        }
        return newVector;
    }

    static void each(Vector vector, Action1 act) {
        if (vector == null) {
            return;
        }

        Enumeration e = vector.elements();
        while (e.hasMoreElements()) {
            act.run(e.nextElement());
        }
    }

    static RichVector filter(Vector vector, Predicate pred) {
        if (vector == null) {
            return new RichVector();
        }
        RichVector newVector = new RichVector(vector.size());
        Enumeration e = vector.elements();
        while (e.hasMoreElements()) {
            Object o = e.nextElement();
            if (pred.pred(o)) {
                newVector.addElement(o);
            }
        }
        return newVector;
    }

    static Object foldr(Object start, Vector vector, Function2 fun) {
        Enumeration e = vector.elements();
        while (e.hasMoreElements()) {
            start = fun.apply(start, e.nextElement());
        }
        return start;
    }

    static RichVector transform(Vector vector, Function1 fun) {
        if (vector == null) {
            return new RichVector();
        }
        RichVector newVector = new RichVector(vector.size());
        Enumeration e = vector.elements();
        while (e.hasMoreElements()) {
            newVector.addElement(fun.apply(e.nextElement()));
        }
        return newVector;
    }

    static RichVector transformNotNull(Vector vector, Function1 fun) {
        if (vector == null) {
            return new RichVector();
        }
        RichVector newVector = new RichVector(vector.size());
        Enumeration e = vector.elements();
        while (e.hasMoreElements()) {
            Object o = fun.apply(e.nextElement());
            if (o != null) {
                newVector.addElement(o);
            }
        }
        return newVector;
    }

}
