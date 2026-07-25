package com.github.walkvoid.wvframework.httplog.publisher;

import com.github.walkvoid.wvframework.httplog.model.HttpLogRecord;

/**
 * HTTP 日志发布器接口（SPI）
 *
 * <p>业务方可实现此接口扩展自定义日志持久化（如发 MQ、上报 APM）
 *
 * @author walkvoid
 */
public interface HttpLogPublisher {

    /**
     * 发布 HTTP 日志记录
     *
     * @param record 日志记录
     */
    void publish(HttpLogRecord record);
}
