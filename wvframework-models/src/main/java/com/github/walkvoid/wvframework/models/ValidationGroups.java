package com.github.walkvoid.wvframework.models;

import javax.validation.groups.Default;

/**
 * @author jiangjunqing
 * @date 2024/11/26
 * @description:
 * @version:
 */
public interface ValidationGroups {

    Class<?> dx = Default.class;

    class Add{}

    class Delete{}

    class Update{}

    class Query{}

    class Export{}
}
