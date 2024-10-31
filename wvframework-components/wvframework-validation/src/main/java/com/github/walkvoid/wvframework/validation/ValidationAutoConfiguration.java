package com.github.walkvoid.wvframework.validation;

import com.github.walkvoid.wvframework.validation.mvc.ValidationExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author walkvoid
 * @version 0.0.1
 * @date 2024/5/22
 * @desc 自动配置类
 */
@Configuration
@ConditionalOnProperty(name = "wvframework.validation.enabled", havingValue = "true", matchIfMissing = true)
public class ValidationAutoConfiguration {

    @Bean
    @ConditionalOnWebApplication
    public ValidationExceptionHandler validationExceptionHandler() {
        return new ValidationExceptionHandler();
    }

}
