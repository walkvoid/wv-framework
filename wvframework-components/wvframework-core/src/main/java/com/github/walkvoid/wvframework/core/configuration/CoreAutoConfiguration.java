package com.github.walkvoid.wvframework.core.configuration;

import com.github.walkvoid.wvframework.core.concurrent.CommonThreadPoolTaskExecutor;
import com.github.walkvoid.wvframework.core.concurrent.SmartApplicationEventPublisher;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jiangjunqing
 * @date 2025/2/20
 * @description:
 * @version:
 */

@Configuration
public class CoreAutoConfiguration {

    @Bean
    public SmartApplicationEventPublisher smartApplicationEventPublisher(ApplicationEventPublisher publisher){
        return new SmartApplicationEventPublisher(publisher);
    }

    @Bean({"commonThreadPoolTaskExecutor"})
    Executor commonThreadPoolTaskExecutor() {
        CommonThreadPoolTaskExecutor executor = new CommonThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(1024);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadNamePrefix("wvframework-pool-");
        executor.initialize();
        return executor;
    }
}
