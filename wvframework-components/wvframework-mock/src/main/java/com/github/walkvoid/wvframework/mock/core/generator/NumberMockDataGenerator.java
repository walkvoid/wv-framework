package com.github.walkvoid.wvframework.mock.core.generator;


import com.github.walkvoid.wvframework.mock.annotation.MockNumber;
import com.github.walkvoid.wvframework.mock.util.MockRuleResolver;
import com.github.walkvoid.wvframework.utils.RandomUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;

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
        String mode = mockNumber.lang();

        if (MockRuleResolver.LANG_FIXED.equalsIgnoreCase(mode)) {
            return parseFixedNumber(mockNumber.fixedValue(), mockNumber);
        }

        String rule = MockRuleResolver.resolve(mode, mockNumber.i18nKey(), mockNumber.rules());
        if (rule != null && !rule.isEmpty()) {
            String fromRule = RandomUtils.fromRule(rule);
            if (!fromRule.isEmpty()) {
                return coerceNumber(fromRule, mockNumber);
            }
        }
        return legacyGenerate(mockNumber);
    }

    private Object legacyGenerate(MockNumber mockNumber) {
        long min = mockNumber.min();
        long max = mockNumber.max();
        int decimals = mockNumber.decimals();
        MockNumber.Type type = mockNumber.type();

        if (mockNumber.decimal() || isDecimalType(type)) {
            return toDecimal(RandomUtils.nextDecimal(min, max, decimals), type);
        }
        long value = RandomUtils.nextLong(min, max);
        return type == MockNumber.Type.LONG ? value : (int) value;
    }

    private Object parseFixedNumber(String fixed, MockNumber mockNumber) {
        if (fixed == null || fixed.isEmpty()) {
            return legacyGenerate(mockNumber);
        }
        return coerceNumber(fixed, mockNumber);
    }

    private Object coerceNumber(String text, MockNumber mockNumber) {
        MockNumber.Type type = mockNumber.type();
        try {
            if (mockNumber.decimal() || isDecimalType(type)) {
                return toDecimal(toBigDecimal(text, mockNumber), type);
            }
            long value = Long.parseLong(text);
            return type == MockNumber.Type.LONG ? value : (int) value;
        } catch (NumberFormatException e) {
            return legacyGenerate(mockNumber);
        }
    }

    private static BigDecimal toBigDecimal(String text, MockNumber mockNumber) {
        BigDecimal value = new BigDecimal(text);
        // 仅 BIG_DECIMAL 保留小数位；其它小数类型由 toDecimal 强转时按 JVM 默认行为处理。
        if (mockNumber.type() == MockNumber.Type.BIG_DECIMAL) {
            value = value.setScale(mockNumber.decimals(), RoundingMode.HALF_UP);
        }
        return value;
    }

    private static boolean isDecimalType(MockNumber.Type type) {
        return type == MockNumber.Type.FLOAT
                || type == MockNumber.Type.DOUBLE
                || type == MockNumber.Type.BIG_DECIMAL;
    }

    private static Object toDecimal(BigDecimal value, MockNumber.Type type) {
        return switch (type) {
            case INTEGER -> value.intValue();
            case LONG -> value.longValue();
            case FLOAT -> value.floatValue();
            case DOUBLE -> value.doubleValue();
            case BIG_DECIMAL -> value;
        };
    }

    @Override
    public Class<?> getSupportedAnnotationType() {
        return MockNumber.class;
    }
}