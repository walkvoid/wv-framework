package com.github.walkvoid.wvframework.validation.dubbo;

import org.apache.dubbo.validation.Validator;

/**
 * @author jiangjunqing
 * @date 2024/11/25
 * @description: dubbo的Validator，适配Hibernate-validate
 * @version: 1.0.0
 */
public class HibernateValidator implements Validator {

    @Override
    public void validate(String methodName, Class<?>[] parameterTypes, Object[] arguments) throws Exception {

    }
}
