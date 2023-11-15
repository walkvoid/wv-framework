package com.wvframework.annotations.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.wvframework.annotations.BaseEnum;

import java.io.IOException;
import java.util.ServiceLoader;

/**
 * @author jiangjunqing
 * @version v0.0.1
 * @date 2023/9/17
 * @desc
 */
public class BaseEnumSerializer extends JsonSerializer<Object> implements ContextualSerializer {

    private BaseEnum baseEnum;

    public BaseEnumSerializer() {}

    public BaseEnumSerializer(BaseEnum baseEnum) {
        this.baseEnum = baseEnum;
    }

    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {

    }

    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        return null;
    }



}
