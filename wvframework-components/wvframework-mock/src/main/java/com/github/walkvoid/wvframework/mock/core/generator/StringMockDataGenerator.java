package com.github.walkvoid.wvframework.mock.core.generator;
import com.github.walkvoid.wvframework.mock.annotation.MockString;
import com.github.walkvoid.wvframework.mock.util.MockRuleResolver;
import com.github.walkvoid.wvframework.utils.RandomUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * 字符串 Mock 数据生成器
 *
 * @author walkvoid
 */
@Component
public class StringMockDataGenerator implements MockDataGenerator<String> {

    @Override
    public String generate(Field field, Object annotation, String lang) {
        MockString mockString = (MockString) annotation;
        String mode = mockString.lang();

        if (MockRuleResolver.LANG_FIXED.equalsIgnoreCase(mode)) {
            return wrap(mockString.fixedValue(), mockString.prefix(), mockString.suffix());
        }

        // LANG_GENER 与 LANG_NONE 在 MockRuleResolver 中都返回 null，自然走 legacyGenerate；
        // 这里不再额外打 warn 噪日志。

        String rule = MockRuleResolver.resolve(mode, mockString.i18nKey(), mockString.rules());
        if (rule != null && !rule.isEmpty()) {
            String fromRule = RandomUtils.fromRule(rule);
            if (!fromRule.isEmpty()) {
                return wrap(fromRule, mockString.prefix(), mockString.suffix());
            }
        }

        String core;
        if (mockString.values() != null && mockString.values().length > 0) {
            core = RandomUtils.random(mockString.values());
        } else {
            int[] length = parseLength(mockString.length());
            core = RandomUtils.nextString(RandomUtils.nextInt(length[0], length[1]), mockString.charset());
        }
        return wrap(core, mockString.prefix(), mockString.suffix());
    }

    private static String wrap(String core, String prefix, String suffix) {
        if (core == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        if (prefix != null && !prefix.isEmpty()) {
            sb.append(prefix);
        }
        sb.append(core);
        if (suffix != null && !suffix.isEmpty()) {
            sb.append(suffix);
        }
        return sb.toString();
    }

    private static int[] parseLength(String length) {
        if (length == null || length.isEmpty()) {
            return new int[]{6, 20};
        }
        try {
            int dash = length.indexOf('-');
            if (dash > 0) {
                return new int[]{Integer.parseInt(length.substring(0, dash).trim()), Integer.parseInt(length.substring(dash + 1).trim())};
            }
            int len = Integer.parseInt(length.trim());
            return new int[]{len, len};
        } catch (Exception e) {
            return new int[]{6, 20};
        }
    }

    @Override
    public Class<?> getSupportedAnnotationType() {
        return MockString.class;
    }
}
