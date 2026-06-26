package com.github.walkvoid.wvframework.dao;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * WV Framework DAO 自动配置（预留扩展点）。
 *
 * @author walkvoid
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "wvframework.dao", name = "enabled", havingValue = "true", matchIfMissing = true)
public class WvframeworkDaoAutoConfiguration {
    // 预留，后续扩展
}
