package com.cht.easygrpc.registry;

import com.cht.easygrpc.exception.UnknownGenericTypeException;
import com.cht.easygrpc.helper.JsonHelper;
import org.apache.curator.framework.recipes.cache.ChildData;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author : chenhaitao934
 * @date : 11:59 下午 2020/10/11
 */
public abstract class AbstractGenericNode<T extends AbstractGenericNode, E extends AbstractNodeData<E>> extends Node{
    private E nodeData;

    public AbstractGenericNode(ChildData childData) {
        this(childData.getPath(), childData.getData());
    }

    public AbstractGenericNode(String path, byte[] bytes) {
        this.path = path;
        this.nodeData = JsonHelper.fromJson(bytes, getGenericType());
    }

    public AbstractGenericNode(String path, E nodeData) {
        this.path = path;
        this.nodeData = nodeData;
    }

    private Class<E> getGenericType() {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return (Class<E>) parameterizedType.getActualTypeArguments()[1];
        }
        throw new UnknownGenericTypeException();
    }


    @Override
    public String toString() {
        return "AbstractGenericNode{" +
                "nodeData=" + JsonHelper.toJson(nodeData) +
                ", path='" + path + '\'' +
                '}';
    }
}
