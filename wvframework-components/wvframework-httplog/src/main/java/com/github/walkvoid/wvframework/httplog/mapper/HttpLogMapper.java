package com.github.walkvoid.wvframework.httplog.mapper;

import com.github.walkvoid.wvframework.httplog.model.HttpLogRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * HTTP 日志 MyBatis Mapper
 *
 * <p>对应 http_access_log 表
 *
 * @author walkvoid
 */
@Mapper
public interface HttpLogMapper {

    /**
     * 插入 HTTP 日志记录
     *
     * @param record 日志记录
     */
    @Insert("""
            INSERT INTO http_access_log (
                log_id, trace_id, type, description, http_method, url,
                request_headers, request_body, request_body_plain,
                response_status, response_headers, response_body, response_body_plain,
                duration, slow, client_name, method_signature, exception, created_at
            ) VALUES (
                #{record.logId}, #{record.traceId}, #{record.type}, #{record.description},
                #{record.httpMethod}, #{record.url},
                #{record.requestHeaders, typeHandler=com.github.walkvoid.wvframework.httplog.mapper.JsonMapTypeHandler},
                #{record.requestBody}, #{record.requestBodyPlain},
                #{record.responseStatus},
                #{record.responseHeaders, typeHandler=com.github.walkvoid.wvframework.httplog.mapper.JsonMapTypeHandler},
                #{record.responseBody}, #{record.responseBodyPlain},
                #{record.duration}, #{record.slow}, #{record.clientName},
                #{record.methodSignature}, #{record.exception}, #{record.timestamp}
            )
            """)
    void insert(@Param("record") HttpLogRecord record);
}
