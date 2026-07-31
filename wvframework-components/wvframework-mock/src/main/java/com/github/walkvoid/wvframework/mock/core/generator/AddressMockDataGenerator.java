package com.github.walkvoid.wvframework.mock.core.generator;
import com.github.walkvoid.wvframework.mock.annotation.MockAddress;
import com.github.walkvoid.wvframework.mock.util.MockI18nUtil;
import com.github.walkvoid.wvframework.mock.util.MockRuleResolver;
import com.github.walkvoid.wvframework.utils.RandomUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 地址 Mock 数据生成器
 *
 * @author walkvoid
 */
@Component
public class AddressMockDataGenerator implements MockDataGenerator<String> {

    private static final List<String> ZH_CN_PROVINCES = List.of(
            "北京市", "天津市", "上海市", "重庆市", "河北省", "山西省", "辽宁省", "吉林省",
            "黑龙江省", "江苏省", "浙江省", "安徽省", "福建省", "江西省", "山东省", "河南省",
            "湖北省", "湖南省", "广东省", "海南省", "四川省", "贵州省", "云南省", "陕西省",
            "甘肃省", "青海省", "台湾省", "内蒙古自治区", "广西壮族自治区", "西藏自治区",
            "宁夏回族自治区", "新疆维吾尔自治区", "香港特别行政区", "澳门特别行政区"
    );

    private static final List<String> ZH_CN_CITIES = List.of(
            "北京市", "上海市", "广州市", "深圳市", "杭州市", "南京市", "武汉市", "成都市",
            "重庆市", "西安市", "苏州市", "天津市", "长沙市", "郑州市", "沈阳市", "青岛市",
            "济南市", "大连市", "厦门市", "宁波市", "合肥市", "昆明市", "兰州市", "石家庄市"
    );

    private static final List<String> ZH_CN_DISTRICTS = List.of(
            "朝阳区", "海淀区", "西城区", "东城区", "浦东新区", "徐汇区", "黄浦区", "天河区",
            "越秀区", "南山区", "福田区", "龙岗区", "玄武区", "秦淮区", "江宁区", "西湖区",
            "拱墅区", "滨江区", "锦江区", "武侯区", "雁塔区", "碑林区", "和平区", "南开区"
    );

    private static final List<String> ZH_CN_STREETS = List.of(
            "人民路", "中山路", "建设路", "解放路", "文化路", "新华路", "和平路", "胜利路",
            "民主路", "胜利街", "友谊街", "团结路", "幸福路", "光明路", "振兴路", "朝阳街"
    );

    private static final List<String> EN_US_STATES = List.of(
            "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA",
            "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD",
            "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ",
            "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC",
            "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"
    );

    private static final List<String> EN_US_CITIES = List.of(
            "New York", "Los Angeles", "Chicago", "Houston", "Phoenix", "Philadelphia",
            "San Antonio", "San Diego", "Dallas", "San Jose", "Austin", "Jacksonville",
            "San Francisco", "Columbus", "Indianapolis", "Fort Worth", "Charlotte",
            "Seattle", "Denver", "Washington", "Boston", "Nashville", "Baltimore",
            "Oklahoma City", "Portland", "Las Vegas", "Detroit", "Memphis", "Louisville"
    );

    private static final List<String> EN_US_NEIGHBORHOODS = List.of(
            "Manhattan", "Brooklyn", "Queens", "Bronx", "Staten Island",
            "Downtown", "Midtown", "Hollywood", "Beverly Hills", "Chelsea",
            "SoHo", "Tribeca", "Mission District", "Capitol Hill", "French Quarter",
            "The Loop", "Magnificent Mile", "Back Bay", "Georgetown", "Old Town"
    );

    private static final List<String> EN_US_STREETS = List.of(
            "Main St", "Oak Ave", "Maple Dr", "Cedar Ln", "Pine St", "Elm St",
            "Washington Blvd", "Park Ave", "Lake Dr", "Hill Rd", "River Rd", "Forest Ave"
    );

    @Override
    public String generate(Field field, Object annotation, String lang) {
        MockAddress mockAddress = (MockAddress) annotation;
        String mode = mockAddress.lang();
        MockAddress.Level level = mockAddress.level();

        if (MockRuleResolver.LANG_FIXED.equalsIgnoreCase(mode)) {
            return mockAddress.fixedValue();
        }

        // LANG_RULES 走用户自定义规则（不受 level 约束）；
        // LANG_AUTO / 具体 locale 走 i18n 规则，但内置 i18n 规则对应 FULL 地址，
        // 当 level != FULL 时直接走 legacyGenerate，避免规则路径覆盖 level 语义。
        boolean useRulePath = MockRuleResolver.LANG_RULES.equalsIgnoreCase(mode)
                || level == MockAddress.Level.FULL;
        if (useRulePath) {
            String rule = MockRuleResolver.resolve(mode, mockAddress.i18nKey(), mockAddress.rules());
            if (rule != null && !rule.isEmpty()) {
                String fromRule = RandomUtils.fromRule(rule);
                if (!fromRule.isEmpty()) {
                    return fromRule;
                }
            }
        }

        String actualLang = MockI18nUtil.resolveLang(mode);
        return legacyGenerate(actualLang, level);
    }

    private String legacyGenerate(String lang, MockAddress.Level level) {
        if ("zh-CN".equalsIgnoreCase(lang) || "zh_CN".equalsIgnoreCase(lang)) {
            return generateZhCnAddress(level);
        }
        return generateEnUsAddress(level);
    }

    private String generateZhCnAddress(MockAddress.Level level) {
        switch (level) {
            case PROVINCE:
                return RandomUtils.random(ZH_CN_PROVINCES);
            case CITY:
                return RandomUtils.random(ZH_CN_CITIES);
            case DISTRICT:
                return RandomUtils.random(ZH_CN_DISTRICTS);
            case DETAIL:
                return generateDetailAddress(ZH_CN_STREETS);
            case FULL:
            default:
                String province = RandomUtils.random(ZH_CN_PROVINCES);
                String city = RandomUtils.random(ZH_CN_CITIES);
                String district = RandomUtils.random(ZH_CN_DISTRICTS);
                String detail = generateDetailAddress(ZH_CN_STREETS);
                return province + city + district + detail;
        }
    }

    private String generateEnUsAddress(MockAddress.Level level) {
        switch (level) {
            case PROVINCE:
                return RandomUtils.random(EN_US_STATES);
            case CITY:
                return RandomUtils.random(EN_US_CITIES);
            case DISTRICT:
                return RandomUtils.random(EN_US_NEIGHBORHOODS);
            case DETAIL:
                return generateDetailAddress(EN_US_STREETS);
            case FULL:
            default:
                String number = String.valueOf(RandomUtils.nextInt(1, 9999));
                String street = RandomUtils.random(EN_US_STREETS);
                String city = RandomUtils.random(EN_US_CITIES);
                String state = RandomUtils.random(EN_US_STATES);
                String zip = String.format("%05d", RandomUtils.nextInt(10000, 99999));
                return number + " " + street + ", " + city + ", " + state + " " + zip;
        }
    }

    private String generateDetailAddress(List<String> streets) {
        String number = String.valueOf(RandomUtils.nextInt(1, 999));
        String street = RandomUtils.random(streets);
        String unit = RandomUtils.nextBoolean() ? "" + RandomUtils.nextInt(1, 30) + "0" + RandomUtils.nextInt(1, 9) + "室" : "";
        return number + street + unit;
    }

    @Override
    public Class<?> getSupportedAnnotationType() {
        return MockAddress.class;
    }
}