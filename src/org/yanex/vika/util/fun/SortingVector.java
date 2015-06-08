package org.yanex.vika.util.fun;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.SimpleSortingVector;
import org.yanex.vika.Configuration;
import org.yanex.vika.util.DebugException;

import java.util.Enumeration;
import java.util.Vector;

public class SortingVector {

    private static final int DEFAULT_SIZE = 16;
    private final SimpleSortingVector vector;

    private static final Comparator COMPARATOR = new Comparator() {

        public int compare(Object o1, Object o2) {
            Comparable c1 = (Comparable) o1;
            Comparable c2 = (Comparable) o2;
            return c1.compareTo(c2);
        }
    };

    public SortingVector() {
        this((Vector) null);
    }

    public SortingVector(ImmutableList v) {
        if (v == null) {
            throw new NullPointerException("Vector can't be null");
        }

        vector = new SimpleSortingVector(v.size());
        for (int i = 0; i < v.size(); ++i) {
            Object o = v.getObject(i);
            vector.addElement(testElement(o));
        }

        vector.setSortComparator(SortingVector.COMPARATOR);
    }

    public SortingVector(Vector v) {
        vector = new SimpleSortingVector(v == null ? DEFAULT_SIZE : v.size());

        if (v != null) {
            Enumeration e = v.elements();
            while (e.hasMoreElements()) {
                Object o = e.nextElement();
                vector.addElement(testElement(o));
            }
        }

        vector.setSortComparator(SortingVector.COMPARATOR);
    }

    private Object testElement(Object o) {
        if (Configuration.DEBUG) {
            if (!(o instanceof Comparable)) {
                throw new DebugException("Element of SortingVector must implement Comparable");
            }
        }
        return o;
    }

    public void addElement(Comparable obj) {
        vector.addElement(obj);
    }

    public void clear() {
        vector.removeAllElements();
    }

    public Vector copy() {
        return Vectors2.clone(vector);
    }

    public Enumeration elements() {
        return vector.elements();
    }

    public Object get(int index) {
        return vector.elementAt(index);
    }

    public void insert(Object obj, int index) {
        vector.insertElementAt(obj, index);
    }

    public boolean isEmpty() {
        return vector.isEmpty();
    }

    public void remove(int index) {
        vector.removeElementAt(index);
    }

    public boolean remove(Object obj) {
        return vector.removeElement(obj);
    }

    public int size() {
        return vector.size();
    }

    public SortingVector sort() {
        vector.reSort();
        return this;
    }

    public SortingVector setSort(boolean toSort) {
        vector.setSort(toSort);
        return this;
    }

}
