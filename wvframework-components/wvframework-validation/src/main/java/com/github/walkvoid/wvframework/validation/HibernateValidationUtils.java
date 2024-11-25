package com.github.walkvoid.wvframework.validation;

import org.hibernate.validator.internal.engine.ConfigurationImpl;
import org.hibernate.validator.internal.engine.ValidatorFactoryImpl;
import org.hibernate.validator.internal.engine.ValidatorImpl;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.sql.SQLException;
import java.util.Set;

/**
 * @author jiangjunqing
 * @date 2024/11/25
 * @description: ValidationUtils的工具类
 * @version:
 */
public class HibernateValidationUtils {


    public static Validator validator = null;

    public static Validator validatorFastFail = null;

    private HibernateValidationUtils(){}

    /**
     * 获取基于Hibernate实现的Validator
     * @return
     */
    public static Validator getValidator(){
        return getValidator(false);
    }

    public static Validator getValidatorFastFail(){
        return getValidator(true);
    }

    private static Validator getValidator(boolean fastFail){
        Validator result = fastFail ? validatorFastFail : validator;
        if (result == null) {
            synchronized (HibernateValidationUtils.class) {
                result = fastFail ? validatorFastFail : validator;;
                if (result == null) {
                    //new ConfigurationImpl();
                    //ValidatorFactoryImpl validatorFactory = new ValidatorFactoryImpl();
                }
            }
        }
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

        Validator validator = validatorFactory.getValidator();

        if (validator instanceof ValidatorImpl) {
        }
        return null;
    }




    /**
     * 验证指定分组下的对象实例
     * @param object 需要验证的对象
     * @param <T>
     * @return
     * @throws ConstraintViolation
     */
    <T> Set<ConstraintViolation<T>> validate(T object) {
        return null;
    }

    /**
     * 验证指定分组下的对象实例
     * @param object 需要验证的对象
     * @param <T>
     * @return
     */
    <T> Set<ConstraintViolation<T>> validate(T object, boolean fastFail) {
        return null;
    }


    /**
     * 验证指定分组下的对象实例
     * @param object 需要验证的对象
     * @param groups 需要验证的分组
     * @param <T>
     * @return
     */
    <T> Set<ConstraintViolation<T>> validate(T object, boolean fastFail, Class<?>... groups) {
        return null;
    }

    <T> Set<ConstraintViolation<T>> validateProperty(T object, boolean fastFail, String propertyName, Class<?>... groups) {
        return null;
    }


    <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, boolean fastFail, String propertyName, Object value, Class<?>... groups) {
        return null;
    }


}
