package com.wvframework.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author walkvoid
 * @desc
 */
public class MapUtils {

    private static final int MAXIMUM_CAPACITY = 1 << 30;

    private static final float LOAD_FACTOR = 0.75f;

    private MapUtils(){}

    public static boolean isEmpty(Map<?,?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?,?> map) {
        return !isEmpty(map);
    }

    public static int size(Map<?,?> map) {
       return map == null ? 0 : map.size() ;
    }

    public static <K,V> HashMap<K,V> newHashMap() {
        return new HashMap<K,V>();
    }

    public static <K,V> HashMap<K,V> newHashMap(int capacity) {
        return new HashMap<K,V>(computeHashMapCapacity(capacity));
    }

    /**
     *
     * @param key
     * @param value
     * @param kvs key2，value2，key3，value3，......
     * @return
     */
    public static <K,V> HashMap<K,V> newHashMap(K key, V value, Object... kvs) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("key or value is null");
        }
        if (kvs != null && (kvs.length % 2) == 1) {
            throw new IllegalArgumentException("kvs length is illegal");
        }
        int newCap = computeHashMapCapacity(kvs == null ? 1 : 1 + kvs.length);
        HashMap<K,V> hashMap = new HashMap<K,V>(newCap);
        hashMap.put(key, value);
        if (kvs != null) {
            for (int i = 0; i < kvs.length-1; i=i+2) {
                Object k = kvs[i];
                Object v = kvs[i+1];
                if (k != null && !k.getClass().equals(key.getClass())) {
                    throw new IllegalArgumentException("type is error");
                }
                if (v != null && !v.getClass().equals(value.getClass())) {
                    throw new IllegalArgumentException("type is error");
                }
                hashMap.put((K)k, (V)v);
            }
        }
        return hashMap;
    }

    public static  <K,V> HashMap<K,V> newHashMap(HashMap<K, V>... hashMaps) {
        int cap = 0;
        for (HashMap map : hashMaps) {
            if (map != null) {
                cap = cap + map.size();
            }
        }
        int newCap = computeHashMapCapacity(cap);
        HashMap<K,V> hashMap = new HashMap<K,V>(newCap);
        for (HashMap map : hashMaps) {
            if (map != null) {
                hashMap.putAll(map);
            }
        }
        return hashMap;
    }

    public static <K,V> ConcurrentHashMap<K,V> newConcurrentHashMap() {
        return new ConcurrentHashMap<K,V>();
    }

    public static <K,V> ConcurrentHashMap<K,V> newConcurrentHashMap(int capacity) {
        return new ConcurrentHashMap<K,V>(computeHashMapCapacity(capacity));
    }

    public static <K,V> ConcurrentHashMap<K,V> newConcurrentHashMap(K key, V value, Object... kvs) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("key or value is null");
        }
        if (kvs != null && (kvs.length % 2) == 1) {
            throw new IllegalArgumentException("kvs length is illegal");
        }
        int newCap = computeHashMapCapacity(kvs == null ? 1 : 1 + kvs.length);
        ConcurrentHashMap<K,V> hashMap = new ConcurrentHashMap<K,V>(newCap);
        hashMap.put(key, value);
        if (kvs != null) {
            for (int i = 0; i < kvs.length-1; i=i+2) {
                Object k = kvs[i];
                Object v = kvs[i+1];
                if (k != null && !k.getClass().equals(key.getClass())) {
                    throw new IllegalArgumentException("type is error");
                }
                if (v != null && !v.getClass().equals(value.getClass())) {
                    throw new IllegalArgumentException("type is error");
                }
                hashMap.put((K)k, (V)v);
            }
        }
        return hashMap;
    }

    public static  <K,V> ConcurrentHashMap<K,V> newConcurrentHashMap(ConcurrentHashMap<K, V>... hashMaps) {
        int cap = 0;
        for (ConcurrentHashMap map : hashMaps) {
            if (map != null) {
                cap = cap + map.size();
            }
        }
        int newCap = computeHashMapCapacity(cap);
        ConcurrentHashMap<K,V> hashMap = new ConcurrentHashMap<K,V>(newCap);
        for (ConcurrentHashMap map : hashMaps) {
            if (map != null) {
                hashMap.putAll(map);
            }
        }
        return hashMap;
    }

    /**
     * 计算hashmap的容量
     * @param cap
     * @return
     */
    private static int computeHashMapCapacity(int cap) {
        if (cap <= 0) {
            throw new IllegalArgumentException("cap must be positive");
        } else  {
            int n = cap - 1;
            n |= n >>> 1;
            n |= n >>> 2;
            n |= n >>> 4;
            n |= n >>> 8;
            n |= n >>> 16;
            int newCap = n >= MAXIMUM_CAPACITY ? MAXIMUM_CAPACITY : n + 1;
            return   cap > (newCap * LOAD_FACTOR) ? newCap<<1 : newCap;
        }
    }


}
