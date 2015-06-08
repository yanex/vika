package org.yanex.vika.util;

import me.regexp.RE;

import java.util.Vector;

public class StringUtils {

    private static final String delimeters = " \n\r\t,.;)>]/?!\\|~`^%@&*-_+=";

    public static String[] matches(String original, String regexp) {
        Vector results = new Vector();
        int i;

        RE re = new RE(regexp);

        int position = 0;
        while (re.match(original, position)) {
            results.addElement(re.getParen(0));
            position = re.getParenEnd(0);
        }

        String[] ret = new String[results.size()];
        for (i = 0; i < ret.length; ++i) {
            ret[i] = (String) results.elementAt(i);
        }

        return ret;
    }

    public static boolean nonRegistryContains(String big, String small) {
        big = big.toUpperCase();
        small = small.toUpperCase();

        return big.indexOf(small) >= 0;
    }

    public static String replace(String _text, String _searchStr, String _replacementStr) {
        // String buffer to store str
        StringBuffer sb = new StringBuffer();

        // Search for search
        int searchStringPos = _text.indexOf(_searchStr);
        int startPos = 0;
        int searchStringLength = _searchStr.length();

        // Iterate to add string
        while (searchStringPos != -1) {
            sb.append(_text.substring(startPos, searchStringPos)).append(_replacementStr);
            startPos = searchStringPos + searchStringLength;
            searchStringPos = _text.indexOf(_searchStr, startPos);
        }

        // Create string
        sb.append(_text.substring(startPos, _text.length()));

        return sb.toString();
    }

    public static String[] split(String splitStr, String delimiter) {
        StringBuffer token = new StringBuffer();
        Vector tokens = new Vector();
        // split
        char[] chars = splitStr.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (delimiter.indexOf(chars[i]) != -1) {
                // we bumbed into a delimiter
                if (token.length() > 0) {
                    tokens.addElement(token.toString());
                    token.setLength(0);
                }
            } else {
                token.append(chars[i]);
            }
        }
        // don't forget the "tail"...
        if (token.length() > 0) {
            tokens.addElement(token.toString());
        }
        // convert the vector into an array
        String[] splitArray = new String[tokens.size()];
        for (int i = 0; i < splitArray.length; i++) {
            splitArray[i] = (String) tokens.elementAt(i);
        }
        return splitArray;
    }

    public static boolean isNumeric(String str) {
        int l = str.length();
        for (int i = 0; i < l; ++i) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    public static Vector splitWords(String text) {
        Vector words = new Vector();
        if (text.length() < 2) {
            words.addElement(text);
            return words;
        }

        text = StringUtils.replace(text, "\r", "");

        int start = 0;
        int pos = 1;
        while (pos < text.length()) {
            char c = text.charAt(pos);
            if (StringUtils.delimeters.indexOf(c) >= 0) {
                String s = text.substring(start, pos);
                if (s.length() > 0) {
                    words.addElement(s);
                }
                words.addElement(text.substring(pos, pos + 1));

                start = pos + 1;
            }
            pos++;
        }
        if (start < text.length()) {
            String s = text.substring(start);
            if (s.length() > 0) {
                words.addElement(s);
            }
        }
        return words;
    }
}
