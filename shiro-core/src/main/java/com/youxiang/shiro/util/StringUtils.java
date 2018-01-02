package com.youxiang.shiro.util;

/**
 * Author: RiversLau
 * Date: 2018/1/2 17:26
 */
public class StringUtils {

    private static final String EMPTY_STRING = "";

    public static boolean hasText(String str) {
        if (!hasLength(str)) {
            return false;
        }
        int len = str.length();
        for (int i = 0; i < len; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasLength(String str) {
        return str != null && str.length() > 0;
    }

    public static String clean(String in) {
        String out = in;

        if (in != null) {
            out = in.trim();
            if (out.equals(EMPTY_STRING)) {
                out = null;
            }
        }
        return out;
    }
}
