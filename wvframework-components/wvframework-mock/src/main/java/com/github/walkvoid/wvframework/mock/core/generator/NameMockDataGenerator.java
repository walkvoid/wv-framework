package com.github.walkvoid.wvframework.mock.core.generator;

import com.github.walkvoid.wvframework.mock.annotation.MockName;
import com.github.walkvoid.wvframework.mock.util.MockI18nUtil;
import com.github.walkvoid.wvframework.utils.RandomUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 姓名 Mock 数据生成器
 *
 * @author walkvoid
 */
@Component
public class NameMockDataGenerator implements MockDataGenerator<String> {

    private String i18nKey(){
        return "mock.name";
    }


    private static final List<String> ZH_CN_FIRST_NAMES = List.of(
            "伟", "芳", "娜", "秀英", "敏", "静", "丽", "强", "磊", "军",
            "洋", "勇", "艳", "杰", "娟", "涛", "明", "超", "秀兰", "霞"
            ,"first[1]+last[2-3]","first[1]+@space+last[1]"
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
        String actualLang = MockI18nUtil.resolveLang(mockName.lang());
        
        MockName.Type type = mockName.type();
        
        switch (type) {
            case FIRST_NAME:
                return generateFirstName(actualLang);
            case LAST_NAME:
                return generateLastName(actualLang);
            case FULL_NAME:
            default:
                return generateFullName(actualLang);
        }
    }

    private String generateFullName(String lang) {
        if ("zh-CN".equalsIgnoreCase(lang)) {
            return RandomUtils.random(ZH_CN_LAST_NAMES) + RandomUtils.random(ZH_CN_FIRST_NAMES);
        } else {
            return RandomUtils.random(EN_US_FIRST_NAMES) + " " + RandomUtils.random(EN_US_LAST_NAMES);
        }
    }

    private String generateFirstName(String lang) {
        if ("zh-CN".equalsIgnoreCase(lang)) {
            return RandomUtils.random(ZH_CN_FIRST_NAMES);
        } else {
            return RandomUtils.random(EN_US_FIRST_NAMES);
        }
    }

    private String generateLastName(String lang) {
        if ("zh-CN".equalsIgnoreCase(lang)) {
            return RandomUtils.random(ZH_CN_LAST_NAMES);
        } else {
            return RandomUtils.random(EN_US_LAST_NAMES);
        }
    }

    @Override
    public Class<?> getSupportedAnnotationType() {
        return MockName.class;
    }
}
