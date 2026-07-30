package com.github.walkvoid.wvframework.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 随机数据生成工具类
 *
 * @author walkvoid
 */
public class RandomUtils {

    private static final Random RANDOM = new Random();
    private static final String CHARSET_ALPHA = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String CHARSET_NUMERIC = "0123456789";
    private static final String CHARSET_ALPHANUMERIC = CHARSET_ALPHA + CHARSET_NUMERIC;

    private static final Map<String, String> CHAR_SET_MAP = new HashMap<>();

    private static final Pattern RULE_PATTERN = Pattern.compile(
            "@(\\w+)\\[([^\\]]+)\\]|\\{([^}]+)}\\[([^\\]]+)\\]"
    );

    private RandomUtils() {

    }

    static {
        CHAR_SET_MAP.put("digits", "0123456789");
        CHAR_SET_MAP.put("upper", "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        CHAR_SET_MAP.put("lower", "abcdefghijklmnopqrstuvwxyz");
        CHAR_SET_MAP.put("letters", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        CHAR_SET_MAP.put("alnum", "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        CHAR_SET_MAP.put("space", " ");
        CHAR_SET_MAP.put("hyphen", "-");
        CHAR_SET_MAP.put("underscore", "_");
    }


    /**
     * 从规则表达式中生成随机字符串
     * @digits[4]表示从0123456789取出四个随机的数字
     * @upper[0-3]表示从ABCDEFGHIJKLMNOPQRSTUVWXYZ取出0个或者3个字母，@表示需要从map找到对应的key，可以是0个，1个，2个或者3个
     * @alnum[2-10]表示从0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ取出2个到10个字母，@表示需要从map找到对应的key
     * {com,cn}[1],表示从com或者cn随机取出一个
     * @space[4]表示4个空格字符
     * @hyphen[1-2]表示1到2个横杠字符
     *
     * @param rule 规则表达式
     * @return 生成的随机字符串
     */
    public static String fromRule(String rule) {
        if (rule == null || rule.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        Matcher matcher = RULE_PATTERN.matcher(rule);

        while (matcher.find()) {
            String keyword = matcher.group(1);   // @digits → "digits"
            String countOrRange = matcher.group(2);
            String options = matcher.group(3);   // com,cn
            String optCount = matcher.group(4);

            if (keyword != null) {
                String charset = CHAR_SET_MAP.get(keyword);
                if (charset != null) {
                    result.append(pickFromCharset(charset, countOrRange));
                }
            } else if (options != null) {
                result.append(pickFromOptions(options, optCount));
            }
        }

        return result.toString();
    }

    /**
     * 解析 [N] 或 [min-max] 得到实际取值的数量
     */
    public static int parseCount(String spec) {
        int dashIdx = spec.indexOf('-');
        if (dashIdx > 0) {
            int min = Integer.parseInt(spec.substring(0, dashIdx).trim());
            int max = Integer.parseInt(spec.substring(dashIdx + 1).trim());
            return nextInt(min, max);
        }
        return Integer.parseInt(spec.trim());
    }

    /**
     * 从字符集中随机取出指定数量的字符
     */
    private static String pickFromCharset(String charset, String countOrRange) {
        int count = parseCount(countOrRange);
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            sb.append(charset.charAt(RANDOM.nextInt(charset.length())));
        }
        return sb.toString();
    }

    /**
     * 从逗号分隔的候选项中随机选取指定次数
     */
    private static String pickFromOptions(String optionsStr, String countOrRange) {
        String[] options = optionsStr.split(",");
        int count = parseCount(countOrRange);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(options[RANDOM.nextInt(options.length)].trim());
        }
        return sb.toString();
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
