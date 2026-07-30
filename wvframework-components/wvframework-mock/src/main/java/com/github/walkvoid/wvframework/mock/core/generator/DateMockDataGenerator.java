package com.github.walkvoid.wvframework.mock.core.generator;

import com.github.walkvoid.wvframework.mock.annotation.MockDate;
import com.github.walkvoid.wvframework.mock.util.MockI18nUtil;
import com.github.walkvoid.wvframework.utils.RandomUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日期时间 Mock 数据生成器
 *
 * @author walkvoid
 */
@Component
public class DateMockDataGenerator implements MockDataGenerator<String> {

    @Override
    public String generate(Field field, Object annotation, String lang) {
        MockDate mockDate = (MockDate) annotation;
        String actualLang = MockI18nUtil.resolveLang(mockDate.lang());
        
        String format = mockDate.format();
        boolean withTime = mockDate.withTime();
        
        // 解析日期范围
        LocalDate from = parseDate(mockDate.from(), -5);
        LocalDate to = parseDate(mockDate.to(), 0);
        
        if (withTime) {
            LocalDateTime fromDateTime = from.atStartOfDay();
            LocalDateTime toDateTime = to.atTime(23, 59, 59);
            LocalDateTime randomDateTime = RandomUtils.nextDateTime(fromDateTime, toDateTime);
            return randomDateTime.format(DateTimeFormatter.ofPattern(format));
        } else {
            LocalDate randomDate = RandomUtils.nextDate(from, to);
            return randomDate.format(DateTimeFormatter.ofPattern(format));
        }
    }

    /**
     * 解析日期字符串
     */
    private LocalDate parseDate(String dateStr, int yearOffset) {
        if (dateStr == null || dateStr.isEmpty()) {
            LocalDate now = LocalDate.now();
            return now.plusYears(yearOffset);
        }
        
        try {
            if (dateStr.contains(" ")) {
                return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toLocalDate();
            } else {
                return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
        } catch (Exception e) {
            return LocalDate.now().plusYears(yearOffset);
        }
    }

    @Override
    public Class<?> getSupportedAnnotationType() {
        return MockDate.class;
    }
}
