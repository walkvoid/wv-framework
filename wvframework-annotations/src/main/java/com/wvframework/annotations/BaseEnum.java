package com.wvframework.annotations;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wvframework.annotations.jackson.BaseEnumDeserializer;
import com.wvframework.annotations.jackson.BaseEnumSerializer;

/**
 * @author walkvoid
 * @version v0.0.1
 * @desc 标记一个枚举为基础的枚举类型：
 * 1、此类枚举可以指定一个字段作为该枚举的（反）序列化字段。
 * 2、如果引入了wvframework-web-starter，提供一个查询所有标记了该注解枚举集合的列表接口
 */

@JacksonAnnotationsInside
@JsonSerialize(using = BaseEnumSerializer.class)
@JsonDeserialize(using = BaseEnumDeserializer.class)
public @interface BaseEnum {

    /**
     * 别名,可用作枚举列表展示，如果为空，默认为枚举的类名
     * @return
     */
    String alias();

    /**
     * （反）序列化字段，如果为空，默认为Enum.name()
     * @return
     */
    String enumField();

}
