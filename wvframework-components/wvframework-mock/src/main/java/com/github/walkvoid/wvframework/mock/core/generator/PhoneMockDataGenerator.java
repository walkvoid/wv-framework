package com.github.walkvoid.wvframework.mock.core.generator;

import com.github.walkvoid.wvframework.mock.annotation.MockPhone;
import com.github.walkvoid.wvframework.mock.util.MockI18nUtil;
import com.github.walkvoid.wvframework.utils.RandomUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * 电话号码 Mock 数据生成器
 *
 * @author walkvoid
 */
@Component
public class PhoneMockDataGenerator implements MockDataGenerator<String> {

    private static final String[] ZH_CN_MOBILE_PREFIXES = {
            "130", "131", "132", "133", "134", "135", "136", "137", "138", "139",
            "147", "150", "151", "152", "153", "155", "156", "157", "158", "159",
            "166", "170", "171", "172", "173", "175", "176", "177", "178", "179",
            "180", "181", "182", "183", "184", "185", "186", "187", "188", "189",
            "191", "193", "195", "197", "198", "199"
    };

    private static final String[] ZH_CN_AREA_CODES = {
            "010", "021", "022", "023", "024", "025", "027", "028", "029", "020"
    };

    @Override
    public String generate(Field field, Object annotation, String lang) {
        MockPhone mockPhone = (MockPhone) annotation;
        String actualLang = MockI18nUtil.resolveLang(mockPhone.lang());
        
        MockPhone.Type type = mockPhone.type();
        
        switch (type) {
            case TELEPHONE:
                return generateTelephone(actualLang);
            case FAX:
                return generateFax(actualLang);
            case MOBILE:
            case ANY:
            default:
                return generateMobile(actualLang);
        }
    }

    /**
     * 生成手机号
     */
    private String generateMobile(String lang) {
        if ("zh-CN".equalsIgnoreCase(lang)) {
            String prefix = RandomUtils.random(ZH_CN_MOBILE_PREFIXES);
            String suffix = String.format("%08d", RandomUtils.nextInt(0, 99999999));
            return prefix + suffix;
        } else {
            // 美国手机号格式
            String areaCode = String.format("%03d", RandomUtils.nextInt(200, 999));
            String prefix = String.format("%03d", RandomUtils.nextInt(200, 999));
            String suffix = String.format("%04d", RandomUtils.nextInt(1000, 9999));
            return "(" + areaCode + ") " + prefix + "-" + suffix;
        }
    }

    /**
     * 生成座机号码
     */
    private String generateTelephone(String lang) {
        if ("zh-CN".equalsIgnoreCase(lang)) {
            String areaCode = RandomUtils.random(ZH_CN_AREA_CODES);
            String prefix = String.format("%03d", RandomUtils.nextInt(100, 999));
            String suffix = String.format("%04d", RandomUtils.nextInt(1000, 9999));
            return areaCode + "-" + prefix + "-" + suffix;
        } else {
            String areaCode = String.format("%03d", RandomUtils.nextInt(200, 999));
            String prefix = String.format("%03d", RandomUtils.nextInt(200, 999));
            String suffix = String.format("%04d", RandomUtils.nextInt(1000, 9999));
            return "(" + areaCode + ") " + prefix + "-" + suffix;
        }
    }

    /**
     * 生成传真号码
     */
    private String generateFax(String lang) {
        if ("zh-CN".equalsIgnoreCase(lang)) {
            String areaCode = RandomUtils.random(ZH_CN_AREA_CODES);
            String prefix = String.format("%03d", RandomUtils.nextInt(100, 999));
            String suffix = String.format("%04d", RandomUtils.nextInt(1000, 9999));
            return areaCode + "-" + prefix + "-" + suffix;
        } else {
            String areaCode = String.format("%03d", RandomUtils.nextInt(200, 999));
            String prefix = String.format("%03d", RandomUtils.nextInt(200, 999));
            String suffix = String.format("%04d", RandomUtils.nextInt(1000, 9999));
            return "+1-" + areaCode + "-" + prefix + "-" + suffix;
        }
    }

    @Override
    public Class<?> getSupportedAnnotationType() {
        return MockPhone.class;
    }
}
