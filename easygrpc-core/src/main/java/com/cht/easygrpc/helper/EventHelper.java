package com.cht.easygrpc.helper;

import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;

/**
 * @author : chenhaitao934
 * @date : 2:20 下午 2020/10/12
 */
public interface EventHelper {

    static boolean addEvent(PathChildrenCacheEvent event) {
        return event != null && event.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED;
    }

    static boolean updateEvent(PathChildrenCacheEvent event) {
        return event != null && event.getType() == PathChildrenCacheEvent.Type.CHILD_UPDATED;
    }

    static boolean removeEvent(PathChildrenCacheEvent event) {
        return event != null && event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED;
    }

    static boolean modifyEvent(PathChildrenCacheEvent event) {
        return addEvent(event) || removeEvent(event) || updateEvent(event);
    }
}
