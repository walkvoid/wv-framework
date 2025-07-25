package com.github.walkvoid.wvframework.tests.utils;

import com.github.walkvoid.wvframework.utils.TemporalUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TemporalUtilsTest {

    @Test
    void testParseBasicPatterns() {
        // 测试标准日期时间字符串
        assertEquals(LocalDateTime.of(2020, 12, 9, 13, 43, 43, 435_000_000),
                TemporalUtils.parse("2020-12-09T13:43:43.435", LocalDateTime.class));
        assertEquals(LocalTime.of(13, 43, 43, 435_000_000),
                TemporalUtils.parse("13:43:43.435", LocalTime.class));
        assertEquals(LocalTime.of(13, 43, 43),
                TemporalUtils.parse("13:43:43", LocalTime.class));
        assertEquals(LocalDateTime.of(2020, 12, 9, 13, 43, 43),
                TemporalUtils.parse("2020/12/09 13:43:43", LocalDateTime.class));
        assertEquals(LocalDate.of(2020, 12, 9),
                TemporalUtils.parse("2020-12-09", LocalDate.class));
        assertEquals(Year.of(2020),
                TemporalUtils.parse("2020", Year.class));
    }

    @Test
    void testParseWithPattern() {
        // 指定pattern解析
        assertEquals(LocalDate.of(2020, 12, 9),
                TemporalUtils.parse("2020/12/09", "yyyy/MM/dd", LocalDate.class));
        assertEquals(LocalTime.of(13, 43, 43),
                TemporalUtils.parse("13:43:43", "HH:mm:ss", LocalTime.class));
    }

    @Test
    void testParseWithNullOrEmpty() {
        // content为空或except为null
        assertThrows(IllegalArgumentException.class, () -> TemporalUtils.parse("", LocalDate.class));
        assertThrows(IllegalArgumentException.class, () -> TemporalUtils.parse("2020-01-01", null));
    }

    @Test
    void testParseUnsupportedPattern() {
        // 不支持的pattern
        assertThrows(UnsupportedOperationException.class, () -> TemporalUtils.parse("2020-01-01-12", LocalDate.class));
    }

    @Test
    void testParseAllTypes() {
        String content = "2020-12-09T13:43:43.435";
        assertNotNull(TemporalUtils.parse(content, LocalDateTime.class));
        assertNotNull(TemporalUtils.parse(content, LocalDate.class));
        assertNotNull(TemporalUtils.parse(content, LocalTime.class));
        assertNotNull(TemporalUtils.parse(content, YearMonth.class));
        assertNotNull(TemporalUtils.parse(content, Year.class));
    }

    @Test
    void testOfSeconds() {
        // 目前ofSeconds未实现，返回null
        assertNull(TemporalUtils.ofSeconds(0L, LocalDateTime.class));
        assertNull(TemporalUtils.ofSeconds(0L, LocalDate.class));
    }

    @Test
    void testParseWithCompatibleMode() {
        // 兼容模式测试
        String content = "2020-12-09";
        LocalDateTime min = TemporalUtils.parse(content, LocalDateTime.class, TemporalUtils.CompatibleMode.MIN);
        LocalDateTime max = TemporalUtils.parse(content, LocalDateTime.class, TemporalUtils.CompatibleMode.MAX);
        assertEquals(LocalDateTime.of(2020, 12, 9, 0, 0, 0), min.withNano(0));
        assertEquals(LocalDateTime.of(2020, 12, 9, 23, 59, 59), max.withNano(999_999_999).withNano(0));
    }

    @Test
    void testMainDemoCases() {
        // 覆盖main方法中的所有case
        List<String> cs = Arrays.asList(
                "2020-12-09T13:43:43.435",
                "13:43:43.435",
                "13:43:43",
                "2020/12/09 13:43:43",
                "2020-12-09",
                "12-09",
                "2020"
        );
        for (String content : cs) {
            assertDoesNotThrow(() -> TemporalUtils.parse(content, LocalDateTime.class));
            assertDoesNotThrow(() -> TemporalUtils.parse(content, LocalDate.class));
            assertDoesNotThrow(() -> TemporalUtils.parse(content, LocalTime.class));
            assertDoesNotThrow(() -> TemporalUtils.parse(content, YearMonth.class));
            assertDoesNotThrow(() -> TemporalUtils.parse(content, Year.class));
        }
    }
} 