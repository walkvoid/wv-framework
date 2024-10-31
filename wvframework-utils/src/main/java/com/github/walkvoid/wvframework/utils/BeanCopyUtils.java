package com.github.walkvoid.wvframework.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;

import java.util.*;

/**
 * @author walkvoid
 * @desc bean属性复制工具类
 */
public class BeanCopyUtils {
    private static final Log logger = LogFactory.getLog(BeanCopyUtils.class);

    /**
     * 复制一个普通的bean
     * @param source 源实例
     * @param targetType 目标类型
     * @return
     * @param <T>
     */
    public static <T> T copyBean(Object source, Class<T> targetType){
        if (source == null) {
            return null;
        }
        if (targetType == null) {
            throw new RuntimeException("targetType must not be null");
        }
        T t = BeanUtils.instantiateClass(targetType);
        BeanUtils.copyProperties(source, t);
        return t;
    }

    /**
     * copy list
     * @param source
     * @param targetType
     * @return
     * @param <T>
     */
    public static <T> List<T> copyList(List<?> source, Class<T> targetType) {
        if (CollectionUtils.isEmpty(source)) {
            return CollectionUtils.newArrayList();
        }
        if (targetType == null) {
            throw new RuntimeException("targetType must not be null");
        }
        ArrayList<T> arrayList = CollectionUtils.newArrayList();
        for (Object item : source) {
            T t = BeanUtils.instantiateClass(targetType);
            BeanUtils.copyProperties(item, t);
            arrayList.add(t);
        }
        return arrayList;
    }



}
