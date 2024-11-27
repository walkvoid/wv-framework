package com.github.walkvoid.wvframework.utils;

import javax.validation.Configuration;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import org.hibernate.validator.BaseHibernateValidatorConfiguration;

import java.util.Set;

/**
 * @author jiangjunqing
 * @date 2024/11/25
 * @description: ValidationUtils的工具类
 * @version:
 */
public class ValidationUtils {


    /**
     * 默认的Validator
     */
    private static volatile Validator validator = null;

    /**
     * 带fast-fail(快速失效)的Validator
     */
    private static volatile Validator validatorFastFail = null;

    private ValidationUtils(){}

    /**
     * 获取基于Hibernate实现的Validator
     * @return
     */
    public static Validator getValidator(){
        return getValidator(false);
    }

    /**
     * 获取快速失效Validator
     * @return
     */
    public static Validator getValidatorFastFail(){
        return getValidator(true);
    }


    /**
     * 获取校验器
     * @param fastFail
     * @return
     */
    private static Validator getValidator(boolean fastFail){
        Validator result = fastFail ? validatorFastFail : validator;
        if (result == null) {
            synchronized (ValidationUtils.class) {
                result = fastFail ? validatorFastFail : validator;
                if (result == null) {
                    Configuration<?> configure = Validation.byDefaultProvider().configure();
                    if (fastFail) {
                        configure.addProperty(BaseHibernateValidatorConfiguration.FAIL_FAST, Boolean.TRUE.toString());
                        result = validatorFastFail = configure.buildValidatorFactory().getValidator();
                    } else {
                        result = validator = configure.buildValidatorFactory().getValidator();
                    }
                }
            }
        }
        return result;
    }




    /**
     * 验证指定分组下的对象实例
     * @param object 需要验证的对象
     * @return
     * @throws ConstraintViolation
     */
    public static void validate(Object object) {
       validate(object, false);
    }

    /**
     * 验证指定分组下的对象实例
     * @param object 需要验证的对象
     * @param fastFail 需要验证的对象
     * @param <T> 泛型
     * @return
     */
    public static <T> void validate(T object, boolean fastFail) {
        validate(object, fastFail, new Class<?>[0]);
    }

    /**
     * 验证指定分组下的对象实例
     * @param object 需要验证的对象
     * @param groups 需要验证的分组
     * @return
     */
    public static void validate(Object object, boolean fastFail, Class<?>... groups) {
        Set<ConstraintViolation<Object>> violationSet = getValidator(fastFail).validate(object, groups);
        if (CollectionUtils.isNotEmpty(violationSet)) {
            throw new ConstraintViolationException(violationSet);
        }
    }

    public static <T> void validateProperty(T object, boolean fastFail, String propertyName, Class<?>... groups) {
        if (groups ==  null) {
            groups = new Class<?>[0];
        }
        Set<ConstraintViolation<T>> violationSet = getValidator(fastFail).validateProperty(object, propertyName, groups);
        if (CollectionUtils.isNotEmpty(violationSet)) {
            throw new ConstraintViolationException(violationSet);
        }
    }


    public static <T> void validateValue(Class<T> beanType, boolean fastFail, String propertyName, Object value, Class<?>... groups) {
        Set<ConstraintViolation<T>> violationSet = getValidator(fastFail).validateValue(beanType, propertyName, value, groups);
        if (CollectionUtils.isNotEmpty(violationSet)) {
            throw new ConstraintViolationException(violationSet);
        }
    }




}
