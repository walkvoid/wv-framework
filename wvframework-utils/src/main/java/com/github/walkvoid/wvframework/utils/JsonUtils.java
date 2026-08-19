package com.github.walkvoid.wvframework.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.github.walkvoid.wvframework.models.TimePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
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

    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);

    /** {@code spring.jackson.date-format} and legacy {@code java.util.Date}. */
    public static final String LEGACY_DATE_FORMAT = TimePattern.P1;

    /** {@code wv.jackson.local-date-time-format}. */
    public static final String LOCAL_DATE_TIME_FORMAT = TimePattern.P1;

    /** {@code wv.jackson.local-date-format}. */
    public static final String LOCAL_DATE_FORMAT = TimePattern.P2;

    private static final ObjectMapper STATIC_OBJECT_MAPPER = createObjectMapper();

    private JsonUtils() {
    }

    /**
     * Returns the Spring-managed {@link ObjectMapper} when running inside a container;
     * otherwise the statically configured fallback instance.
     */
    public static ObjectMapper getObjectMapper() {
        if (SpringUtils.currentIsSpringEnvironment()) {
            try {
                return SpringUtils.getBean(ObjectMapper.class);
            } catch (RuntimeException ex) {
                log.warn("Use static ObjectMapper because Spring ObjectMapper is unavailable: {}",
                        ex.getMessage());
            }
        }
        return STATIC_OBJECT_MAPPER;
    }

    public static JavaTimeModule createJavaTimeModule(String dateTimePattern, String datePattern) {
        DateTimeFormatter dateTime = DateTimeFormatter.ofPattern(dateTimePattern);
        DateTimeFormatter date = DateTimeFormatter.ofPattern(datePattern);
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTime));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTime));
        module.addSerializer(LocalDate.class, new LocalDateSerializer(date));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(date));
        return module;
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        apply(mapper);
        return mapper;
    }

    private static void apply(ObjectMapper mapper) {
        mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        mapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        mapper.setDateFormat(new SimpleDateFormat(LEGACY_DATE_FORMAT));
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(createJavaTimeModule(LOCAL_DATE_TIME_FORMAT, LOCAL_DATE_FORMAT));
        mapper.registerModule(new SimpleModule());
    }

    public static String object2json(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            ObjectMapper mapper = getObjectMapper();
            return (obj instanceof String) ? (String) obj : mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("obj to json fail.", e);
        }
    }

    public static String object2jsonPretty(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            ObjectMapper mapper = getObjectMapper();
            return (obj instanceof String) ? (String) obj
                    : mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("obj to json fail.", e);
        }
    }

    public static <T> T json2object(String json, Class<T> clazz) {
        if (json == null || "".equals(json)) {
            return null;
        }
        try {
            return getObjectMapper().readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("json to obj fail.", e);
        }
    }

    public static <T> List<T> json2list(String json, Class<T> clazz) {
        if (json == null || "".equals(json)) {
            return null;
        }
        try {
            return getObjectMapper().readValue(json, new TypeReference<List<T>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("json to array fail.", e);
        }
    }
}
