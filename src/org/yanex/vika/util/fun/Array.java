package org.yanex.vika.util.fun;

public class Array {

    private final Object[] array;

    public Array(Object[] array) {
        this.array = array;
    }

    public Object get(int index) {
        return array[index];
    }

    public void replace(int index, Object o) {
        array[index] = o;
    }

    public int size() {
        return array.length;
    }

    public void each(Action1 act) {
        Arrays2.each(array, act);
    }

    public boolean any(Predicate condition) {
        return Arrays2.any(array, condition);
    }

    public Object firstOrNull(Predicate condition) {
        return Arrays2.firstOrNull(array, condition);
    }

    public Array clone() {
        return new Array(cloneArray());
    }

    public String toString() {
        return Arrays2.toString(array);
    }

    private Object[] cloneArray() {
        Object[] na = new Object[array.length];
        System.arraycopy(array, 0, na, 0, na.length);
        return na;
    }

}
