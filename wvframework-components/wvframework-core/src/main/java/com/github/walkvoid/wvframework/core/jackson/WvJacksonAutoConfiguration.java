package com.github.walkvoid.wvframework.core.jackson;

import com.fasterxml.jackson.databind.Module;
import com.github.walkvoid.wvframework.utils.JsonUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * Registers JSR-310 LocalDate / LocalDateTime ser/deser.
 * Patterns come from {@code wv.jackson.local-date-format} and {@code wv.jackson.local-date-time-format}.
 */
@AutoConfiguration(before = JacksonAutoConfiguration.class)
@ConditionalOnClass(Module.class)
public class WvJacksonAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "wvJavaTimeModule")
    public Module wvJavaTimeModule(Environment environment) {
        String dateTimePattern = environment.getProperty(
                "wv.jackson.local-date-time-format", JsonUtils.LOCAL_DATE_TIME_FORMAT);
        String datePattern = environment.getProperty(
                "wv.jackson.local-date-format", JsonUtils.LOCAL_DATE_FORMAT);
        return JsonUtils.createJavaTimeModule(dateTimePattern, datePattern);
    }
}
