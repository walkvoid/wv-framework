package com.github.walkvoid.wvframework.mock.core.generator;

import com.github.walkvoid.wvframework.mock.annotation.MockString;
import com.github.walkvoid.wvframework.mock.util.MockI18nUtil;
import com.github.walkvoid.wvframework.utils.RandomUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * 字符串 Mock 数据生成器
 *
 * @author walkvoid
 */
@Component
public class StringMockDataGenerator implements MockDataGenerator<String> {

    @Value("${mock.string.config.location:mock-strings.properties}")
    private String configLocation;

    @Override
    public String generate(Field field, Object annotation, String lang) {
        MockString mockString = (MockString) annotation;
        
        // 解析语言环境
        String actualLang = MockI18nUtil.resolveLang(mockString.lang());
        
        String prefix = mockString.prefix();
        String suffix = mockString.suffix();
        
        String result;
        
        // 1. 优先使用固定值列表
        if (mockString.values() != null && mockString.values().length > 0) {
            result = RandomUtils.random(mockString.values());
        }
        // 2. 使用配置文件
        else if (mockString.configKey() != null && !mockString.configKey().isEmpty()) {
            result = generateFromConfig(mockString.configKey(), actualLang);
        }
        // 3. 根据长度和字符集生成随机字符串
        else {
            int[] length = parseLength(mockString.length());
            result = RandomUtils.nextString(
                    RandomUtils.nextInt(length[0], length[1]),
                    mockString.charset()
            );
        }
        
        return prefix + result + suffix;
    }

    /**
     * 从配置获取值
     */
    private String generateFromConfig(String configKey, String lang) {
        // 这里可以扩展为从配置文件读取
        // 暂时使用默认值
        return RandomUtils.nextString(RandomUtils.nextInt(6, 12), "alphanumeric");
    }

    /**
     * 解析长度字符串
     */
    private int[] parseLength(String length) {
        if (length == null || length.isEmpty()) {
            return new int[]{6, 20};
        }
        
        try {
            if (length.contains("-")) {
                String[] parts = length.split("-");
                return new int[]{Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim())};
            } else {
                int len = Integer.parseInt(length.trim());
                return new int[]{len, len};
            }
        } catch (Exception e) {
            return new int[]{6, 20};
        }
    }

    @Override
    public Class<?> getSupportedAnnotationType() {
        return MockString.class;
    }
}
