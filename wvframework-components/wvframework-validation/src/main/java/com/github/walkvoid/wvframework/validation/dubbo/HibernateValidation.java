package com.github.walkvoid.wvframework.validation.dubbo;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.validation.Validation;
import org.apache.dubbo.validation.Validator;

/**
 * @author jiangjunqing
 * @date 2024/11/25
 * @description: dubbo的Validation实现，适配Hibernate-validate
 * @version: 1.0.0
 */
public class HibernateValidation implements Validation {

    @Override
    public Validator getValidator(URL url) {
        return null;
    }
}
