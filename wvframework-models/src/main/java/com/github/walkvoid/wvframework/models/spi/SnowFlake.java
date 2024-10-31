package com.github.walkvoid.wvframework.models.spi;

/**
 * @author jiangjunqing
 * @version 0.0.1
 * @date 2024/10/31
 * @desc
 */
public interface SnowFlake {

    /**
     * 获取雪花ID
     * @return
     */
    Long nextId();
}
