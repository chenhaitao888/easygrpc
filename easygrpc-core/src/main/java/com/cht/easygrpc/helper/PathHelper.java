package com.cht.easygrpc.helper;

/**
 * @author : chenhaitao934
 */
public interface PathHelper {

    static String getParentPath(String path) {
        Assert.notNull(path, "path can't be null.");
        int index = path.lastIndexOf("/");
        if (index < 0) {
            return path;
        }
        return path.substring(0, index);
    }
}
