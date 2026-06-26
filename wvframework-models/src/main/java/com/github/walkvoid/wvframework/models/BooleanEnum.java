package com.github.walkvoid.wvframework.models;

import com.github.walkvoid.wvframework.models.BaseEnum;

/**
 * 布尔值枚举类
 * @author walkvoid
 * @version 1.0
 * @date 2025/11/30
 * @desc 包含key和desc属性，1=是，0=否
 */
public enum BooleanEnum implements BaseEnum<Integer> {

    /**
     * 是
     */
    YES(1, "是"),
    /**
     * 否
     */
    NO(0, "否");

    /**
     * 枚举值
     */
    private final Integer key;
    /**
     * 枚举描述
     */
    private final String desc;

    /**
     * 构造函数
     * @param key 枚举值
     * @param desc 枚举描述
     */
    BooleanEnum(Integer key, String desc) {
        this.key = key;
        this.desc = desc;
    }

    /**
     * 获取枚举值
     * @return 枚举值
     */
    public Integer getKey() {
        return key;
    }

    /**
     * 获取枚举描述
     * @return 枚举描述
     */
    public String getDesc() {
        return desc;
    }

    /**
     * 根据key获取枚举
     * @param key 枚举值
     * @return 对应的枚举
     */
    public static BooleanEnum getByKey(Integer key) {
        for (BooleanEnum booleanEnum : values()) {
            if (booleanEnum.getKey().equals(key)) {
                return booleanEnum;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return toString0();
    }
}
