package com.github.walkvoid.wvframework.mock.core.generator;

import com.github.walkvoid.wvframework.mock.annotation.MockIdCardNo;
import com.github.walkvoid.wvframework.mock.util.MockI18nUtil;
import com.github.walkvoid.wvframework.mock.util.RandomUtil;
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
        String actualLang = MockI18nUtil.resolveLang(mockAnnotation.lang());
        
        MockIdCardNo.Type type = mockAnnotation.type();
        
        switch (type) {
            case PASSPORT:
                return generatePassport(actualLang);
            case DRIVER_LICENSE:
                return generateDriverLicense(actualLang);
            case ID_CARD:
            default:
                return generateIdCard(actualLang);
        }
    }

    /**
     * 生成18位身份证号
     */
    private String generateIdCard(String lang) {
        StringBuilder sb = new StringBuilder();
        
        // 1-2位：省代码
        sb.append(RandomUtil.random(ZH_CN_PROVINCES));
        
        // 3-4位：市代码
        sb.append(String.format("%02d", RandomUtil.nextInt(1, 99)));
        
        // 5-6位：区代码
        sb.append(String.format("%02d", RandomUtil.nextInt(1, 99)));
        
        // 7-14位：出生日期
        LocalDate birthDate = RandomUtil.nextDate(
                LocalDate.of(1960, 1, 1),
                LocalDate.of(2005, 12, 31)
        );
        sb.append(birthDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        
        // 15-17位：顺序码
        sb.append(String.format("%03d", RandomUtil.nextInt(1, 999)));
        
        // 18位：校验码
        sb.append(calculateCheckCode(sb.toString()));
        
        return sb.toString();
    }

    /**
     * 计算身份证校验码
     */
    private char calculateCheckCode(String idCard17) {
        int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        char[] checkCodes = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
        
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (idCard17.charAt(i) - '0') * weights[i];
        }
        
        return checkCodes[sum % 11];
    }

    /**
     * 生成护照号
     */
    private String generatePassport(String lang) {
        StringBuilder sb = new StringBuilder();
        // 护照号格式：E + 8位数字
        sb.append("E");
        for (int i = 0; i < 8; i++) {
            sb.append(RandomUtil.nextInt(0, 9));
        }
        return sb.toString();
    }

    /**
     * 生成驾驶证号
     */
    private String generateDriverLicense(String lang) {
        StringBuilder sb = new StringBuilder();
        // 驾驶证号格式：12位数字
        for (int i = 0; i < 12; i++) {
            sb.append(RandomUtil.nextInt(0, 9));
        }
        return sb.toString();
    }

    @Override
    public Class<?> getSupportedAnnotationType() {
        return MockIdCardNo.class;
    }
}
