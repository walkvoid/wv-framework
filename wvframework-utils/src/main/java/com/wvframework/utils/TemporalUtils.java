package com.wvframework.utils;

import java.time.*;
import java.time.temporal.Temporal;
import java.util.Comparator;

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



    interface Convertor<S,T> {

        /**
         * source convert to target
         * @param source
         * @return target
         */
        T convert(S source);


    }

    interface Convertor2<S,T> extends Convertor<S,T>{

        /**
         * source convert to target
         * @param source
         * @return target
         */
        T convert(S source, int m);

        default T convert(S source) {
            return convert(source, 1);
        }
    }

    class LocalDateTimeToLocalDateConvertor implements Convertor<LocalDateTime, LocalDate> {

        @Override
        public LocalDate convert(LocalDateTime source) {
            return source.toLocalDate();
        }
    }

    class LocalDateTimeToLocalTimeConvertor implements Convertor<LocalDateTime, LocalTime> {

        @Override
        public LocalTime convert(LocalDateTime source) {
            return null;
        }
    }

    class LocalDateTimeToYearMonthConvertor implements Convertor<LocalDateTime, YearMonth> {

        @Override
        public YearMonth convert(LocalDateTime source) {
            return null;
        }
    }

    class LocalDateTimeToYearConvertor implements Convertor<LocalDateTime, Year> {
        @Override
        public Year convert(LocalDateTime source) {
            return Year.of(source.getYear());
        }
    }

    class LocalDateToLocalDateTimeConvertor implements Convertor<LocalDate, LocalDateTime> {

        @Override
        public LocalDate convert(LocalDateTime source) {
            return source.toLocalDate();
        }
    }

    class LocalDateTimeToLocalTimeConvertor implements Convertor<LocalDate, LocalTime> {

        @Override
        public LocalTime convert(LocalDateTime source) {
            return null;
        }
    }

    class LocalDateTimeToYearMonthConvertor implements Convertor<LocalDate, YearMonth> {

        @Override
        public YearMonth convert(LocalDateTime source) {
            return null;
        }
    }

    class LocalDateTimeToYearConvertor implements Convertor<LocalDate, Year> {
        @Override
        public Year convert(LocalDateTime source) {
            return Year.of(source.getYear());
        }
    }






}
