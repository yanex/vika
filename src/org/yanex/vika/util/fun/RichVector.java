package org.yanex.vika.util.fun;

import net.rim.device.api.util.Persistable;

import java.util.Enumeration;
import java.util.Vector;

public class RichVector extends Vector implements Persistable {

    public RichVector() {
        super();
    }

    public RichVector(int initialCapacity, int capacityIncrement) {
        super(initialCapacity, capacityIncrement);
    }

    public RichVector(int initialCapacity) {
        super(initialCapacity);
    }

    public RichVector(Vector vector) {
        super(vector.size());
        addAll(vector);
    }

    public void addAll(Object[] elements) {
        if (elements != null) {
            for (int i = 0; i < elements.length; ++i) {
                addElement(elements[i]);
            }
        }
    }

    public void addAll(Vector vector) {
        if (vector != null) {
            Enumeration e = vector.elements();
            while (e.hasMoreElements()) {
                addElement(e.nextElement());
            }
        }
    }

    public void addAll(int index, Object[] elements) {
        if (elements != null) {
            for (int i = elements.length - 1; i >= 0; --i) {
                add(index, elements[i]);
            }
        }
    }

    public void addAll(int index, Vector vector) {
        if (vector != null) {
            Enumeration e = vector.elements();
            while (e.hasMoreElements()) {
                add(index, e.nextElement());
            }
        }
    }

    public Object get(int index) {
        return super.elementAt(index);
    }

    public RichVector add(Object obj) {
        super.addElement(obj);
        return this;
    }

    public RichVector add(int index, Object obj) {
        super.insertElementAt(obj, index);
        return this;
    }

    public RichVector replace(int index, Object obj) {
        super.setElementAt(obj, index);
        return this;
    }

    public RichVector remove(int index) {
        super.removeElementAt(index);
        return this;
    }

    public RichVector remove(Object obj) {
        super.removeElement(obj);
        return this;
    }

    public RichVector clear() {
        super.removeAllElements();
        return this;
    }

    public RichVector filter(Predicate predicate) {
        return Vectors2.filter(this, predicate);
    }

    public Object firstOrNull(Predicate predicate) {
        Enumeration e = elements();
        while (e.hasMoreElements()) {
            Object o = e.nextElement();
            if (predicate.pred(o)) {
                return o;
            }
        }
        return null;
    }

    public RichVector transform(Function1 fun) {
        return Vectors2.transform(this, fun);
    }

    public RichVector transformNotNull(Function1 fun) {
        return Vectors2.transformNotNull(this, fun);
    }

    public String join(String separator) {
        StringBuffer buffer = new StringBuffer();
        Enumeration e = elements();
        boolean first = true;
        while (e.hasMoreElements()) {
            if (separator != null && first) {
                buffer.append(separator);
                first = false;
            }
            buffer.append(e.nextElement().toString());
        }
        return buffer.toString();
    }

    public Object foldr(Object start, Function2 fun) {
        return Vectors2.foldr(start, this, fun);
    }

    public RichVector copy() {
        return Vectors2.clone(this);
    }

    public RichVector first(int count) {
        return Vectors2.first(this, count);
    }

    public RichVector last(int count) {
        return Vectors2.last(this, count);
    }

    public void each(Action1 act) {
        Vectors2.each(this, act);
    }

    public String toString() {
        return Vectors2.toString(this);
    }
}
