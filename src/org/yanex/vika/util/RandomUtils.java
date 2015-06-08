package org.yanex.vika.util;

import java.util.Random;

public class RandomUtils {

    public static final RandomUtils instance = new RandomUtils();

    private Random r = new Random();

    public String nextIntString(int length) {
        StringBuffer b = new StringBuffer(length);
        for (int i = 0; i < length; ++i) {
            b.append((char) (r.nextInt('9' - '0') + '0'));
        }
        return b.toString();
    }

    public String nextString() {
        return nextString(8);
    }

    private String nextString(int length) {
        StringBuffer b = new StringBuffer(length);
        for (int i = 0; i < length; ++i) {
            b.append((char) (r.nextInt('z' - 'A') + 'A'));
        }
        return b.toString();
    }

}
