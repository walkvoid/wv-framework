package com.github.walkvoid.wvframework.models;

import com.baomidou.mybatisplus.annotation.IEnum;

import java.io.Serializable;

/**
 * 通用枚举基类接口 — 继承 MyBatis-Plus {@link IEnum}，复用其枚举映射能力。
 * <p>
 * 实现此接口的枚举可被 MyBatis-Plus 自动识别为数据库映射枚举，
 * 无需额外 TypeHandler。
 *
 * @param <K> 枚举 key 的类型（通常为 Integer）
 * @author walkvoid
 */
public interface BaseEnum<K extends Serializable> extends IEnum<K> {

    /** 数据库存储值 */
    K getKey();

    /** 枚举描述文本 */
    String getDesc();

    /** MyBatis-Plus 枚举值 — 委托给 {@link #getKey()} */
    @Override
    default K getValue() {
        return getKey();
    }

    /** JSON 序列化：{"key":"xxx","desc":"xxx"} */
    default String toString0() {
        return "{\"key\":\"" + getKey() + "\",\"desc\":\"" + getDesc() + "\"}";
    }
}
