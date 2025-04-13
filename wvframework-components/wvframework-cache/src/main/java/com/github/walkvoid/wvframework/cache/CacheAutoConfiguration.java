package com.github.walkvoid.wvframework.cache;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@AutoConfiguration
@AutoConfigureAfter({RedisAutoConfiguration.class})
public class CacheAutoConfiguration {
    public CacheAutoConfiguration() {
    }


    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnSingleCandidate(RedisConnectionFactory.class)
    public SmartRedisTemplate smartRedisTemplate(RedisConnectionFactory redisConnectionFactory,
                                                 RedisTemplate<?,?> redisTemplate) {
        return new SmartRedisTemplate(redisConnectionFactory, redisTemplate);
    }
}