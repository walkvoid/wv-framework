package com.github.walkvoid.wvframework.core.concurrent;

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
public class CommonThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {


    @Override
    public void setCorePoolSize(int corePoolSize) {
        if (corePoolSize<0){
            corePoolSize=2;
        }
        super.setCorePoolSize(corePoolSize);
    }

    @Override
    public int getMaxPoolSize() {
        return super.getMaxPoolSize();
    }

    @Override
    public void setKeepAliveSeconds(int keepAliveSeconds) {
        super.setKeepAliveSeconds(keepAliveSeconds);
    }

    @Override
    public void setQueueCapacity(int queueCapacity) {
        super.setQueueCapacity(queueCapacity);
    }

    @Override
    public void setAllowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
        super.setAllowCoreThreadTimeOut(allowCoreThreadTimeOut);
    }

    @Override
    public void setPrestartAllCoreThreads(boolean prestartAllCoreThreads) {
        super.setPrestartAllCoreThreads(prestartAllCoreThreads);
    }

    @Override
    public void setTaskDecorator(TaskDecorator taskDecorator) {
        if (taskDecorator == null) {
            super.setTaskDecorator(new TraceableTaskDecorator());
        }else {
            super.setTaskDecorator(taskDecorator);
        }
    }

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




}
