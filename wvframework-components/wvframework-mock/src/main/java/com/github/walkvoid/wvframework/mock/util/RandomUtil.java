package com.github.walkvoid.wvframework.mock.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 随机数据生成工具类
 *
 * @author walkvoid
 */
public class RandomUtil {

    private static final Random RANDOM = new Random();
    private static final String CHARSET_ALPHA = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String CHARSET_NUMERIC = "0123456789";
    private static final String CHARSET_ALPHANUMERIC = CHARSET_ALPHA + CHARSET_NUMERIC;

    private RandomUtil() {
    }

    /**
     * 生成指定范围内的随机整数
     */
    public static int nextInt(int min, int max) {
        return min + RANDOM.nextInt(max - min + 1);
    }

    /**
     * 生成指定范围内的随机长整数
     */
    public static long nextLong(long min, long max) {
        return min + (long) (RANDOM.nextDouble() * (max - min + 1));
    }

    /**
     * 生成随机浮点数
     */
    public static double nextDouble(double min, double max) {
        return min + RANDOM.nextDouble() * (max - min);
    }

    /**
     * 生成指定小数位数的随机浮点数
     */
    public static BigDecimal nextDecimal(long min, long max, int decimals) {
        double value = nextDouble(min, max);
        return BigDecimal.valueOf(value).setScale(decimals, RoundingMode.HALF_UP);
    }

    /**
     * 生成随机字符串
     *
     * @param length  字符串长度
     * @param charset 字符集：alpha、numeric、alphanumeric
     */
    public static String nextString(int length, String charset) {
        String chars;
        switch (charset.toLowerCase()) {
            case "alpha":
                chars = CHARSET_ALPHA;
                break;
            case "numeric":
                chars = CHARSET_NUMERIC;
                break;
            case "alphanumeric":
            default:
                chars = CHARSET_ALPHANUMERIC;
                break;
        }

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 从数组中随机选择一个元素
     */
    @SafeVarargs
    public static <T> T random(T... array) {
        if (array == null || array.length == 0) {
            return null;
        }
        return array[RANDOM.nextInt(array.length)];
    }

    /**
     * 从列表中随机选择一个元素
     */
    public static <T> T random(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(RANDOM.nextInt(list.size()));
    }

    /**
     * 从逗号分隔的字符串中随机选择一个
     */
    public static String randomFromString(String commaSeparated) {
        if (commaSeparated == null || commaSeparated.isEmpty()) {
            return "";
        }
        String[] parts = commaSeparated.split(",");
        return parts[RANDOM.nextInt(parts.length)].trim();
    }

    /**
     * 生成随机日期
     */
    public static LocalDate nextDate(LocalDate from, LocalDate to) {
        long fromEpoch = from.toEpochDay();
        long toEpoch = to.toEpochDay();
        long randomEpoch = nextLong(fromEpoch, toEpoch);
        return LocalDate.ofEpochDay(randomEpoch);
    }

    /**
     * 生成随机日期时间
     */
    public static LocalDateTime nextDateTime(LocalDateTime from, LocalDateTime to) {
        long fromEpoch = from.toEpochSecond(java.time.ZoneOffset.UTC);
        long toEpoch = to.toEpochSecond(java.time.ZoneOffset.UTC);
        long randomEpoch = nextLong(fromEpoch, toEpoch);
        return LocalDateTime.ofEpochSecond(randomEpoch, 0, java.time.ZoneOffset.UTC);
    }

    /**
     * 格式化日期
     */
    public static String formatDate(LocalDate date, String pattern) {
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 格式化日期时间
     */
    public static String formatDateTime(LocalDateTime dateTime, String pattern) {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 生成UUID
     */
    public static String uuid() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 随机布尔值
     */
    public static boolean nextBoolean() {
        return RANDOM.nextBoolean();
    }

    /**
     * 生成随机字节数组
     */
    public static byte[] nextBytes(int length) {
        byte[] bytes = new byte[length];
        RANDOM.nextBytes(bytes);
        return bytes;
    }
}
