package com.youxiang.shiro.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: RiversLau
 * Date: 2018/1/2 17:26
 */
public class StringUtils {

    public static final String EMPTY_STRING = "";

    public static final char DEFAULT_DELIMITER_CHAR = ',';

    public static final char DEFAULT_QUOTE_CHAR = '"';

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

    public static String toString(Object[] array) {
        return toDelimitedString(array, ",");
    }

    public static String toDelimitedString(Object[] array, String delimiter) {
        if (array == null || array.length == 0) {
            return EMPTY_STRING;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                sb.append(delimiter);
            }
            sb.append(array[i]);
        }
        return sb.toString();
    }

    public static String[] split(String line) {
        return split(line, DEFAULT_DELIMITER_CHAR);
    }

    public static String[] split(String line, char delimiter) {
        return split(line, delimiter, DEFAULT_QUOTE_CHAR);
    }

    public static String[] split(String line, char delimiter, char quoteChar) {
        return split(line, delimiter, quoteChar, quoteChar);
    }

    public static String[] split(String line, char delimiter, char beginQuoteChar, char endQuoteChar) {
        return split(line, delimiter, beginQuoteChar, endQuoteChar, false, true);
    }

    public static String[] split(String aLine, char delimiter, char beginQuoteChar,
                                 char endQuoteChar, boolean retainQuotes, boolean trimTokens) {
        String line = clean(aLine);
        if (line == null) {
            return null;
        }

        List<String> tokens = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == beginQuoteChar) {
                if (inQuotes && line.length() > (i + 1) &&
                        line.charAt(i + 1) == beginQuoteChar) {
                    sb.append(line.charAt(i + 1));
                    i++;
                } else {
                    inQuotes = !inQuotes;
                    if (retainQuotes) {
                        sb.append(c);
                    }
                }
            } else if (c == endQuoteChar) {
                inQuotes = !inQuotes;
                if (retainQuotes) {
                    sb.append(c);
                }
            } else if (c == delimiter && !inQuotes) {
                String s = sb.toString();
                if (trimTokens) {
                    s = s.trim();
                }
                tokens.add(s);
                sb = new StringBuilder();
            } else {
                sb.append(c);
            }
        }
        String s = sb.toString();
        if (trimTokens) {
            s = s.trim();
        }
        tokens.add(s);
        return tokens.toArray(new String[tokens.size()]);
    }
}
