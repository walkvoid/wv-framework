package com.github.walkvoid.wvframework.mock.store;

import com.github.walkvoid.wvframework.mock.config.MockProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据库 Mock 数据源实现
 * 
 * <p>核心逻辑：
 * <ol>
 *   <li>从 wv_mock_data 表查询 mock_key 对应的 mock_data</li>
 *   <li>支持缓存（简单内存缓存，避免每次查询数据库）</li>
 *   <li>未找到返回 null，降级为 MockObjectFactory 自动生成</li>
 * </ol>
 *
 * @author walkvoid
 */
@Component
@ConditionalOnBean(DataSource.class)
@ConditionalOnProperty(name = "wv.mock.store.enabled", havingValue = "true", matchIfMissing = false)
public class DatabaseMockDataStore implements MockDataStore {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseMockDataStore.class);

    private final JdbcTemplate jdbcTemplate;
    private final MockProperties properties;
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public DatabaseMockDataStore(DataSource dataSource, MockProperties properties) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        logger.info("初始化 DatabaseMockDataStore，表名: {}", properties.getStore().getTable());
    }

    @Override
    public String getMockData(String key) {
        if (key == null || key.isEmpty()) {
            return null;
        }

        // 1. 先查缓存
        String cached = cache.get(key);
        if (cached != null) {
            return cached;
        }

        // 2. 查数据库
        try {
            String tableName = properties.getStore().getTable();
            String sql = "SELECT mock_data FROM " + tableName 
                       + " WHERE mock_key = ? AND enabled = 1 LIMIT 1";
            
            var results = jdbcTemplate.queryForList(sql, String.class, key);
            if (!results.isEmpty()) {
                String data = results.get(0);
                // 加入缓存
                cache.put(key, data);
                logger.debug("从数据库获取 Mock 数据: {}", key);
                return data;
            }
        } catch (Exception e) {
            logger.warn("从数据库查询 Mock 数据失败, key: {}", key, e);
        }

        return null;
    }

    /**
     * 清除缓存
     */
    public void clearCache() {
        cache.clear();
        logger.info("Mock 数据缓存已清除");
    }

    /**
     * 手动刷新指定 key 的缓存
     */
    public void refreshCache(String key) {
        cache.remove(key);
        getMockData(key);
    }
}
