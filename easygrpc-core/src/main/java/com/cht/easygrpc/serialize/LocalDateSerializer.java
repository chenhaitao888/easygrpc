package com.cht.easygrpc.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author : chenhaitao934
 * @date : 1:43 下午 2020/10/9
 */
public class LocalDateSerializer extends JsonSerializer<LocalDate> {

    public LocalDateSerializer() {
    }

    public void serialize(LocalDate date, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(DateTimeFormatter.ISO_LOCAL_DATE.format(date));
    }
}
