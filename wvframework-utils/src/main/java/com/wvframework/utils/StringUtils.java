package com.wvframework.utils;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author walkvoid
 * @desc
 */
public class StringUtils {

    private StringUtils(){}

    /**
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str){
        return !isEmpty(str);
    }

    /**
     *
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        return !isNotBlank(str);
    }

    /**
     *
     * @param str
     * @return
     */
    public static boolean isNotBlank(String str) {
        if (str != null) {
            int strLen = str.length();
            for (int i = 0; i < strLen; i++) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *
     * @param str
     * @return
     */
    private static boolean containsText(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }


    /**
     *
     * @param str
     * @param regex
     * @return
     */
    public static List<String> splitToList(String str, String regex){
        if (isEmpty(str)) {
            return CollectionUtils.newArrayList();
        }
        String[] split = str.split(regex);
        return CollectionUtils.newArrayList(split);
    }

    /**
     *
     * @param collection
     * @param delimiter
     * @return
     */
    public static String joinToString(Collection<String> collection, String delimiter){
        if (CollectionUtils.isEmpty(collection)) {
            return "";
        }
        return String.join(delimiter, collection);
    }

    /**
     * Collection Element String Field Join a string
     * @param collection
     * @param mapper
     * @param delimiter
     * @return
     * @param <E>
     */
    public static <E> String joinToString(Collection<E> collection, Function<E, String> mapper, String delimiter) {
        if (CollectionUtils.isEmpty(collection)) {
            return "";
        }
        return collection.stream().map(mapper).collect(Collectors.joining(delimiter));
    }

}
