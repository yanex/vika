package org.yanex.vika.api.util;

import net.rim.device.api.system.Bitmap;
import org.yanex.vika.gui.util.R;
import org.yanex.vika.util.StringUtils;
import org.yanex.vika.util.fun.RichVector;

import java.util.Hashtable;

public class Emoticons {

    public static Emoticons instance = new Emoticons();

    private static final Hashtable emoticons = new Hashtable();

    private static final String EMOJI = "D83DDE0A,D83DDE03,D83DDE09,D83DDE06,D83DDE1C,D83DDE0B,D83DDE0D,D83DDE0E,D83DDE12,D83DDE0F,D83DDE14,D83DDE22,D83DDE2D,D83DDE29,D83DDE28,D83DDE10,D83DDE0C,D83DDE20,D83DDE21,D83DDE07,D83DDE30,D83DDE32,D83DDE33,D83DDE37,D83DDE1A,D83DDE08,2764,D83DDC4D,D83DDC4E,261D,270C,D83DDC4C,26BD,26C5,D83CDF1F,D83CDF4C,D83CDF7A,D83CDF7B,D83CDF39,D83CDF45,D83CDF52,D83CDF81,D83CDF82,D83CDF84,D83CDFC1,D83CDFC6,D83DDC0E,D83DDC0F,D83DDC1C,D83DDC2B,D83DDC2E,D83DDC03,D83DDC3B,D83DDC3C,D83DDC05,D83DDC13,D83DDC18,D83DDC94,D83DDCAD,D83DDC36,D83DDC31,D83DDC37,D83DDC11,23F3,26BE,26C4,2600,D83CDF3A,D83CDF3B,D83CDF3C,D83CDF3D,D83CDF4A,D83CDF4B,D83CDF4D,D83CDF4E,D83CDF4F,D83CDF6D,D83CDF37,D83CDF38,D83CDF46,D83CDF49,D83CDF50,D83CDF51,D83CDF53,D83CDF54,D83CDF55,D83CDF56,D83CDF57,D83CDF69,D83CDF83,D83CDFAA,D83CDFB1,D83CDFB2,D83CDFB7,D83CDFB8,D83CDFBE,D83CDFC0,D83CDFE6,D83DDC00,D83DDC0C,D83DDC1B,D83DDC1D,D83DDC1F,D83DDC2A,D83DDC2C,D83DDC2D,D83DDC3A,D83DDC3D,D83DDC2F,D83DDC5C,D83DDC7B,D83DDC14,D83DDC23,D83DDC24,D83DDC40,D83DDC42,D83DDC43,D83DDC46,D83DDC47,D83DDC48,D83DDC51,D83DDC60,D83DDCA1,D83DDCA3,D83DDCAA,D83DDCAC,D83DDD14,D83DDD25";

    static {
        String[] list = StringUtils.split(Emoticons.EMOJI, ",");

        for (int i = 0; i < list.length; ++i) {
            String s = list[i];
            int i1 = 0, i2 = 0;
            if (s.length() == 8) {
                String s1 = s.substring(0, 4);
                String s2 = s.substring(4);
                i1 = Integer.parseInt(s1, 16);
                i2 = Integer.parseInt(s2, 16);
            } else if (s.length() == 4) {
                i1 = Integer.parseInt(s, 16);
                i2 = 0;
            }

            if (i1 > 0 || i2 > 0) {
                Emoticons.put(i1, i2, R.instance.getBitmap("Emoji/" + s + ".png"));
            }
        }
    }

    private static String getEmoticonString(int b1, int b2) {
        char c1 = (char) b1;
        char c2 = (char) b2;

        if (c1 > 0 && c2 > 0) {
            return c1 + "" + c2;
        } else if (c1 > 0) {
            return c1 + "";
        } else if (c2 > 0) {
            return c2 + "";
        } else {
            return "";
        }
    }

    private static void put(int b1, int b2, Bitmap b) {
        String s = Emoticons.getEmoticonString(b1, b2);
        if (s.length() > 0 && b != null) {
            Emoticons.emoticons.put(s, b);
        }
    }

    private Emoticons() {

    }

    public Bitmap getByHexString(String s) {
        int i1 = 0, i2 = 0;
        if (s.length() == 8) {
            String s1 = s.substring(0, 4);
            String s2 = s.substring(4);
            i1 = Integer.parseInt(s1, 16);
            i2 = Integer.parseInt(s2, 16);
        } else if (s.length() == 4) {
            i1 = Integer.parseInt(s, 16);
            i2 = 0;
        }

        s = Emoticons.getEmoticonString(i1, i2);
        return getEmoticon(s);
    }

    public Bitmap getEmoticon(String t) {
        Object o = Emoticons.emoticons.get(t);
        if (o == null) {
            return null;
        }

        return (Bitmap) o;
    }
}
