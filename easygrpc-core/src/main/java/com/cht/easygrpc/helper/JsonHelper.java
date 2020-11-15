package com.cht.easygrpc.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * @author : chenhaitao934
 * @date : 12:10 上午 2020/10/12
 */
public class JsonHelper {

    private static final ObjectMapper mapper = new ObjectMapper();


    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            if(json == null){
                return null;
            }
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(byte[] bytes, Class<T> clazz) {

        return fromJson(StringHelper.getString(bytes), clazz);
    }


    public static String toJson(Object object) {
        if (object == null) {
            return "";
        }
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    public static byte[] toBytes(Object object) {
        return StringHelper.getBytes(toJson(object));
    }


}
