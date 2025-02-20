package com.github.walkvoid.wvframework.core.concurrent;

import com.github.walkvoid.wvframework.utils.JsonUtils;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;

/**
 * @author jiangjunqing
 * @date 2025/2/20
 * @description: 
 * @version: 
 */
public class SmartApplicationEventPublisher implements ApplicationEventPublisher {
    
    private static final Logger log = LoggerFactory.getLogger(SmartApplicationEventPublisher.class);
    
    private ApplicationEventPublisher applicationEventPublisher;

    public SmartApplicationEventPublisher() {
    }

    public SmartApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.applicationEventPublisher = publisher;
    }

    public void publishEvent(ApplicationEvent event) {
        this.applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publishEvent(Object event) {
        
    }

    @Async("traceIdTransmittableExecutor")
    public void pushAsyncEvent(ApplicationEvent event) {
        log.debug("开始发送spring异步事件，event：{}", JsonUtils.object2json(event));
        this.applicationEventPublisher.publishEvent(event);
    }

    public void pushAsyncEvent(ApplicationEvent event, Executor executor) {
        if (executor != null) {
            CompletableFuture.runAsync(() -> this.publishEvent(event), executor);
        } else {
            CompletableFuture.runAsync(() -> this.publishEvent(event));
        }
    }
}
