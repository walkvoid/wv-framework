package com.github.walkvoid.wvframework.httplog.publisher;

import com.github.walkvoid.wvframework.httplog.mapper.HttpLogMapper;
import com.github.walkvoid.wvframework.httplog.model.HttpLogRecord;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据库默认日志发布器（默认实现）
 *
 * <p>核心逻辑：
 * <ol>
 *   <li>将 HttpLogRecord 持久化到 http_access_log 表</li>
 *   <li>支持异步写入（通过线程池）避免阻塞主请求</li>
 *   <li>包含 requestBodyPlain / responseBodyPlain 明文字段的持久化</li>
 * </ol>
 *
 * @author walkvoid
 */
public class DatabaseHttpLogPublisher implements HttpLogPublisher {

    private static final Logger log = LoggerFactory.getLogger(DatabaseHttpLogPublisher.class);

    private final HttpLogMapper httpLogMapper;

    private final Executor asyncExecutor;

    public DatabaseHttpLogPublisher(HttpLogMapper httpLogMapper) {
        this(httpLogMapper, Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors(),
                r -> {
                    Thread t = new Thread(r, "httplog-writer");
                    t.setDaemon(true);
                    return t;
                }));
    }

    public DatabaseHttpLogPublisher(HttpLogMapper httpLogMapper, Executor asyncExecutor) {
        this.httpLogMapper = httpLogMapper;
        this.asyncExecutor = asyncExecutor;
    }

    @Override
    public void publish(HttpLogRecord record) {
        // 异步写入数据库，不阻塞主请求流程
        asyncExecutor.execute(() -> {
            try {
                httpLogMapper.insert(record);
            } catch (Exception e) {
                log.error("Failed to insert HTTP log record: {}", record.getLogId(), e);
            }
        });
    }
}
