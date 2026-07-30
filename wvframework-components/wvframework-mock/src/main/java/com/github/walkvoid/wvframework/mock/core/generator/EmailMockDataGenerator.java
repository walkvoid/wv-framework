package com.github.walkvoid.wvframework.mock.core.generator;

import com.github.walkvoid.wvframework.mock.annotation.MockEmail;
import com.github.walkvoid.wvframework.mock.util.MockI18nUtil;
import com.github.walkvoid.wvframework.utils.RandomUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 邮箱 Mock 数据生成器
 *
 * @author walkvoid
 */
@Component
public class EmailMockDataGenerator implements MockDataGenerator<String> {

    private static final List<String> COMMON_DOMAINS = List.of(
            "gmail.com", "yahoo.com", "hotmail.com", "outlook.com", "qq.com",
            "163.com", "126.com", "sina.com", "sohu.com", "foxmail.com"
    );

    private static final List<String> ZH_CN_FIRST_NAMES = List.of(
            "wang", "li", "zhang", "liu", "chen", "yang", "huang", "zhao", "wu", "zhou",
            "xu", "sun", "ma", "zhu", "hu", "guo", "he", "gao", "lin", "luo"
    );

    private static final List<String> ZH_CN_LAST_NAMES = List.of(
            "ming", "hong", "fang", "lan", "jing", "li", "na", "Ping", "qiang", "jie",
            "ying", "jun", "lei", "feng", "xia", "yan", "ling", "yan", "xiu", "mei"
    );

    @Override
    public String generate(Field field, Object annotation, String lang) {
        MockEmail mockEmail = (MockEmail) annotation;
        String actualLang = MockI18nUtil.resolveLang(mockEmail.lang());
        
        String domain = mockEmail.domain();
        if (domain == null || domain.isEmpty()) {
            domain = RandomUtils.random(COMMON_DOMAINS);
        }
        
        // 生成随机用户名
        String username = generateUsername(actualLang);
        
        return username + "@" + domain;
    }

    private String generateUsername(String lang) {
        if ("zh-CN".equalsIgnoreCase(lang)) {
            String first = RandomUtils.random(ZH_CN_FIRST_NAMES);
            String last = RandomUtils.random(ZH_CN_LAST_NAMES);
            String separator = RandomUtils.random("_", ".", "");
            String number = RandomUtils.nextBoolean() ? String.valueOf(RandomUtils.nextInt(1, 999)) : "";
            return first + separator + last + number;
        } else {
            String first = RandomUtils.nextString(RandomUtils.nextInt(4, 8), "alpha").toLowerCase();
            String last = RandomUtils.nextString(RandomUtils.nextInt(4, 10), "alpha").toLowerCase();
            String separator = RandomUtils.random("_", ".", "");
            String number = RandomUtils.nextBoolean() ? String.valueOf(RandomUtils.nextInt(1, 999)) : "";
            return first + separator + last + number;
        }
    }

    @Override
    public Class<?> getSupportedAnnotationType() {
        return MockEmail.class;
    }
}
