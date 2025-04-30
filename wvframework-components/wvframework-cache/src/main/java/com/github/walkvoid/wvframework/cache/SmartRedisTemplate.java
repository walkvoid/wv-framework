package com.github.walkvoid.wvframework.cache;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.hash.ObjectHashMapper;
import org.springframework.lang.Nullable;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * @author walkvoid
 * @version 1.0
 * @date 2025/3/14
 * @desc
 */
public class SmartRedisTemplate extends RedisTemplate<Object, Object> {

    private  RedisTemplate<?, ?> redisTemplate;


    private static Map<String, Object> opsFieldCache = new ConcurrentHashMap<>();


    public SmartRedisTemplate(){
    }

    public SmartRedisTemplate(RedisConnectionFactory connectionFactory, RedisTemplate<?,?> redisTemplate) {
        this();
        this.redisTemplate = redisTemplate;
        setConnectionFactory(connectionFactory);
        super.afterPropertiesSet();
        this.handleOpsFieldCache();
    }

    String[] opsFieldNames = {"valueOps","listOps","setOps","streamOps","zSetOps","geoOps","hllOps","clusterOps"};


    @SuppressWarnings("unchecked")
    public <K,V> ValueOperations<K, V> opsForValue(Class<K> kClass, Class<V> vClass) {
        return (ValueOperations<K, V>) opsFieldCache.get("valueOps");
    }

    @SuppressWarnings("unchecked")
    public <V> ValueOperations<String, V> opsForValue(Class<V> vClass) {
        return (ValueOperations<String, V>) opsFieldCache.get("valueOps");
    }

    @SuppressWarnings("unchecked")
    public <K,V> ListOperations<K, V> opsForList(Class<K> kClass, Class<V> vClass) {
        return (ListOperations<K, V>) opsFieldCache.get("listOps");
    }

    @SuppressWarnings("unchecked")
    public <V> ListOperations<String, V> opsForList(Class<V> vClass) {
        return (ListOperations<String, V>) opsFieldCache.get("listOps");
    }

    @SuppressWarnings("unchecked")
    public <K,HK,HV> HashOperations<K,HK,HV> opsForHash(Class<K> KClass, Class<HK> HKClass,Class<HV> HVClass) {
        return (HashOperations<K,HK,HV>) opsFieldCache.get("hashOps");
    }

    @SuppressWarnings("unchecked")
    public <HK,HV> HashOperations<String,HK,HV> opsForHash(Class<HK> HKClass, Class<HV> HVClass) {
        return (HashOperations<String,HK,HV>) opsFieldCache.get("hashOps");
    }

    @SuppressWarnings("unchecked")
    public <HV> HashOperations<String, String, HV> opsForHash(Class<HV> HVClass) {
        return (HashOperations<String,String,HV>) opsFieldCache.get("hashOps");
    }

    @SuppressWarnings("unchecked")
    public <K,V> SetOperations<K, V> opsForSet(Class<K> kClass, Class<V> vClass) {
        return (SetOperations<K, V>) opsFieldCache.get("setOps");
    }

    @SuppressWarnings("unchecked")
    public <V> SetOperations<String, V> opsForSet(Class<V> vClass) {
        return (SetOperations<String, V>) opsFieldCache.get("setOps");
    }

    @SuppressWarnings("unchecked")
    public <K,V> ZSetOperations<K, V> opsForZSet(Class<K> kClass, Class<V> vClass) {
        return (ZSetOperations<K, V>) opsFieldCache.get("zSetOps");
    }

    @SuppressWarnings("unchecked")
    public <V> ZSetOperations<String, V> opsForZSet(Class<V> vClass) {
        return (ZSetOperations<String, V>) opsFieldCache.get("zSetOps");
    }

    @Nullable
    protected <T> T postProcessResult(@Nullable T result, RedisConnection conn, boolean existingConnection) {
        return result;
    }

    /**
     * put xxxOps of parent field on opsFieldCache
     */
    private void handleOpsFieldCache() {
        HashOperations<Object, Object, Object> hashOps = this.opsForHash();
        StreamOperations<Object, Object, Object> streamOps = this.opsForStream();
        String[] opsFieldNames = {"valueOps","listOps","setOps","streamOps","zSetOps","geoOps","hllOps","clusterOps"};
        for (String name : opsFieldNames) {
            try {
                Field declaredField = this.getClass().getDeclaredField(name);
                declaredField.setAccessible(true);
                Object field = ReflectionUtils.getField(declaredField, this);
                opsFieldCache.putIfAbsent(name,field);
            } catch (NoSuchFieldException e) {
                //do nothing
            }
        }
    }





}
