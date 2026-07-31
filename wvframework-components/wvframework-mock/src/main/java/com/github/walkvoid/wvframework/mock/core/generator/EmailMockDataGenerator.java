package com.github.walkvoid.wvframework.mock.core.generator;

import com.github.walkvoid.wvframework.mock.annotation.MockEmail;
import com.github.walkvoid.wvframework.mock.util.MockI18nUtil;
import com.github.walkvoid.wvframework.mock.util.MockRuleResolver;
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
            "ming", "hong", "fang", "lan", "jing", "li", "na", "ping", "qiang", "jie",
            "ying", "jun", "lei", "feng", "xia", "yan", "ling", "yan", "xiu", "mei"
    );

    @Override
    public String generate(Field field, Object annotation, String lang) {
        MockEmail mockEmail = (MockEmail) annotation;
        String mode = mockEmail.lang();

        if (MockRuleResolver.LANG_FIXED.equalsIgnoreCase(mode)) {
            return mockEmail.fixedValue();
        }

        String rule = MockRuleResolver.resolve(mode, mockEmail.i18nKey(), mockEmail.rules());
        if (rule != null && !rule.isEmpty()) {
            String fromRule = RandomUtils.fromRule(rule);
            if (!fromRule.isEmpty()) {
                return fromRule;
            }
        }

        String actualLang = MockI18nUtil.resolveLang(mode);
        String domain = RandomUtils.random(COMMON_DOMAINS);
        String username = generateUsername(actualLang);
        return username + "@" + domain;
    }

    private String generateUsername(String lang) {
        String first;
        String last;
        if ("zh-CN".equalsIgnoreCase(lang) || "zh_CN".equalsIgnoreCase(lang)) {
            first = RandomUtils.random(ZH_CN_FIRST_NAMES);
            last = RandomUtils.random(ZH_CN_LAST_NAMES);
        } else {
            first = RandomUtils.nextString(RandomUtils.nextInt(4, 8), "alpha").toLowerCase();
            last = RandomUtils.nextString(RandomUtils.nextInt(4, 10), "alpha").toLowerCase();
        }
        String separator = RandomUtils.random("_", ".", "");
        String number = RandomUtils.nextBoolean() ? String.valueOf(RandomUtils.nextInt(1, 999)) : "";
        return first + separator + last + number;
    }

    @Override
    public Class<?> getSupportedAnnotationType() {
        return MockEmail.class;
    }
}