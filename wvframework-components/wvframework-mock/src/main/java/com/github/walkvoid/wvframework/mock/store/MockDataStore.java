package com.github.walkvoid.wvframework.mock.store;

/**
 * Mock 数据源 SPI 接口
 * 
 * <p>支持从不同来源获取预配置的 Mock 数据</p>
 * <p>默认实现为数据库，业务方可自定义实现（如从 Redis、配置文件读取）</p>
 *
 * @author walkvoid
 */
public interface MockDataStore {

    /**
     * 根据数据键获取预配置的 Mock 数据
     *
     * @param key 数据键（如 "bank.callback.success"）
     * @return Mock 数据的 JSON 字符串，未找到返回 null
     */
    String getMockData(String key);

    /**
     * 检查数据键是否存在
     *
     * @param key 数据键
     * @return 是否存在
     */
    default boolean exists(String key) {
        return getMockData(key) != null;
    }
}
