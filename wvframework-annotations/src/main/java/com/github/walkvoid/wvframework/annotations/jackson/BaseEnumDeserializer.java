package com.github.walkvoid.wvframework.annotations.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.github.walkvoid.wvframework.annotations.BaseEnum;

import java.io.IOException;

/**
 * @author jiangjunqing
 * @version v0.0.1
 * @date 2023/9/17
 * @desc
 */
public class BaseEnumDeserializer extends JsonDeserializer<Number> implements ContextualDeserializer {

    private BaseEnum baseEnum;

    public BaseEnumDeserializer() {}

    public BaseEnumDeserializer(BaseEnum baseEnum) {
        this.baseEnum = baseEnum;
    }

    public Number deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        return null;
    }

    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        return null;
    }
}
