package com.wvframework.utils;

import org.springframework.format.annotation.DateTimeFormat;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.util.*;

/**
 * @author walkvoid
 * @description: LocalDateTime,LocalDate,... utils
 */
public class TemporalUtils {

    private static Map<String, String> replaceMap;
    private static List<String> patterns;
    private static Map<Class<? extends TemporalAccessor>, TemporalQuery<?>> supportMap;


    private TemporalUtils(){}

    static {
        replaceMap = new HashMap<>(8);
        replaceMap.putIfAbsent("-", "");
        replaceMap.putIfAbsent("/", "");
        replaceMap.putIfAbsent(":", "");
        replaceMap.putIfAbsent("T", " ");
        replaceMap.putIfAbsent(".", "");

        patterns = new ArrayList<>();
        patterns.add("yyyy");
        patterns.add("yyyyMM");
        patterns.add("yyyyMMdd");
        patterns.add("yyyyMMdd HHmmss");
        patterns.add("yyyyMMdd HHmmssSSS");
        patterns.add("HHmmss");
        patterns.add("HHmmssSSS");

        supportMap = new HashMap<>(8);
        supportMap.put(Year.class, (temporal) -> temporal.query(Year::from));
        supportMap.put(YearMonth.class, (temporal) -> temporal.query(YearMonth::from));
        supportMap.put(LocalDate.class, (temporal) -> temporal.query(LocalDate::from));
        supportMap.put(LocalDateTime.class, (temporal) -> temporal.query(LocalDateTime::from));
        supportMap.put(LocalTime.class, (temporal) -> temporal.query(LocalTime::from));
    }




    /**
     * 解析一个字符串,并可以指定期望的类型
     * @param content
     * @param except
     * @param <T>
     * @return
     */
    public static <T extends TemporalAccessor> T parse(String content, Class<T> except) {
        return parse(content, except, CompatibleMode.CURRENT);
    }

    /**
     * 解析一个字符串,并可以指定期望的类型
     * @param content
     * @param except
     * @param <T>
     * @return
     */
    public static <T extends TemporalAccessor> T parse(String content, Class<T> except, CompatibleMode mode) {
        List<String> patterns = deducePattern(content);
        return parse(content, except);
    }

    /**
     * 指定一个时间字符串和格式,返回期望类型的时间
     * @param content
     * @param except
     * @param <T>
     * @return
     */
    public static <T extends TemporalAccessor> T parse(String content, String pattern, Class<T> except) {

        List<String> patterns = deducePattern(content);
        return parse(content, except);
    }

    /**
     * 指定一个时间字符串和格式,返回期望类型的时间
     * @param content
     * @param except
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends TemporalAccessor> T parse(String content, String pattern, Class<T> except, CompatibleMode mode) {
        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("content can not be empty.");
        }
        if (pattern == null || pattern.isEmpty()) {
            throw new IllegalArgumentException("pattern can not be empty.");
        }
        if (except == null) {
            throw new IllegalArgumentException("except can not be null.");
        }
        if (mode == null) {
            throw new IllegalArgumentException("mode can not be null.");
        }
        TemporalAccessor temporalAccessor = DateTimeFormatter.ofPattern(pattern).parse(content);
        if (except.equals(temporalAccessor.getClass())) {
            return (T)temporalAccessor;
        } else {
           return Convertors.convert(temporalAccessor, except, mode);
        }
    }

    private static List<String> deducePattern(String content) {
        return null;
    }

    /**
     * 解析一个字符串,并可以指定日期格式和指定期望的类型
     * @param content
     * @param patterns
     * @param except
     * @param <T>
     * @return
     */
    private static <T extends TemporalAccessor> T parse(String content, List<String> patterns, Class<T> except, CompatibleMode mode) {
        if (content == null || content.isEmpty() || except == null) {
            throw new IllegalArgumentException("content and except class cant not be null.");
        }
        TemporalAccessor parse = null;
//        if (pattern == null || pattern.isEmpty()) {
//
//        } else {
//            parse = DateTimeFormatter.ofPattern(pattern).parse(content);
//        }

        if (parse == null) {
            throw new DateTimeException("parse " + content + " fail, parse result is null.");
        }

        if (except.equals(parse.getClass())) {
            return (T)parse;
        }
        return Convertors.convert(parse, except, mode);
    }

    /**
     * 将一个long型的时间戳解析成时间格式
     * @param epochSecond
     * @param except
     * @param <T>
     * @return
     */
    public static <T extends TemporalAccessor> T ofSeconds(long epochSecond, Class<T> except) {
        return null;
    }



    @SuppressWarnings("unchecked")
    protected static class Convertors {
        static List<Convertor<?,?>> convertors = new ArrayList<>();
        static {
            registerConvertors();
        }
        static <T extends TemporalAccessor> T convert(TemporalAccessor source, Class<T> except, CompatibleMode mode) {

            //find matched Convertor
            @SuppressWarnings("unchecked")
            Convertor matched = null;
            for (Convertor<? ,?> convertor : convertors) {
                Type[] genericInterfaces =  convertor.getClass().getGenericInterfaces();
                Type[] actualTypeArguments = ((ParameterizedType) genericInterfaces[0]).getActualTypeArguments();
                Class sourceClass = (Class) actualTypeArguments[0];
                Class targetClass = (Class) actualTypeArguments[1];
                if (source.getClass().equals(sourceClass) && targetClass.equals(except)) {
                    matched = convertor;
                    break;
                }
            }
            if (matched == null) {
                throw new RuntimeException("cant not found convertor: "+source.getClass().getSimpleName()+" -> " + except.getSimpleName());
            }
            //convert
            if (mode != null && matched instanceof CompatibleConvertor) {
                return (T)((CompatibleConvertor) matched).convert(source,mode);

            } else {
                return (T) matched.convert(source);
            }
        }

        private static void registerConvertors() {
            convertors.add(new LocalDateTimeToLocalDateConvertor());
            convertors.add(new LocalDateTimeToLocalTimeConvertor());
            convertors.add(new LocalDateTimeToYearMonthConvertor());
            convertors.add(new LocalDateTimeToYearConvertor());
            convertors.add(new LocalDateToLocalDateTimeConvertor());
            convertors.add(new LocalDateToYearMonthConvertor());
            convertors.add(new LocalDateToYearConvertor());
            convertors.add(new YearMonthToLocalDateTimeConvertor());
            convertors.add(new YearMonthToLocalDateConvertor());
            convertors.add(new YearMonthToYearConvertor());
        }
    }

    interface Convertor<S extends TemporalAccessor,T extends TemporalAccessor> {

        /**
         * source convert to target
         * @param source
         * @return target
         */
        T convert(S source);

    }

    interface CompatibleConvertor<S extends TemporalAccessor,T extends TemporalAccessor> extends Convertor<S,T>{

        /**
         * source convert to target
         * @param source
         * @return target
         */
        T convert(S source, CompatibleMode mode);

        /**
         * default convert(S source)
         * @param source
         * @return
         */
        default T convert(S source) {
            return convert(source, CompatibleMode.CURRENT);
        }
    }

    /**
     * LocalDate -> LocalDateTime
     * MIN: 2024-09-05 -> 2024-09-05 00:00:00
     * CURRENT: 2024-09-05 -> 2024-09-05 10:11:37
     * MAX: 2024-09-05 -> 2024-09-05 23:59:59
     */
    enum CompatibleMode {
        MIN,
        CURRENT,
        MAX,
    }

    protected static class LocalDateTimeToLocalDateConvertor implements Convertor<LocalDateTime, LocalDate> {

        @Override
        public LocalDate convert(LocalDateTime source) {
            return source.toLocalDate();
        }
    }

    protected static class LocalDateTimeToLocalTimeConvertor implements Convertor<LocalDateTime, LocalTime> {

        @Override
        public LocalTime convert(LocalDateTime source) {
            return source.toLocalTime();
        }
    }

    protected static class LocalDateTimeToYearMonthConvertor implements Convertor<LocalDateTime, YearMonth> {

        @Override
        public YearMonth convert(LocalDateTime source) {
            return YearMonth.of(source.getYear(), source.getMonth());
        }
    }

    protected static class LocalDateTimeToYearConvertor implements Convertor<LocalDateTime, Year> {
        @Override
        public Year convert(LocalDateTime source) {
            return Year.of(source.getYear());
        }
    }

    protected static class LocalDateToLocalDateTimeConvertor implements CompatibleConvertor<LocalDate, LocalDateTime> {

        @Override
        public LocalDateTime convert(LocalDate source, CompatibleMode mode) {
            if (CompatibleMode.CURRENT.equals(mode)) {
                return source.atTime(LocalTime.now());
            }
            if (CompatibleMode.MIN.equals(mode)) {
                return source.atTime(LocalTime.MIN);
            }

            if (CompatibleMode.MAX.equals(mode)) {
                return source.atTime(LocalTime.MAX);
            }
            return null;
        }
    }


    protected static class LocalDateToYearMonthConvertor implements Convertor<LocalDate, YearMonth> {
        @Override
        public YearMonth convert(LocalDate source) {
            return YearMonth.of(source.getYear(), source.getMonth());
        }
    }

    protected static class LocalDateToYearConvertor implements Convertor<LocalDate, Year> {
        @Override
        public Year convert(LocalDate source) {
            return Year.of(source.getYear());
        }
    }

    protected static class YearMonthToLocalDateTimeConvertor implements CompatibleConvertor<YearMonth, LocalDateTime> {

        @Override
        public LocalDateTime convert(YearMonth source, CompatibleMode mode) {
            if (CompatibleMode.CURRENT.equals(mode)) {
                LocalDateTime now = LocalDateTime.now();
                return LocalDateTime.of(source.getYear(), source.getMonth(), now.getDayOfMonth(), now.getHour(), now.getMinute(), now.getSecond(), now.getNano());
            }
            if (CompatibleMode.MIN.equals(mode)) {
                return LocalDateTime.of(source.getYear(), source.getMonth(), 1, 0, 0, 0, 0);
            }
            if (CompatibleMode.MAX.equals(mode)) {
                source.getMonth().maxLength();
                return LocalDateTime.of(source.getYear(), source.getMonth(),  source.getMonth().maxLength(), 23, 59, 59, 999999999);
            }
            return null;
        }
    }

    protected static class YearMonthToLocalDateConvertor implements CompatibleConvertor<YearMonth, LocalDate> {

        @Override
        public LocalDate convert(YearMonth source, CompatibleMode mode) {
            if (CompatibleMode.CURRENT.equals(mode)) {
                LocalDateTime now = LocalDateTime.now();
                return LocalDate.of(source.getYear(), source.getMonth(), now.getDayOfMonth());
            }
            if (CompatibleMode.MIN.equals(mode)) {
                return LocalDate.of(source.getYear(), source.getMonth(), 1);
            }
            if (CompatibleMode.MAX.equals(mode)) {
                source.getMonth().maxLength();
                return LocalDate.of(source.getYear(), source.getMonth(),  source.getMonth().maxLength());
            }
            return null;
        }
    }

    protected static class YearMonthToYearConvertor implements Convertor<YearMonth, Year> {
        @Override
        public Year convert(YearMonth source) {
            return Year.of(source.getYear());
        }
    }


    public static void main(String[] args) {
//        OffsetDateTime now = OffsetDateTime.now();
//        System.out.println(now);
//        //20240905135436283
//        TemporalAccessor yyyyMM = DateTimeFormatter.ofPattern("yyyyMMdd HHmmssSSS").parse("20240905 135436283");
//        System.out.println(yyyyMM);
//        Arrays.asList("yyyy","yyyyMM","yyyyMMdd","yyyyMMdd HHmmss","yyyyMMdd HHmmssSSS");
//        HashMap<String, String> map = new HashMap<>();
//        map.putIfAbsent("-", "");
//        map.putIfAbsent("/", "");
//        map.putIfAbsent(":", "");
//        map.putIfAbsent("T", " ");
//        map.putIfAbsent(".", "");
//        String xx = "2024-09-05T14:13:06.223";
//        map.entrySet().stream().forEach(entry-> xx.replace(entry.getKey(), entry.getValue()));

        //LocalDateTime parse = parse("2024-09-09", "yyyy-MM-dd", LocalDateTime.class, CompatibleMode.CURRENT);

        TemporalAccessor parse1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").parse("2024-09-09 19:07:40");
        //TemporalAccessor parse1 = DateTimeFormatter.ofPattern("HH:mm:ss").parse("19:07:40");

        //parse1.isSupported()
        YearMonth query = (YearMonth) parse1.query(supportMap.get(YearMonth.class));
        System.out.println(query);

    }

}
