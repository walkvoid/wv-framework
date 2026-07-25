package com.github.walkvoid.wvframework.httplog.publisher;

import com.github.walkvoid.wvframework.httplog.model.HttpLogRecord;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 组合发布器
 *
 * <p>支持同时使用多个发布器，例如同时写入数据库和发送 MQ
 *
 * @author walkvoid
 */
public class CompositeHttpLogPublisher implements HttpLogPublisher {

    private static final Logger log = LoggerFactory.getLogger(CompositeHttpLogPublisher.class);

    private final List<HttpLogPublisher> publishers;

    public CompositeHttpLogPublisher(List<HttpLogPublisher> publishers) {
        this.publishers = publishers != null ? new ArrayList<>(publishers) : new ArrayList<>();
    }

    @Override
    public void publish(HttpLogRecord record) {
        for (HttpLogPublisher publisher : publishers) {
            try {
                publisher.publish(record);
            } catch (Exception e) {
                log.error("Failed to publish HTTP log via {}: {}",
                        publisher.getClass().getSimpleName(), e.getMessage(), e);
            }
        }
    }

    /**
     * 添加发布器
     */
    public void addPublisher(HttpLogPublisher publisher) {
        if (publisher != null) {
            this.publishers.add(publisher);
        }
    }

    /**
     * 获取所有发布器
     */
    public List<HttpLogPublisher> getPublishers() {
        return new ArrayList<>(publishers);
    }
}
