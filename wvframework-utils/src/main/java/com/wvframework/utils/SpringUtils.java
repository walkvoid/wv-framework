package com.wvframework.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author jiangjunqing
 * @version v0.0.1
 * @date 2023/9/17
 * @desc
 */
public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * 从获取给定class类型的bean
     * @param requireType
     * @return
     * @param <T>
     */
    public static <T> T getBean(Class<T> requireType) {
        return applicationContext.getBean(requireType);
    }

    /**
     * 判断当前环境是否是spring容器环境
     * @return
     */
    public static boolean currentIsSpringEnvironment(){
        return  SpringUtils.applicationContext != null;
    }

    /**
     * 判断当前环境是否是spring web环境
     * @return
     */
    public static boolean currentIsSpringWebEnvironment(){
        return  SpringUtils.applicationContext != null;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        SpringUtils.applicationContext = applicationContext;
    }

}
