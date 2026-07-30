package com.github.walkvoid.wvframework.mock.core.generator;

import com.github.walkvoid.wvframework.mock.annotation.MockAddress;
import com.github.walkvoid.wvframework.mock.util.MockI18nUtil;
import com.github.walkvoid.wvframework.mock.util.RandomUtil;
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

    private static final List<String> EN_US_CITIES = List.of(
            "New York", "Los Angeles", "Chicago", "Houston", "Phoenix", "Philadelphia", 
            "San Antonio", "San Diego", "Dallas", "San Jose", "Austin", "Jacksonville",
            "San Francisco", "Columbus", "Indianapolis", "Fort Worth", "Charlotte"
    );

    private static final List<String> EN_US_STATES = List.of(
            "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA",
            "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD",
            "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ"
    );

    private static final List<String> EN_US_STREETS = List.of(
            "Main St", "Oak Ave", "Maple Dr", "Cedar Ln", "Pine St", "Elm St", 
            "Washington Blvd", "Park Ave", "Lake Dr", "Hill Rd", "River Rd", "Forest Ave"
    );

    @Override
    public String generate(Field field, Object annotation, String lang) {
        MockAddress mockAddress = (MockAddress) annotation;
        String actualLang = MockI18nUtil.resolveLang(mockAddress.lang());
        
        MockAddress.Level level = mockAddress.level();
        
        return generateAddress(actualLang, level);
    }

    private String generateAddress(String lang, MockAddress.Level level) {
        if ("zh-CN".equalsIgnoreCase(lang)) {
            return generateZhCnAddress(level);
        } else {
            return generateEnUsAddress(level);
        }
    }

    private String generateZhCnAddress(MockAddress.Level level) {
        switch (level) {
            case PROVINCE:
                return RandomUtil.random(ZH_CN_PROVINCES);
            case CITY:
                return RandomUtil.random(ZH_CN_CITIES);
            case DISTRICT:
                return RandomUtil.random(ZH_CN_DISTRICTS);
            case DETAIL:
                return generateDetailAddress(ZH_CN_STREETS);
            case FULL:
            default:
                String province = RandomUtil.random(ZH_CN_PROVINCES);
                String city = RandomUtil.random(ZH_CN_CITIES);
                String district = RandomUtil.random(ZH_CN_DISTRICTS);
                String detail = generateDetailAddress(ZH_CN_STREETS);
                return province + city + district + detail;
        }
    }

    private String generateEnUsAddress(MockAddress.Level level) {
        switch (level) {
            case PROVINCE:
            case CITY:
                return RandomUtil.random(EN_US_CITIES);
            case DISTRICT:
                return RandomUtil.random(EN_US_STATES);
            case DETAIL:
                return generateDetailAddress(EN_US_STREETS);
            case FULL:
            default:
                String number = String.valueOf(RandomUtil.nextInt(1, 9999));
                String street = RandomUtil.random(EN_US_STREETS);
                String city = RandomUtil.random(EN_US_CITIES);
                String state = RandomUtil.random(EN_US_STATES);
                String zip = String.format("%05d", RandomUtil.nextInt(10000, 99999));
                return number + " " + street + ", " + city + ", " + state + " " + zip;
        }
    }

    private String generateDetailAddress(List<String> streets) {
        String number = String.valueOf(RandomUtil.nextInt(1, 999));
        String street = RandomUtil.random(streets);
        String unit = RandomUtil.nextBoolean() ? "" + RandomUtil.nextInt(1, 30) + "0" + RandomUtil.nextInt(1, 9) + "室" : "";
        return number + street + unit;
    }

    @Override
    public Class<?> getSupportedAnnotationType() {
        return MockAddress.class;
    }
}
