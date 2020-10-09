package com.cht.easygrpc.helper;

/**
 * @author : chenhaitao934
 * @date : 2:58 下午 2020/10/9
 */
public class ClassHelper {

    /** The package separator character '.' */
    private static final char PACKAGE_SEPARATOR = '.';

    /** The path separator character '/' */
    private static final char PATH_SEPARATOR = '/';

    /** The inner class separator character '$' */
    private static final char INNER_CLASS_SEPARATOR = '$';

    /** The CGLIB class separator character "$$" */
    public static final String CGLIB_CLASS_SEPARATOR = "$$";

    /** The ".class" file suffix */
    public static final String CLASS_FILE_SUFFIX = ".class";

    /** Suffix for array class names: "[]" */
    public static final String ARRAY_SUFFIX = "[]";

    public static String getShortName(Class<?> clazz) {
        return getShortName(getQualifiedName(clazz));
    }

    public static String getQualifiedName(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        if (clazz.isArray()) {
            return getQualifiedNameForArray(clazz);
        }
        else {
            return clazz.getName();
        }
    }

    public static String getShortName(String className) {
        Assert.hasLength(className, "Class name must not be empty");
        int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
        int nameEndIndex = className.indexOf(CGLIB_CLASS_SEPARATOR);
        if (nameEndIndex == -1) {
            nameEndIndex = className.length();
        }
        String shortName = className.substring(lastDotIndex + 1, nameEndIndex);
        shortName = shortName.replace(INNER_CLASS_SEPARATOR, PACKAGE_SEPARATOR);
        return shortName;
    }

    private static String getQualifiedNameForArray(Class<?> clazz) {
        StringBuilder result = new StringBuilder();
        while (clazz.isArray()) {
            clazz = clazz.getComponentType();
            result.append(ARRAY_SUFFIX);
        }
        result.insert(0, clazz.getName());
        return result.toString();
    }

}
