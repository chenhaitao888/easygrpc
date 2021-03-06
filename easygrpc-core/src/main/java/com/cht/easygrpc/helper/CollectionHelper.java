package com.cht.easygrpc.helper;

import java.util.*;
import java.util.function.Predicate;

/**
 * @author : chenhaitao934
 * @date : 8:55 下午 2020/6/20
 */
public class CollectionHelper {
    private CollectionHelper() {
    }

    public static <T> List<T> newArrayListOnNull(List<T> list) {
        if (list == null) {
            list = new ArrayList<T>();
        }
        return list;
    }

    public static <T> Set<T> newHashSetOnNull(Set<T> set) {
        if (set == null) {
            set = new HashSet<T>();
        }
        return set;
    }

    public static <K, V> Map<K, V> newHashMapOnNull(Map<K, V> map) {
        if (map == null) {
            map = new HashMap<K, V>();
        }
        return map;
    }

    public static <T> Set<T> createHashSet(T... arr) {
        int size = arr == null ? 0 : arr.length;
        Set<T> set = new HashSet<T>(size);
        if (arr != null && arr.length > 0) {
            Collections.addAll(set, arr);
        }
        return set;
    }

    public static Map<String, String> toMap(Properties properties) {
        if (properties == null) {
            return new HashMap<String, String>(0);
        }
        Map<String, String> map = new HashMap<String, String>(properties.size());

        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            map.put(entry.getKey().toString(), entry.getValue().toString());
        }
        return map;
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return map != null && map.size() > 0;
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return !isNotEmpty(map);
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return collection != null && collection.size() > 0;
    }

    public static boolean isEmpty(Collection<?> collection) {
        return !isNotEmpty(collection);
    }

    public static <K, V> V getValue(Map<K, V> map, K key) {
        if (map == null) {
            return null;
        }
        return map.get(key);
    }

    public static int sizeOf(Collection<?> collection) {
        if (isEmpty(collection)) {
            return 0;
        }
        return collection.size();
    }

    public static int sizeOf(Map<?, ?> map) {
        if (map == null) {
            return 0;
        }
        return map.size();
    }

    public static <T> List<T> getLeftDiff(List<T> list1, List<T> list2) {
        if (isEmpty(list2)) {
            return list1;
        }
        List<T> list = new ArrayList<T>();
        if (isNotEmpty(list1)) {
            for (T o : list1) {
                if (!list2.contains(o)) {
                    list.add(o);
                }
            }
        }
        return list;
    }

    public static <T> List<T> setToList(Set<T> set) {
        if (set == null) {
            return null;
        }
        return new ArrayList<T>(set);
    }

    public static <T> List<T> arrayToList(T[] t) {
        if (t == null || t.length == 0) {
            return new ArrayList<T>(0);
        }
        List<T> list = new ArrayList<T>(t.length);
        Collections.addAll(list, t);
        return list;
    }

    public static <T> Optional<T> getFirstOptional(Collection<T> collection, Predicate<? super T> predicate) {
        VerifyHelper.notNull(collection);
        VerifyHelper.notNull(predicate);
        return collection.stream()
                .filter(predicate)
                .findFirst();

    }
}
