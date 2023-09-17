package com.wvframework.annotations;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wvframework.json.BaseEnumDeserializer;
import com.wvframework.json.BaseEnumSerializer;

/**
 * @author jiangjunqing
 * @version v0.0.1
 * @date 2023/9/17
 * @desc
 */

@JacksonAnnotationsInside
@JsonSerialize(using = BaseEnumSerializer.class)
@JsonDeserialize(using = BaseEnumDeserializer.class)
public @interface BaseEnum {

    String name();

    String enumField() default "value";

}
