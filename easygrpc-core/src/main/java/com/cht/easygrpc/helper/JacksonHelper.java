package com.cht.easygrpc.helper;

import com.cht.easygrpc.serialize.LocalDateDeserializer;
import com.cht.easygrpc.serialize.LocalDateSerializer;
import com.cht.easygrpc.serialize.LocalDateTimeDeserializer;
import com.cht.easygrpc.serialize.LocalDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author : chenhaitao934
 * @date : 1:42 下午 2020/10/9
 */
public class JacksonHelper {

    private static final SimpleModule module = initModule();
    private static final ObjectMapper mapper;
    private static final ObjectMapper prettyMapper;

    public JacksonHelper() {
    }

    private static SimpleModule initModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDate.class, new LocalDateSerializer()).addDeserializer(LocalDate.class, new LocalDateDeserializer());
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer()).addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        return module;
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }

    public static ObjectMapper getPrettyMapper() {
        return prettyMapper;
    }

    public static JavaType genJavaType(Type type) {
        return getMapper().getTypeFactory().constructType(type);
    }

    static {
        mapper = (new ObjectMapper()).registerModule(module).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false).setSerializationInclusion(JsonInclude.Include.NON_NULL);
        prettyMapper = mapper.copy().configure(SerializationFeature.INDENT_OUTPUT, true);
    }
}
