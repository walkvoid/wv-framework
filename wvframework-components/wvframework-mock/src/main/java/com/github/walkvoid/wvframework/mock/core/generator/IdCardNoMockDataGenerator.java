package com.github.walkvoid.wvframework.mock.core.generator;

import com.github.walkvoid.wvframework.mock.annotation.MockIdCardNo;
import com.github.walkvoid.wvframework.mock.util.MockRuleResolver;
import com.github.walkvoid.wvframework.utils.RandomUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 身份证号 Mock 数据生成器
 *
 * @author walkvoid
 */
@Component
public class IdCardNoMockDataGenerator implements MockDataGenerator<String> {

    private static final String[] ZH_CN_PROVINCES = {
            "11", "12", "13", "14", "15", "21", "22", "23", "31", "32",
            "33", "34", "35", "36", "37", "41", "42", "43", "44", "45",
            "46", "50", "51", "52", "53", "54", "61", "62", "63", "64", "65"
    };

    @Override
    public String generate(Field field, Object annotation, String lang) {
        MockIdCardNo mockAnnotation = (MockIdCardNo) annotation;
        String mode = mockAnnotation.lang();
        MockIdCardNo.Type type = mockAnnotation.type();

        if (MockRuleResolver.LANG_FIXED.equalsIgnoreCase(mode)) {
            return mockAnnotation.fixedValue();
        }

        String rule = MockRuleResolver.resolve(mode, mockAnnotation.i18nKey(), mockAnnotation.rules());
        if (rule != null && !rule.isEmpty()) {
            String fromRule = RandomUtils.fromRule(rule);
            if (!fromRule.isEmpty()) {
                return fromRule;
            }
        }

        return legacyGenerate(type);
    }

    private String legacyGenerate(MockIdCardNo.Type type) {
        switch (type) {
            case PASSPORT:
                return generatePassport();
            case DRIVER_LICENSE:
                return generateDriverLicense();
            case ID_CARD:
            default:
                return generateIdCard();
        }
    }

    /**
     * 生成18位身份证号
     */
    private String generateIdCard() {
        StringBuilder sb = new StringBuilder();

        sb.append(RandomUtils.random(ZH_CN_PROVINCES));
        sb.append(String.format("%02d", RandomUtils.nextInt(1, 99)));
        sb.append(String.format("%02d", RandomUtils.nextInt(1, 99)));

        LocalDate birthDate = RandomUtils.nextDate(
                LocalDate.of(1960, 1, 1),
                LocalDate.of(2005, 12, 31)
        );
        sb.append(birthDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        sb.append(String.format("%03d", RandomUtils.nextInt(1, 999)));
        sb.append(calculateCheckCode(sb.toString()));
        return sb.toString();
    }

    private char calculateCheckCode(String idCard17) {
        int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        char[] checkCodes = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (idCard17.charAt(i) - '0') * weights[i];
        }
        return checkCodes[sum % 11];
    }

    private String generatePassport() {
        StringBuilder sb = new StringBuilder();
        sb.append("E");
        for (int i = 0; i < 8; i++) {
            sb.append(RandomUtils.nextInt(0, 9));
        }
        return sb.toString();
    }

    private String generateDriverLicense() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            sb.append(RandomUtils.nextInt(0, 9));
        }
        return sb.toString();
    }

    @Override
    public Class<?> getSupportedAnnotationType() {
        return MockIdCardNo.class;
    }
}
