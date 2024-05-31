package com.wvframework.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.wvframework.models.TimePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


/**
 * @author jjq
 * @version 1.0
 * @date 2020/8/23
 * @desc https://github.com/timo-reymann/spring-boot-date-and-time-starter.git
 */

public class JsonUtils {
    private final static Logger log = LoggerFactory.getLogger(JsonUtils.class);

    private static ObjectMapper objectMapper = new ObjectMapper();

    private static DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("");


    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        objectMapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        objectMapper.setDateFormat(new SimpleDateFormat(TimePattern.P1));
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern(TimePattern.P1);
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class,new LocalDateTimeSerializer(dateTimeFormat));
        javaTimeModule.addDeserializer(LocalDateTime.class,new LocalDateTimeDeserializer(dateTimeFormat));

        SimpleModule simpleModule = new SimpleModule();
        objectMapper.registerModule(javaTimeModule);
        objectMapper.registerModule(simpleModule);

        //simpleModule.addDeserializer(BaseEnum.class, new BaseEnumDeserializer());
    }

    public static String object2json(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return  (obj instanceof String) ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Parse object to json Pretty error:{}", e.getMessage());
            return null;
        }
    }

    public static String object2jsonPretty(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return  (obj instanceof String) ? (String) obj :
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Parse object to json Pretty error:{}", e.getMessage());
            return null;
        }
    }

    public static <T> T json2object(String json, Class<T> clazz) {
        if (json == null || "".equals(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("read json convert object error:{}", e.getMessage());
            return null;
        }
    }


    public static <T> List<T> json2list(String json, Class<T> clazz) {
        if (json == null || "".equals(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<T>>(){});
        } catch (JsonProcessingException e) {
            log.error("read json convert list error:{}", e.getMessage());
            return null;
        }
    }

    public static <T> String list2json(List<T> list) {
        if (list == null || list.size() == 0) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            log.error("parse list convert json error:{}", e.getMessage());
            return null;
        }
    }


}
