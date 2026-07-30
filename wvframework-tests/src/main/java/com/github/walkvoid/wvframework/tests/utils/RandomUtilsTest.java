package com.github.walkvoid.wvframework.tests.utils;

import com.github.walkvoid.wvframework.utils.RandomUtils;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RandomUtils 单元测试
 */
class RandomUtilsTest {

    // ======================== fromRule ========================

    @Test
    void fromRule_nullOrEmpty() {
        assertEquals("", RandomUtils.fromRule(null));
        assertEquals("", RandomUtils.fromRule(""));
    }

    @RepeatedTest(10)
    void fromRule_keywordExactCount() {
        String result = RandomUtils.fromRule("@digits[4]");
        assertNotNull(result);
        assertEquals(4, result.length());
        for (char c : result.toCharArray()) {
            assertTrue(Character.isDigit(c), "expected digit but got: " + c);
        }
    }

    @RepeatedTest(10)
    void fromRule_keywordRange() {
        String result = RandomUtils.fromRule("@upper[2-4]");
        assertTrue(result.length() >= 2 && result.length() <= 4,
                "expected 2-4 length but got: " + result.length());
        for (char c : result.toCharArray()) {
            assertTrue(c >= 'A' && c <= 'Z', "expected uppercase but got: " + c);
        }
    }

    @RepeatedTest(10)
    void fromRule_optionsExactCount() {
        String result = RandomUtils.fromRule("{com,cn}[1]");
        assertNotNull(result);
        assertTrue(result.equals("com") || result.equals("cn"),
                "expected 'com' or 'cn' but got: " + result);
    }

    @RepeatedTest(10)
    void fromRule_optionsRange() {
        String result = RandomUtils.fromRule("{a,b,c}[2-4]");
        assertTrue(result.length() >= 2 && result.length() <= 4,
                "expected 2-4 length but got: " + result.length());
        for (char c : result.toCharArray()) {
            assertTrue(c == 'a' || c == 'b' || c == 'c',
                    "expected a, b, or c but got: " + c);
        }
    }

    @RepeatedTest(10)
    void fromRule_mixedSyntax() {
        String result = RandomUtils.fromRule("@upper[2]{_,-}[1]@digits[3]");
        assertNotNull(result);
        assertEquals(6, result.length(), "expected 2+1+3=6 chars but got: " + result);
        assertTrue(Character.isUpperCase(result.charAt(0)), "first 2 chars should be uppercase");
        assertTrue(Character.isUpperCase(result.charAt(1)), "first 2 chars should be uppercase");
        assertTrue(result.charAt(2) == '_' || result.charAt(2) == '-', "separator should be _ or -");
        assertTrue(Character.isDigit(result.charAt(3)), "last 3 chars should be digits");
        assertTrue(Character.isDigit(result.charAt(4)), "last 3 chars should be digits");
        assertTrue(Character.isDigit(result.charAt(5)), "last 3 chars should be digits");
    }

    @Test
    void fromRule_unknownKeyword() {
        // Unknown keyword should be silently skipped, producing empty output
        String result = RandomUtils.fromRule("@unknown[4]");
        assertEquals("", result);
    }

    @Test
    void fromRule_unknownKeywordMixed() {
        // Unknown keyword skipped, valid keyword still produces output
        String result = RandomUtils.fromRule("@unknown[4]@digits[3]");
        assertEquals(3, result.length());
    }

    @Test
    void fromRule_minEqualsMax() {
        String result = RandomUtils.fromRule("@digits[3-3]");
        assertEquals(3, result.length());
    }

    @Test
    void fromRule_zeroRange() {
        // [0-0] should produce empty
        String result = RandomUtils.fromRule("@digits[0-0]");
        assertEquals("", result);
    }

    @Test
    void fromRule_multipleSegments() {
        String result = RandomUtils.fromRule(
                "{A,B}[1]@digits[2]{X,Y}[1]@upper[1]@digits[1]");
        assertNotNull(result);
        assertEquals(6, result.length());
    }

    @Test
    void fromRule_space() {
        String result = RandomUtils.fromRule("@space[4]");
        assertEquals("    ", result);
    }

    @RepeatedTest(10)
    void fromRule_hyphenRange() {
        String result = RandomUtils.fromRule("@hyphen[1-2]");
        assertTrue(result.length() >= 1 && result.length() <= 2);
        for (char c : result.toCharArray()) {
            assertEquals('-', c);
        }
    }

    @Test
    void fromRule_underscoreExact() {
        String result = RandomUtils.fromRule("@underscore[3]");
        assertEquals("___", result);
    }

    @Test
    void fromRule_mixedLiterals() {
        String result = RandomUtils.fromRule("@space[2]@hyphen[2]@space[2]");
        assertEquals(6, result.length());
        assertEquals("  --  ", result);
    }

    // ======================== nextInt ========================

    @RepeatedTest(20)
    void nextInt_inRange() {
        int val = RandomUtils.nextInt(5, 10);
        assertTrue(val >= 5 && val <= 10);
    }

    @Test
    void nextInt_minEqualsMax() {
        assertEquals(5, RandomUtils.nextInt(5, 5));
    }

    // ======================== nextLong ========================

    @RepeatedTest(20)
    void nextLong_inRange() {
        long val = RandomUtils.nextLong(100, 200);
        assertTrue(val >= 100 && val <= 200);
    }

    // ======================== nextDouble ========================

    @RepeatedTest(20)
    void nextDouble_inRange() {
        double val = RandomUtils.nextDouble(1.0, 5.0);
        assertTrue(val >= 1.0 && val < 5.0);
    }

    // ======================== nextDecimal ========================

    @RepeatedTest(10)
    void nextDecimal_scale() {
        BigDecimal val = RandomUtils.nextDecimal(1, 100, 3);
        assertNotNull(val);
        assertEquals(3, val.scale());
    }

    // ======================== nextString ========================

    @RepeatedTest(10)
    void nextString_lengthAndCharset() {
        String val = RandomUtils.nextString(10, "alpha");
        assertEquals(10, val.length());
        for (char c : val.toCharArray()) {
            assertTrue(Character.isLetter(c));
        }
    }

    @Test
    void nextString_numeric() {
        String val = RandomUtils.nextString(8, "numeric");
        assertEquals(8, val.length());
        for (char c : val.toCharArray()) {
            assertTrue(Character.isDigit(c));
        }
    }

    // ======================== random(array) ========================

    @RepeatedTest(10)
    void randomArray_inChoices() {
        String[] choices = {"A", "B", "C"};
        String val = RandomUtils.random(choices);
        assertTrue(Arrays.asList(choices).contains(val));
    }

    @Test
    void randomArray_nullOrEmpty() {
        assertNull(RandomUtils.random((String[]) null));
        assertNull(RandomUtils.random(new String[0]));
    }

    // ======================== random(list) ========================

    @RepeatedTest(10)
    void randomList_inChoices() {
        String val = RandomUtils.random(Arrays.asList("X", "Y", "Z"));
        assertTrue(Arrays.asList("X", "Y", "Z").contains(val));
    }

    @Test
    void randomList_nullOrEmpty() {
        assertNull(RandomUtils.random((java.util.List<String>) null));
        assertNull(RandomUtils.random(Collections.emptyList()));
    }

    // ======================== randomFromString ========================

    @RepeatedTest(10)
    void randomFromString_inCommaList() {
        String val = RandomUtils.randomFromString("hello,world,test");
        assertTrue("hello".equals(val) || "world".equals(val) || "test".equals(val));
    }

    @Test
    void randomFromString_nullOrEmpty() {
        assertEquals("", RandomUtils.randomFromString(null));
        assertEquals("", RandomUtils.randomFromString(""));
    }

    // ======================== nextDate / nextDateTime ========================

    @RepeatedTest(10)
    void nextDate_inRange() {
        LocalDate from = LocalDate.of(2020, 1, 1);
        LocalDate to = LocalDate.of(2020, 12, 31);
        LocalDate val = RandomUtils.nextDate(from, to);
        assertFalse(val.isBefore(from));
        assertFalse(val.isAfter(to));
    }

    @RepeatedTest(10)
    void nextDateTime_inRange() {
        LocalDateTime from = LocalDateTime.of(2020, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2020, 12, 31, 23, 59);
        LocalDateTime val = RandomUtils.nextDateTime(from, to);
        assertFalse(val.isBefore(from));
        assertFalse(val.isAfter(to));
    }

    // ======================== formatDate / formatDateTime ========================

    @Test
    void formatDate_standard() {
        LocalDate date = LocalDate.of(2025, 3, 15);
        assertEquals("2025-03-15", RandomUtils.formatDate(date, "yyyy-MM-dd"));
    }

    @Test
    void formatDateTime_standard() {
        LocalDateTime dt = LocalDateTime.of(2025, 3, 15, 8, 30, 0);
        assertEquals("2025-03-15 08:30:00", RandomUtils.formatDateTime(dt, "yyyy-MM-dd HH:mm:ss"));
    }

    // ======================== uuid ========================

    @RepeatedTest(5)
    void uuid_length32_noDashes() {
        String val = RandomUtils.uuid();
        assertEquals(32, val.length());
        assertFalse(val.contains("-"));
    }

    // ======================== nextBoolean ========================

    @RepeatedTest(50)
    void nextBoolean_returnsBoolean() {
        // 50 iterations, at some point should produce both true and false
        RandomUtils.nextBoolean(); // just verify no exception
    }

    // ======================== nextBytes ========================

    @Test
    void nextBytes_length() {
        byte[] bytes = RandomUtils.nextBytes(16);
        assertEquals(16, bytes.length);
    }

    // ======================== parseCount (package-private) ========================

    @Test
    void parseCount_exact() {
        assertEquals(5, RandomUtils.parseCount("5"));
    }

    @Test
    void parseCount_range() {
        // With range, result should be within [min, max]
        for (int i = 0; i < 20; i++) {
            int count = RandomUtils.parseCount("3-7");
            assertTrue(count >= 3 && count <= 7);
        }
    }
}
