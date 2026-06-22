package com.github.walkvoid.wvframework.dao;

import java.io.Serializable;

/**
 * 通用枚举基类接口 — 任何实现此接口的枚举均可通过 type-enums-package 被 MyBatis-Plus 自动识别。
 *
 * @param <K> 枚举 key 的类型（通常为 Integer）
 * @author walkvoid
 */
public interface BaseEnum<K extends Serializable> {

    /** 数据库存储值 */
    K getKey();

    /** 枚举描述文本 */
    String getDesc();
}
