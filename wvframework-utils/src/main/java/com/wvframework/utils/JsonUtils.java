package com.wvframework.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * @author walkvoid
 * @desc
 */
public class JsonUtils {

    private JsonUtils(){}

    private static ObjectMapper globalObjectMapper;

    private ObjectMapper objectMapper;




    public static String toJson(Object obj) {
        return null;
    }

    public static String toPrettyJson(Object obj) {
        return null;
    }

    public static <T> T parse(String json, Class<T> targetType) {
        return null;
    }

    public static <T> List<T> parseList(String json, Class<T> targetType) {
        return null;
    }


}
