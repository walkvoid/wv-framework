package com.github.walkvoid.wvframework.httplog.publisher;

import com.github.walkvoid.wvframework.httplog.model.HttpLogRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于 SLF4J 的日志发布器
 *
 * <p>当没有配置数据库（MyBatis Mapper）时作为默认发布器，
 * 将 HTTP 日志通过 SLF4J 输出，便于开发和调试。
 *
 * @author walkvoid
 */
public class Slf4jHttpLogPublisher implements HttpLogPublisher {

    private static final Logger log = LoggerFactory.getLogger("com.github.walkvoid.wvframework.httplog.access");

    @Override
    public void publish(HttpLogRecord record) {
        if (record == null) {
            return;
        }
        log.info("[HTTP-LOG] {} {} {} | {}ms | {} | {} | traceId={}",
                record.getType(),
                record.getHttpMethod(),
                record.getUrl(),
                record.getDuration(),
                record.getResponseStatus(),
                record.getMethodSignature(),
                record.getTraceId());
    }
}
