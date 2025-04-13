package com.github.walkvoid.wvframework.cache;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.lang.Nullable;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author walkvoid
 * @version 1.0
 * @date 2025/3/14
 * @desc
 */
public class SmartRedisTemplate extends RedisTemplate<Object, Object> {

    private  RedisTemplate<?, ?> redisTemplate;

    private static Map<String, Field> fieldMap = new ConcurrentHashMap<>();

    public SmartRedisTemplate(){
    }

    public SmartRedisTemplate(RedisConnectionFactory connectionFactory, RedisTemplate<?,?> redisTemplate) {
        this();
        this.redisTemplate = redisTemplate;
        setConnectionFactory(connectionFactory);
        afterPropertiesSet();
    }



    @SuppressWarnings("unchecked")
    public <K,V> ValueOperations<K, V> opsForValue(Class<K> kClass, Class<V> vClass) {
        return (ValueOperations<K, V>) super.opsForValue();
    }

    @SuppressWarnings("unchecked")
    public <V> ValueOperations<String, V> opsForValue(Class<V> vClass) {
        return (ValueOperations<String, V>) redisTemplate.opsForValue();
    }


    @SuppressWarnings("unchecked")
    public <K,V> ValueOperations<K, V> opsForList(Class<K> kClass, Class<V> vClass) {
        return (ValueOperations<K, V>) super.opsForList();
    }

    @SuppressWarnings("unchecked")
    public <String,V> ValueOperations<String, V> opsForList(Class<V> vClass) {
        return (ValueOperations<String, V>) super.opsForValue();
    }

    @Nullable
    protected <T> T postProcessResult(@Nullable T result, RedisConnection conn, boolean existingConnection) {
        return result;
    }



}
