package com.github.walkvoid.wvframework.mock.core.generator;

import com.github.walkvoid.wvframework.mock.annotation.MockNumber;
import com.github.walkvoid.wvframework.utils.RandomUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.math.BigDecimal;

/**
 * 数值 Mock 数据生成器
 *
 * @author walkvoid
 */
@Component
public class NumberMockDataGenerator implements MockDataGenerator<Object> {

    @Override
    public Object generate(Field field, Object annotation, String lang) {
        MockNumber mockNumber = (MockNumber) annotation;
        
        long min = mockNumber.min();
        long max = mockNumber.max();
        boolean decimal = mockNumber.decimal();
        int decimals = mockNumber.decimals();
        MockNumber.Type type = mockNumber.type();
        
        if (decimal || type == MockNumber.Type.FLOAT || type == MockNumber.Type.DOUBLE || type == MockNumber.Type.BIG_DECIMAL) {
            return RandomUtils.nextDecimal(min, max, decimals);
        }
        
        switch (type) {
            case LONG:
                return RandomUtils.nextLong(min, max);
            case FLOAT:
                return (float) RandomUtils.nextDouble(min, max);
            case DOUBLE:
                return RandomUtils.nextDouble(min, max);
            case BIG_DECIMAL:
                return RandomUtils.nextDecimal(min, max, decimals);
            case INTEGER:
            default:
                return (int) RandomUtils.nextLong(min, max);
        }
    }

    @Override
    public Class<?> getSupportedAnnotationType() {
        return MockNumber.class;
    }
}
