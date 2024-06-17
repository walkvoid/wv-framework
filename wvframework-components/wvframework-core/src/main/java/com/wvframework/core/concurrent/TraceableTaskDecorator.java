package com.wvframework.core.concurrent;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

/**
 * @author jiangjunqing
 * @date 2024/6/17
 * @desc 支持traceID在线程池传递的TaskDecorator
 */
public class TraceableTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        final String xx = MDC.get("xx");

        return () -> {
            MDC.put("a","b");
            try {
                runnable.run();

            } finally {
                MDC.clear();
            }
        };
    }






}
