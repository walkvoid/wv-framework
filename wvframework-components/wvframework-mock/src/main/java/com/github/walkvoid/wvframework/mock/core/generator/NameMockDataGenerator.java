package com.github.walkvoid.wvframework.mock.core.generator;
import com.github.walkvoid.wvframework.mock.annotation.MockName;
import com.github.walkvoid.wvframework.mock.util.MockI18nUtil;
import com.github.walkvoid.wvframework.mock.util.MockRuleResolver;
import com.github.walkvoid.wvframework.utils.RandomUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 姓名 Mock 数据生成器
 *
 * @author walkvoid
 */
@Component
public class NameMockDataGenerator implements MockDataGenerator<String> {

    private static final List<String> ZH_CN_FIRST_NAMES = List.of(
            "伟", "芳", "娜", "秀英", "敏", "静", "丽", "强", "磊", "军",
            "洋", "勇", "艳", "杰", "娟", "涛", "明", "超", "秀兰", "霞"
    );

    private static final List<String> ZH_CN_LAST_NAMES = List.of(
            "王", "李", "张", "刘", "陈", "杨", "赵", "黄", "周", "吴",
            "徐", "孙", "胡", "朱", "高", "林", "何", "郭", "马", "罗"
    );

    private static final List<String> EN_US_FIRST_NAMES = List.of(
            "James", "John", "Robert", "Michael", "William", "David", "Richard", "Joseph",
            "Thomas", "Charles", "Mary", "Patricia", "Jennifer", "Linda", "Barbara",
            "Elizabeth", "Susan", "Jessica", "Sarah", "Karen"
    );

    private static final List<String> EN_US_LAST_NAMES = List.of(
            "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller",
            "Davis", "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez",
            "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin"
    );

    @Override
    public String generate(Field field, Object annotation, String lang) {
        MockName mockName = (MockName) annotation;
        String mode = mockName.lang();
        MockName.Type type = mockName.type();

        if (MockRuleResolver.LANG_FIXED.equalsIgnoreCase(mode)) {
            return mockName.fixedValue();
        }

        // FULL_NAME 才走规则路径；FIRST/LAST 走规则结果切片不可靠，交给 legacyGenerate。
        if (type == MockName.Type.FULL_NAME) {
            String rule = MockRuleResolver.resolve(mode, mockName.i18nKey(), mockName.rules());
            if (rule != null && !rule.isEmpty()) {
                String fromRule = RandomUtils.fromRule(rule);
                if (!fromRule.isEmpty()) {
                    return fromRule;
                }
            }
        }

        String actualLang = MockI18nUtil.resolveLang(mode);
        return legacyGenerate(actualLang, type);
    }

    private String legacyGenerate(String lang, MockName.Type type) {
        if (type == MockName.Type.FIRST_NAME) {
            return generateFirstName(lang);
        }
        if (type == MockName.Type.LAST_NAME) {
            return generateLastName(lang);
        }
        return generateFullName(lang);
    }

    private String generateFullName(String lang) {
        if ("zh-CN".equalsIgnoreCase(lang) || "zh_CN".equalsIgnoreCase(lang)) {
            return RandomUtils.random(ZH_CN_LAST_NAMES) + RandomUtils.random(ZH_CN_FIRST_NAMES);
        }
        return RandomUtils.random(EN_US_FIRST_NAMES) + " " + RandomUtils.random(EN_US_LAST_NAMES);
    }

    private String generateFirstName(String lang) {
        if ("zh-CN".equalsIgnoreCase(lang) || "zh_CN".equalsIgnoreCase(lang)) {
            return RandomUtils.random(ZH_CN_FIRST_NAMES);
        }
        return RandomUtils.random(EN_US_FIRST_NAMES);
    }

    private String generateLastName(String lang) {
        if ("zh-CN".equalsIgnoreCase(lang) || "zh_CN".equalsIgnoreCase(lang)) {
            return RandomUtils.random(ZH_CN_LAST_NAMES);
        }
        return RandomUtils.random(EN_US_LAST_NAMES);
    }

    @Override
    public Class<?> getSupportedAnnotationType() {
        return MockName.class;
    }
}
