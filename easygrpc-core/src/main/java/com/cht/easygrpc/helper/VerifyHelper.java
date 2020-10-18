package com.cht.easygrpc.helper;

import com.google.common.base.VerifyException;

import java.util.Collection;

/**
 * @author : chenhaitao934
 */
public class VerifyHelper {

    public VerifyHelper() {
    }

    public static void typeCheck(Object value, Class<?> cls, String message) {
        if (value == null) {
            throw new VerifyException(message);
        } else if (StringHelper.isEmpty(message)) {
            typeCheck(value, cls);
        } else {
            Class<?> type = value.getClass();
            if (!cls.isAssignableFrom(type)) {
                throw new VerifyException(message);
            }
        }
    }

    public static void typeCheck(Object value, Class<?> cls) {
        typeCheck(value, cls, String.format("[Verify failed] - the argument must be assignable from [%s]", cls.getName()));
    }

    public static void isInterface(Class<?> cls, String message) {
        if (StringHelper.isEmpty(message)) {
            isInterface(cls);
        } else if (!cls.isInterface()) {
            throw new VerifyException(message);
        }
    }

    public static void isInterface(Class<?> cls) {
        isInterface(cls, "[Verify failed] - the class argument must be a interface");
    }

    public static void isNull(Object object, String message) {
        if (StringHelper.isEmpty(message)) {
            isNull(object);
        } else if (object != null) {
            throw new VerifyException(message);
        }
    }

    public static void isNull(Object object) {
        isNull(object, "[Verify failed] - the object argument must be null");
    }

    public static void notNull(Object object, String message) {
        if (StringHelper.isEmpty(message)) {
            notNull(object);
        } else if (object == null) {
            throw new VerifyException(message);
        }
    }

    public static void notNull(Object object) {
        notNull(object, "[Verify failed] - the object argument must not be null");
    }

    public static void hasLength(Object value, String message) {
        if (value == null) {
            throw new VerifyException(message);
        } else if (StringHelper.isEmpty(message)) {
            hasLength(value);
        } else {
            Class<?> type = value.getClass();
            if (String.class.isAssignableFrom(type)) {
                hasLength((String)value, message);
            } else {

            }

        }
    }

    public static void hasLength(Object value) {
        hasLength(value, "[Verify failed] - the object argument must has length.");
    }

    public static void hasLength(String string, String message) {
        if (StringHelper.isEmpty(message)) {
            hasLength(string);
        } else if (string == null || string.length() <= 0) {
            throw new VerifyException(message);
        }
    }

    public static void hasLength(String string) {
        hasLength(string, "[Verify failed] - the string argument must has length.");
    }

    public static void hasText(String string, String message) {
        if (StringHelper.isEmpty(message)) {
            hasText(string);
        } else {
            hasLength(string, message);
            if (string.trim().length() <= 0) {
                throw new VerifyException(message);
            }
        }
    }

    public static void hasText(String string) {
        hasText(string, "[Verify failed] - the string argument must has text.");
    }

    public static void notEmpty(Object value, String message) {
        if (value == null) {
            throw new VerifyException(message);
        } else if (StringHelper.isEmpty(message)) {
            notEmpty(value);
        } else {
            Class<?> type = value.getClass();
            if (Collection.class.isAssignableFrom(type)) {
                notEmpty((Collection)Collection.class.cast(value), message);
            } else if (type.isArray()) {
                notEmpty((Object[])((Object[])value), message);
            } else {
            }

        }
    }

    public static void notEmpty(Object value) {
        notEmpty(value, "[Verify failed] - the object argument must not be empty.");
    }

    public static void notEmpty(Collection<?> collection, String message) {
        if (StringHelper.isEmpty(message)) {
            notEmpty(collection);
        } else if (collection == null || collection.isEmpty()) {
            throw new VerifyException(message);
        }
    }

    public static void notEmpty(Collection<?> collection) {
        notEmpty(collection, "[Verify failed] - the collection argument must not be empty.");
    }

    public static void notEmpty(Object[] array, String message) {
        if (StringHelper.isEmpty(message)) {
            notEmpty(array);
        } else if (array == null || array.length <= 0) {
            throw new VerifyException(message);
        }
    }

    public static void notEmpty(Object[] array) {
        notEmpty(array, "[Verify failed] - the array argument must not be empty.");
    }

    public static void empty(Object value, String message) {
        if (value == null) {
            throw new VerifyException(message);
        } else if (StringHelper.isEmpty(message)) {
            empty(value);
        } else {
            Class<?> type = value.getClass();
            if (Collection.class.isAssignableFrom(type)) {
                empty((Collection)Collection.class.cast(value), message);
            } else if (type.isArray()) {
                empty((Object[])((Object[])value), message);
            } else {
            }

        }
    }

    public static void empty(Object value) {
        empty(value, "[Verify failed] - the object argument must be empty.");
    }

    public static void empty(Collection<?> collection, String message) {
        if (StringHelper.isEmpty(message)) {
            empty(collection);
        } else if (collection == null || !collection.isEmpty()) {
            throw new VerifyException(message);
        }
    }

    public static void empty(Collection<?> collection) {
        empty(collection, "[Verify failed] - the collection argument must be empty.");
    }

    public static void empty(Object[] array, String message) {
        if (StringHelper.isEmpty(message)) {
            empty(array);
        } else if (array == null || array.length > 0) {
            throw new VerifyException(message);
        }
    }

    public static void empty(Object[] array) {
        empty(array, "[Verify failed] - the array argument must be empty.");
    }

    public static void lenthLimit(Object value, long length, String message) {
        if (value == null) {
            throw new VerifyException(message);
        } else if (StringHelper.isEmpty(message)) {
            lenthLimit(value, length);
        } else {
            Class<?> type = value.getClass();
            if (Collection.class.isAssignableFrom(type)) {
                lenthLimit((Collection)Collection.class.cast(value), length, message);
            } else if (type.isArray()) {
                lenthLimit((Object[])((Object[])value), length, message);
            } else if (String.class.isAssignableFrom(type)) {
                lenthLimit((String)value, length, message);
            } else {
            }

        }
    }

    public static void lenthLimit(Object value, long length) {
        lenthLimit(value, length, "[Verify failed] - the object size is out of limit.");
    }

    public static void lenthLimit(Collection<?> collection, long limit, String message) {
        if (StringHelper.isEmpty(message)) {
            lenthLimit(collection, limit);
        } else if (collection == null || (long)collection.size() > limit) {
            throw new VerifyException(message);
        }
    }

    public static void lenthLimit(Collection<?> collection, long limit) {
        lenthLimit(collection, limit, "[Verify failed] - the collection size is out of limit.");
    }

    public static void lenthLimit(Object[] array, long limit, String message) {
        if (StringHelper.isEmpty(message)) {
            lenthLimit(array, limit);
        } else if (array == null || (long)array.length > limit) {
            throw new VerifyException(message);
        }
    }

    public static void lenthLimit(Object[] array, long limit) {
        lenthLimit(array, limit, "[Verify failed] - the array size is out of limit.");
    }

    public static void lenthLimit(String string, long limit, String message) {
        if (StringHelper.isEmpty(message)) {
            lenthLimit(string, limit);
        } else if (string == null || (long)string.length() > limit) {
            throw new VerifyException(message);
        }
    }

    public static void lenthLimit(String string, long limit) {
        lenthLimit(string, limit, "[Verify failed] - the string length is out of limit.");
    }

    public static void fixedLength(Object value, long length, String message) {
        if (value == null) {
            throw new VerifyException(message);
        } else if (StringHelper.isEmpty(message)) {
            fixedLength(value, length);
        } else {
            Class<?> type = value.getClass();
            if (Collection.class.isAssignableFrom(type)) {
                fixedLength((Collection)Collection.class.cast(value), length, message);
            } else if (type.isArray()) {
                fixedLength((Object[])((Object[])value), length, message);
            } else if (String.class.isAssignableFrom(type)) {
                fixedLength((String)value, length, message);
            } else {
            }

        }
    }

    public static void fixedLength(Object value, long length) {
        fixedLength(value, length, String.format("[Verify failed] - the object size must be %d.", length));
    }

    public static void fixedLength(Collection<?> collection, long length, String message) {
        if (StringHelper.isEmpty(message)) {
            fixedLength(collection, length);
        } else if (collection == null || (long)collection.size() != length) {
            throw new VerifyException(message);
        }
    }

    public static void fixedLength(Collection<?> collection, long length) {
        fixedLength(collection, length, String.format("[Verify failed] - the collection size must be %d.", length));
    }

    public static void fixedLength(Object[] array, long length, String message) {
        if (StringHelper.isEmpty(message)) {
            fixedLength(array, length);
        } else if (array == null || (long)array.length != length) {
            throw new VerifyException(message);
        }
    }

    public static void fixedLength(Object[] array, long length) {
        fixedLength(array, length, String.format("[Verify failed] - the array size must be %d.", length));
    }

    public static void fixedLength(String string, long length, String message) {
        if (StringHelper.isEmpty(message)) {
            fixedLength(string, length);
        } else if (string == null || (long)string.length() != length) {
            throw new VerifyException(message);
        }
    }

    public static void fixedLength(String string, long length) {
        fixedLength(string, length, String.format("[Verify failed] - the string length must be %d.", length));
    }

    public static void largerThan(Object src, Number compare, String message) {
        if (!(src instanceof Number)) {
            throw new VerifyException(message);
        } else {
            largerThan((Number)src, compare, message);
        }
    }

    public static void largerThan(Number src, Number compare, String message) {
        if (src.doubleValue() <= compare.doubleValue()) {
            throw new VerifyException(message);
        }
    }

    public static void largerThan(Number src, Number compare) {
        largerThan(src, compare, String.format("[Verify failed] - provided number must be larger than %s, but it is %s", compare, src));
    }

    public static void notLargerThan(Number src, Number compare, String message) {
        if (src.doubleValue() > compare.doubleValue()) {
            throw new VerifyException(message);
        }
    }

    public static void notLargerThan(Number src, Number compare) {
        notLargerThan(src, compare, String.format("[Verify failed] - provided number must not be larger than %s, but it is %s", compare, src));
    }

    public static void lessThan(Number src, Number compare, String message) {
        if (src.doubleValue() >= compare.doubleValue()) {
            throw new VerifyException(message);
        }
    }

    public static void lessThan(Object src, Number compare, String message) {
        if (!(src instanceof Number)) {
            throw new VerifyException(message);
        } else {
            lessThan((Number)src, compare, message);
        }
    }

    public static void lessThan(Number src, Number compare) {
        lessThan((Number)src.doubleValue(), compare.doubleValue(), String.format("[Verify failed] - provided number must be less than %s, but it is %s", compare, src));
    }

    public static void notLessThan(Number src, Number compare, String message) {
        if (src.doubleValue() < compare.doubleValue()) {
            throw new VerifyException(message);
        }
    }

    public static void notLessThan(Number src, Number compare) {
        notLessThan(src, compare, String.format("[Verify failed] - provided number must not be less than %s, but it is %s", compare, src));
    }

    public static void assertEquals(Object src, Object compare) {
        assertEquals(src, compare, String.format("[Verify failed] - provided object must be equated with %s, but it is %s", compare, src));
    }

    public static void assertEquals(Object src, Object compare, String message) {
        if (src == null || !src.equals(compare)) {
            throw new VerifyException(message);
        }
    }

    public static void assertNotEquals(Object src, Object compare) {
        assertNotEquals(src, compare, String.format("[Verify failed] - provided object must not be equated with %s, but it is %s", compare, src));
    }

    public static void assertNotEquals(Object src, Object compare, String message) {
        if (src == null && compare != null || src != null && src.equals(compare)) {
            throw new VerifyException(message);
        }
    }

    public static void isMatch(Object src, String regex) {
        isMatch(src, regex, String.format("[Verify failed] - String '%s' dose not match regex '%s'", src, regex));
    }

    public static void isMatch(Object src, String regex, String message) {
        if (!(src instanceof String) || !StringHelper.isMatch((String)src, regex)) {
            throw new VerifyException(message);
        }
    }
}
