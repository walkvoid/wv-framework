package com.wvframework.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.Temporal;

/**
 * @author walkvoid
 * @description: LocalDateTime,LocalDate,... utils
 */
public class TemporalUtils {

    private TemporalUtils(){}


    /**
     * 解析一个时间字符串
     * @param content
     * @return
     */
    public static Temporal parse(String content) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZoneOffset of = ZoneOffset.of(zoneId.getId());

        return null;
    }

    /**
     * 解析一个字符串,并可以指定期望的类型
     * @param content
     * @param except
     * @param <T>
     * @return
     */
    public static <T extends Temporal> T parse(String content, Class<T> except) {
        return null;
    }

    /**
     * 解析一个字符串,并可以指定日期格式和指定期望的类型
     * @param content
     * @param pattern
     * @param except
     * @param <T>
     * @return
     */
    public static <T extends Temporal> T parse(String content, String pattern, Class<T> except) {
        return null;
    }

    /**
     * 将一个long型的时间戳解析成时间格式
     * @param epochSecond
     * @param except
     * @param <T>
     * @return
     */
    public static <T extends Temporal> T ofSeconds(long epochSecond, Class<T> except) {
        return null;
    }


}
