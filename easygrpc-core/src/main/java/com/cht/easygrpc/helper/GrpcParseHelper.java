package com.cht.easygrpc.helper;

import com.cht.easygrpc.domain.MethodInfo;
import com.cht.easygrpc.domain.ParamInfo;
import com.cht.easygrpc.exception.EasyGrpcException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : chenhaitao934
 */
public class GrpcParseHelper {
    private static Map<String, JavaType> returnTypeMap = new ConcurrentHashMap<>();

    public static String genArgJsons(Object[] args) {
        if (args == null) {
            return "";
        }
        try {
            Map<String, String> argJsons = new HashMap<>();
            for (int i = 0; i < args.length; i++) {
                argJsons.put(String.format("arg%d", i), JacksonHelper.getMapper().writeValueAsString(args[i]));
            }
            return JacksonHelper.getMapper().writeValueAsString(argJsons);
        } catch (JsonProcessingException e) {
            throw new EasyGrpcException("serialize args failure", e);
        }
    }

    public static Map<String, Object> parseArgs(String argsJson, MethodInfo methodInfo) {
        try {
            Map<String, Object> args = methodInfo.getDefaultArgs();
            Map<String, String> argsJsonMap = parseArgsJson(argsJson);
            if (argsJsonMap == null) {
                return args;
            }
            Map<String, ParamInfo> paramInfos = methodInfo.getParamInfos();
            for (Map.Entry<String, String> entry : argsJsonMap.entrySet()) {
                ParamInfo paramInfo = paramInfos.get(entry.getKey());
                if (paramInfo == null) {
                    throw new EasyGrpcException("Unknown Parameter(" + entry.getKey() + ") Found in Method(" + methodInfo.getMethod().getName() + ")!");
                }
                args.put(entry.getKey(), JacksonHelper.getMapper().readValue(entry.getValue(), paramInfo.getJavaType()));
            }

            return args;
        } catch (IOException e) {
            throw new IllegalArgumentException("parse args failure!", e);
        }
    }

    private static Map<String, String> parseArgsJson(String argsJson) throws IOException {
        if (StringHelper.isEmpty(argsJson)) {
            return null;
        }
        return JacksonHelper.getMapper().readValue(argsJson, new TypeReference<HashMap<String, String>>() {
        });
    }

    public static <T> T parseResult(String resultJson, String serviceName, Method method) {

        if (resultJson == null) {
            return null;
        }

        Class<?> clazz = method.getReturnType();
        if (clazz == void.class || clazz == Void.class) {
            return null;
        }

        try {
            JavaType returnJavaType = JsonClientHelper.getReturnJavaType(serviceName, method);
            return JacksonHelper.getMapper().readValue(resultJson, returnJavaType);
        } catch (IOException e) {
            throw new EasyGrpcException("parse result failure", e);
        }
    }


    public static <T> T parseResult(String resultJson, String iface, String method, Type returnType) {
        if (resultJson == null) {
            return null;
        }

        if (returnType == void.class || returnType == Void.class || returnType == null) {
            return null;
        }

        String key = getIfaceMethodKey(iface, method);
        try {
            JavaType javaType = returnTypeMap.get(key);
            if (javaType == null) {
                javaType = JacksonHelper.genJavaType(returnType);
                returnTypeMap.put(key, javaType);
            }

            return JacksonHelper.getMapper().readValue(resultJson, javaType);
        } catch (IOException e) {
            throw new EasyGrpcException("parse result failure", e);
        }
    }

    private static String getIfaceMethodKey(String iface, String method) {
        return iface + "_" + method;
    }

    public static <T> T parseResult(String resultJson, TypeReference typeReference) {
        if (resultJson == null) {
            throw new EasyGrpcException("Result Json is Null!");
        }

        try {
            return JacksonHelper.getMapper().readValue(resultJson, typeReference);
        } catch (IOException e) {
            throw new EasyGrpcException("parse result failure", e);
        }
    }
}
