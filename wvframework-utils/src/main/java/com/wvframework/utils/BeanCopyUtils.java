package com.wvframework.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.*;

/**
 * @author jiangjunqing
 * @date 2023/9/18
 * @desc bean属性复制工具类
 *
 */
public class BeanCopyUtils {
    private static final Log logger = LogFactory.getLog(org.springframework.beans.CachedIntrospectionResults.class);

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

        return null;
    }

    public static <T> List<T> copyList(List<?> source, Class<T> targetType){
        return null;
    }



}
