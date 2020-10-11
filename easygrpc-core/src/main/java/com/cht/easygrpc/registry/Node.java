package com.cht.easygrpc.registry;

/**
 * @author : chenhaitao934
 * @date : 2:11 下午 2020/10/10
 */
public abstract class Node {

    protected String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
