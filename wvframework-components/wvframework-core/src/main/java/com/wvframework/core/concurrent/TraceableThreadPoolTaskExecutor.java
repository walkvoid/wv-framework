package com.wvframework.core.concurrent;

import org.slf4j.MDC;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @author jiangjunqing
 * @date 2024/6/25
 * @desc
 */
public class TraceableThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {


    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(task);
    }


    public class TraceableTaskDecorator implements TaskDecorator {

        @Override
        public Runnable decorate(Runnable runnable) {

            final String xx = MDC.get("xx");

            return () -> {
                MDC.put("a", "b");
                try {
                    runnable.run();

                } finally {
                    MDC.clear();
                }
            };
        }
    }

    /**
     *
     */
    @ConfigurationProperties(prefix = "wvframework.trace")
    public class TraceableProperties {

        private String traceKey;

        private Map<String, String> exts;

    }




}
