package com.github.walkvoid.wvframework.mock.core;

import com.github.walkvoid.wvframework.mock.annotation.*;
import com.github.walkvoid.wvframework.mock.config.MockProperties;
import com.github.walkvoid.wvframework.mock.core.generator.MockDataGenerator;
import com.github.walkvoid.wvframework.mock.core.generator.MockDataGeneratorRegistry;
import com.github.walkvoid.wvframework.mock.store.MockDataStore;
import com.github.walkvoid.wvframework.mock.util.MockI18nUtil;
import com.github.walkvoid.wvframework.mock.util.RandomUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Mock 对象工厂
 * 
 * <p>负责根据注解生成 Mock 对象</p>
 *
 * @author walkvoid
 */
@Component
public class MockObjectFactory {

    private static final Logger logger = LoggerFactory.getLogger(MockObjectFactory.class);

    @Autowired
    private MockDataGeneratorRegistry generatorRegistry;

    @Autowired
    private MockProperties properties;

    @Autowired
    private ObjectMapper objectMapper;

    private MockDataStore mockDataStore;

    @Autowired(required = false)
    public void setMockDataStore(MockDataStore mockDataStore) {
        this.mockDataStore = mockDataStore;
    }

    /**
     * 根据数据键获取预配置的 Mock 数据
     */
    public Object getMockDataByKey(String key) {
        if (mockDataStore != null && key != null && !key.isEmpty()) {
            String jsonData = mockDataStore.getMockData(key);
            if (jsonData != null) {
                try {
                    return objectMapper.readValue(jsonData, Object.class);
                } catch (Exception e) {
                    logger.warn("解析 Mock 数据失败, key: {}", key, e);
                }
            }
        }
        return null;
    }

    /**
     * 创建 Mock 对象
     *
     * @param returnType 返回类型
     * @param mock       @Mock 注解
     * @return Mock 对象
     */
    public Object createMockObject(Type returnType, Mock mock) {
        // 1. 如果指定了数据键，优先从数据源获取
        if (mock != null && mock.value() != null && !mock.value().isEmpty()) {
            Object dataFromStore = getMockDataByKey(mock.value());
            if (dataFromStore != null) {
                return dataFromStore;
            }
        }

        // 2. 解析返回类型，创建 Mock 对象
        int count = (mock != null) ? mock.count() : properties.getController().getDefaultCount();
        return createMockObjectByType(returnType, count);
    }

    /**
     * 创建 Mock 对象（不依赖 @Mock 注解，基于 OperationSource 缓存）
     *
     * <p>仅按返回类型与条数生成 Mock 对象，调用方需自行处理
     * {@link com.github.walkvoid.wvframework.mock.store.MockDataStore} 的查询。</p>
     *
     * @param returnType 方法返回类型（支持 ParameterizedType，如 List&lt;T&gt;）
     * @param count      集合类型返回值的条数
     * @return Mock 对象
     */
    public Object createMockObject(Type returnType, int count) {
        return createMockObjectByType(returnType, count);
    }
    /**
     * 根据类型创建 Mock 对象
     */
    private Object createMockObjectByType(Type returnType, int count) {
        // count 由调用方传入

        // 处理集合类型
        if (returnType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) returnType;
            Type rawType = pt.getRawType();
            Type[] typeArgs = pt.getActualTypeArguments();

            if (rawType == List.class || rawType == ArrayList.class) {
                if (typeArgs.length > 0) {
                    Class<?> elementType = (Class<?>) typeArgs[0];
                    return createMockList(elementType, count);
                }
            }
        }

        // 处理简单类型
        Class<?> clazz = getRawType(returnType);
        return createMockObjectByClass(clazz);
    }

    /**
     * 创建 Mock 列表
     */
    @SuppressWarnings("unchecked")
    private List<Object> createMockList(Class<?> elementType, int count) {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(createMockObjectByClass(elementType));
        }
        return list;
    }

    /**
     * 根据 Class 创建 Mock 对象
     */
    public Object createMockObjectByClass(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        // 基本类型处理
        if (clazz == String.class) {
            return RandomUtil.nextString(RandomUtil.nextInt(6, 20), "alphanumeric");
        }
        if (clazz == Integer.class || clazz == int.class) {
            return RandomUtil.nextInt(1, 1000);
        }
        if (clazz == Long.class || clazz == long.class) {
            return RandomUtil.nextLong(1, 10000);
        }
        if (clazz == Double.class || clazz == double.class) {
            return RandomUtil.nextDouble(0, 1000);
        }
        if (clazz == Float.class || clazz == float.class) {
            return (float) RandomUtil.nextDouble(0, 1000);
        }
        if (clazz == Boolean.class || clazz == boolean.class) {
            return RandomUtil.nextBoolean();
        }
        if (clazz == Date.class) {
            return new Date(RandomUtil.nextLong(System.currentTimeMillis() - 365L * 24 * 3600 * 1000, System.currentTimeMillis()));
        }
        if (clazz == java.time.LocalDate.class) {
            return RandomUtil.nextDate(
                    java.time.LocalDate.now().minusYears(5),
                    java.time.LocalDate.now()
            );
        }
        if (clazz == java.time.LocalDateTime.class) {
            return RandomUtil.nextDateTime(
                    java.time.LocalDateTime.now().minusYears(5),
                    java.time.LocalDateTime.now()
            );
        }

        // 创建实体对象
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            fillMockFields(instance);
            return instance;
        } catch (Exception e) {
            logger.warn("创建 Mock 对象失败: {}", clazz.getName(), e);
            return null;
        }
    }

    /**
     * 填充对象的 Mock 字段
     */
    private void fillMockFields(Object instance) {
        Class<?> clazz = instance.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object mockValue = generateMockValue(field);
                if (mockValue != null) {
                    field.set(instance, mockValue);
                }
            } catch (Exception e) {
                logger.debug("填充 Mock 字段失败: {}.{}", clazz.getSimpleName(), field.getName(), e);
            }
        }
    }

    /**
     * 为字段生成 Mock 值
     */
    private Object generateMockValue(Field field) {
        // 获取语言环境
        String lang = MockI18nUtil.getCurrentLang();

        // 检查字段上的所有注解
        for (MockDataGenerator<?> generator : generatorRegistry.getAllGenerators().values()) {
            @SuppressWarnings("unchecked")
            Class<? extends java.lang.annotation.Annotation> annotationType =
                    (Class<? extends java.lang.annotation.Annotation>) generator.getSupportedAnnotationType();
            java.lang.annotation.Annotation annotation = field.getAnnotation(annotationType);
            if (annotation != null) {
                @SuppressWarnings("unchecked")
                MockDataGenerator<Object> gen = (MockDataGenerator<Object>) generator;
                return gen.generate(field, annotation, lang);
            }
        }

        // 没有找到特定注解，使用默认策略
        return generateDefaultValue(field);
    }

    /**
     * 生成默认值
     */
    private Object generateDefaultValue(Field field) {
        Class<?> fieldType = field.getType();

        if (fieldType == String.class) {
            return RandomUtil.nextString(RandomUtil.nextInt(6, 20), "alphanumeric");
        }
        if (fieldType == Integer.class || fieldType == int.class) {
            return RandomUtil.nextInt(1, 1000);
        }
        if (fieldType == Long.class || fieldType == long.class) {
            return RandomUtil.nextLong(1, 10000);
        }
        if (fieldType == Double.class || fieldType == double.class) {
            return RandomUtil.nextDouble(0, 1000);
        }
        if (fieldType == Boolean.class || fieldType == boolean.class) {
            return RandomUtil.nextBoolean();
        }
        if (fieldType == java.math.BigDecimal.class) {
            return RandomUtil.nextDecimal(0, 1000, 2);
        }

        return null;
    }

    /**
     * 获取原始类型
     */
    private Class<?> getRawType(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        }
        return Object.class;
    }

    /**
     * 为请求参数填充 Mock 数据
     */
    public void fillMockRequest(Object request) {
        if (request == null) {
            return;
        }
        fillMockFields(request);
    }
}


