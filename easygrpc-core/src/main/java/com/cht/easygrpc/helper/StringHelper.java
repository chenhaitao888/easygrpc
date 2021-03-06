package com.cht.easygrpc.helper;

import com.cht.easygrpc.exception.ResourceProcessException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author : chenhaitao934
 * @date : 1:59 下午 2020/10/9
 */
public class StringHelper {

    private static final String charset = "UTF-8";

    private StringHelper() {
    }

    private static final Pattern INT_PATTERN = Pattern.compile("^\\d+$");

    public static boolean isEmpty(String s) {
        return s == null || s.trim().equals("");
    }

    public static final String NEW_LINE_STRING = System.getProperty("line.separator");

    private static final int PAD_LIMIT = 8192;

    public static final String EMPTY = "";

    public static final String SPACE = " ";

    public static boolean isNotEmpty(String... strs) {
        if (strs != null) {
            for (String s : strs) {
                if (isEmpty(s)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static String generateUUID() {
        return StringHelper.replace(java.util.UUID.randomUUID().toString(), "-", "").toUpperCase();
    }

    public static String replace(String text, String repl, String with) {
        return replace(text, repl, with, -1);
    }

    public static String replace(String text, String repl, String with, int max) {
        if (isEmpty(text) || isEmpty(repl) || with == null || max == 0) {
            return text;
        }
        int start = 0;
        int end = text.indexOf(repl, start);
        if (end == -1) {
            return text;
        }
        int replLength = repl.length();
        int increase = with.length() - replLength;
        increase = (increase < 0 ? 0 : increase);
        increase *= (max < 0 ? 16 : (max > 64 ? 64 : max));
        StringBuilder buf = new StringBuilder(text.length() + increase);
        while (end != -1) {
            buf.append(text.substring(start, end)).append(with);
            start = end + replLength;
            if (--max == 0) {
                break;
            }
            end = text.indexOf(repl, start);
        }
        buf.append(text.substring(start));
        return buf.toString();
    }

    public static boolean hasLength(String str) {
        return (str != null && str.length() > 0);
    }

    public static boolean hasText(String str) {
        if (!hasLength(str)) {
            return false;
        }
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }


    public static String toString(Throwable e) {
        StringWriter w = new StringWriter();
        PrintWriter p = new PrintWriter(w);
        p.print(e.getClass().getName());
        if (e.getMessage() != null) {
            p.print(": " + e.getMessage());
        }
        p.println();
        try {
            e.printStackTrace(p);
            return w.toString();
        } finally {
            p.close();
        }
    }

    public static String toString(String msg, Throwable e) {
        StringWriter w = new StringWriter();
        w.write(msg + "\n");
        PrintWriter p = new PrintWriter(w);
        try {
            e.printStackTrace(p);
            return w.toString();
        } finally {
            p.close();
        }
    }

    public static String concat(String... strings) {
        if (strings == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String str : strings) {
            if (str != null) {
                sb.append(str);
            }
        }
        return sb.toString();
    }

    public static String trim(String str) {
        if (str == null) {
            return null;
        }
        return str.trim();
    }

    public static boolean isInteger(String str) {
        return !(str == null || str.length() == 0) && INT_PATTERN.matcher(str).matches();
    }

    public static String toString(Object value) {
        if (value == null) {
            return null;
        }

        return value.toString();
    }

    public static String[] splitWithTrim(String spilt, String sequence) {
        if (isEmpty(sequence)) {
            return null;
        }
        String[] values = sequence.split(spilt);
        if (values.length == 0) {
            return values;
        }
        for (int i = 0; i < values.length; i++) {
            values[i] = values[i].trim();
        }
        return values;
    }

    /**
     * Capitalize a {@code String}, changing the first letter to
     * upper case as per {@link Character#toUpperCase(char)}.
     * No other letters are changed.
     * @param str the {@code String} to capitalize, may be {@code null}
     * @return the capitalized {@code String}, or {@code null} if the supplied
     * string is {@code null}
     */
    public static String capitalize(String str) {
        return changeFirstCharacterCase(str, true);
    }

    /**
     * Uncapitalize a {@code String}, changing the first letter to
     * lower case as per {@link Character#toLowerCase(char)}.
     * No other letters are changed.
     * @param str the {@code String} to uncapitalize, may be {@code null}
     * @return the uncapitalized {@code String}, or {@code null} if the supplied
     * string is {@code null}
     */
    public static String uncapitalize(String str) {
        return changeFirstCharacterCase(str, false);
    }

    private static String changeFirstCharacterCase(String str, boolean capitalize) {
        if (str == null || str.length() == 0) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str.length());
        if (capitalize) {
            sb.append(Character.toUpperCase(str.charAt(0)));
        }
        else {
            sb.append(Character.toLowerCase(str.charAt(0)));
        }
        sb.append(str.substring(1));
        return sb.toString();
    }

    public static String getString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try {
            return new String(bytes, charset);
        } catch (UnsupportedEncodingException e) {
            //ignored
            return "";
        }
    }

    public static byte[] getBytes(String s) {
        if (s == null) {
            return new byte[0];
        }
        try {
            return s.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            //ignored
            return new byte[0];
        }
    }

    public static boolean isMatch(String src, String regex) {
        return getMatcher(src, regex).find();
    }

    private static Matcher getMatcher(String src, String regex) {
        Pattern pattern = null;

        try {
            pattern = Pattern.compile(regex);
        } catch (PatternSyntaxException var4) {
            throw new ResourceProcessException(var4);
        }

        Matcher matcher = pattern.matcher(src);
        return matcher;
    }

    public static String getUUIDNoLine() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replaceAll("-", "");
    }

    public static String leftPad(final String str, final int size, final char padChar) {
        if (str == null) {
            return null;
        }
        final int pads = size - str.length();
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (pads > PAD_LIMIT) {
            return leftPad(str, size, String.valueOf(padChar));
        }
        return repeat(padChar, pads).concat(str);
    }

    public static String leftPad(final String str, final int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = SPACE;
        }
        final int padLen = padStr.length();
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= PAD_LIMIT) {
            return leftPad(str, size, padStr.charAt(0));
        }

        if (pads == padLen) {
            return padStr.concat(str);
        } else if (pads < padLen) {
            return padStr.substring(0, pads).concat(str);
        } else {
            final char[] padding = new char[pads];
            final char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return new String(padding).concat(str);
        }
    }

    public static String repeat(final char ch, final int repeat) {
        if (repeat <= 0) {
            return EMPTY;
        }
        final char[] buf = new char[repeat];
        for (int i = repeat - 1; i >= 0; i--) {
            buf[i] = ch;
        }
        return new String(buf);
    }
}
