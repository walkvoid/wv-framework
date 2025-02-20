package com.github.walkvoid.wvframework.core.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jiangjunqing
 * @date 2025/2/20
 * @description:
 * @version:
 */
@Configuration
public class FrameworkAutoConfiguration {

    @Bean
    @ConditionalOnClass(CoreAutoConfiguration.class)
    public CoreAutoConfiguration coreAutoConfiguration(){
        return new CoreAutoConfiguration();
    }
}
