package com.github.walkvoid.wvframework.mock.util;

import com.github.walkvoid.wvframework.utils.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Mock 规则解析器
 *
 * <p>根据 {@code @MockXxx} 注解的 lang 模式从注解属性或多语言文件中解析出最终的随机规则字符串，
 * 然后由调用方传入 {@link RandomUtils#fromRule(String)} 生成具体的 Mock 数据。</p>
 *
 * <p>支持的五种 lang 模式：
 * <ul>
 *   <li>{@link #LANG_FIXED} —— 固定值，由调用方直接使用 {@code fixedValue()}（解析器不参与）</li>
 *   <li>{@link #LANG_RULES} —— 直接使用注解 {@code rules()} 中的某一条规则（随机抽取）</li>
 *   <li>{@link #LANG_AUTO} —— 根据当前 locale 加载 {@code i18n/mock/{basename}_{locale}.properties}，
 *       从第一行 {@code rules} 属性中随机抽取一条模板，把 {@code keyword[N]} 占位符展开为
 *       {@code "{choices}[N]"} 后返回</li>
 *   <li>{@link #LANG_GENER} —— 委托给业务方自定义的 generator（解析器不参与）</li>
 *   <li>{@link #LANG_NONE} 或其它 —— 走注解方提供的兜底逻辑（解析器返回 {@code null}）</li>
 * </ul>
 *
 * <p>具体 locale（如 {@code "zh-CN"}、{@code "en-US"}）走和 {@link #LANG_AUTO} 相同的加载逻辑，
 * 只是 locale 不再从当前环境推断。</p>
 *
 * <p>i18n 文件解析结果会按 {@code basename + "@" + locale} 缓存，避免每次 Mock 生成都重读文件。</p>
 *
 * @author walkvoid
 */
public final class MockRuleResolver {

    private static final Logger logger = LoggerFactory.getLogger(MockRuleResolver.class);

    /** lang 模式：自动根据当前环境的多语言配置加载规则 */
    public static final String LANG_AUTO = "AUTO";
    /** lang 模式：直接使用注解上的 {@code rules} 属性（不读取多语言文件） */
    public static final String LANG_RULES = "RULES";
    /** lang 模式：固定值，由 {@code fixedValue()} 提供 */
    public static final String LANG_FIXED = "FIXED";
    /** lang 模式：由业务方自定义的 generator 处理 */
    public static final String LANG_GENER = "GENER";
    /** lang 模式：不使用规则，走注解方提供的兜底逻辑 */
    public static final String LANG_NONE = "NONE";

    /** i18n 资源文件所在 classpath 目录 */
    public static final String RESOURCE_BASE = "i18n/mock/";

    /**
     * 匹配模板中 {@code keyword[N]} 或 {@code keyword[N-M]} 形式的占位符。
     * 替换步骤会手动检查前后字符，避免误处理：
     * <ul>
     *   <li>{@code @keyword[N]} —— 由 {@code fromRule} 直接识别的字符集占位符，不展开</li>
     *   <li>{@code {choices}[N]} —— 已展开的候选项，不再次展开</li>
     * </ul>
     */
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("(\\w+)\\[([^\\]]+)\\]");

    /** 缓存缺失结果，避免反复访问 classpath */
    private static final Object NULL_MARKER = new Object();

    /** basename + "@" + locale -> 解析后的规则 / {@link #NULL_MARKER} */
    private static final ConcurrentMap<String, Object> CACHE = new ConcurrentHashMap<>();

    private MockRuleResolver() {
    }

    /**
     * 解析 lang 模式，提取规则字符串。
     *
     * @param lang             注解上的 lang 字符串（不区分大小写）
     * @param i18nKey          注解上的 i18nKey（会自动剥离前缀，如 {@code "mock."} / {@code "mocke."}）
     * @param annotationRules  注解上的 rules 数组（仅 {@link #LANG_RULES} 模式使用）
     * @return 解析后的规则字符串；如果当前模式不需要规则解析（如 FIXED/GENER/NONE/未匹配），
     *         或未找到任何可用规则，返回 {@code null}（调用方需要走兜底逻辑）
     */
    public static String resolve(String lang, String i18nKey, String[] annotationRules) {
        if (lang == null) {
            return null;
        }
        String mode = lang.trim();
        if (mode.isEmpty()) {
            return null;
        }

        if (LANG_RULES.equalsIgnoreCase(mode)) {
            if (annotationRules == null || annotationRules.length == 0) {
                return null;
            }
            return annotationRules[RandomUtils.nextInt(0, annotationRules.length - 1)];
        }

        if (LANG_AUTO.equalsIgnoreCase(mode)) {
            String locale = MockI18nUtil.getCurrentLang();
            return resolveFromI18n(i18nKey, locale);
        }

        if (LANG_FIXED.equalsIgnoreCase(mode)
                || LANG_GENER.equalsIgnoreCase(mode)
                || LANG_NONE.equalsIgnoreCase(mode)) {
            return null;
        }

        // 其它值按具体 locale 处理（如 "zh-CN"、"en-US"）
        return resolveFromI18n(i18nKey, MockI18nUtil.normalizeLang(mode));
    }

    /**
     * 从 i18n/mock/{basename}_{locale}.properties 加载规则并随机抽取一条展开。
     * 找不到可用文件时返回 {@code null}。
     */
    private static String resolveFromI18n(String i18nKey, String locale) {
        String baseName = normalizeI18nKey(i18nKey);
        if (baseName.isEmpty()) {
            return null;
        }
        LoadedRules loaded = loadRules(baseName, locale);
        if (loaded == null || loaded.templates.isEmpty()) {
            return null;
        }
        String template = loaded.templates.get(RandomUtils.nextInt(0, loaded.templates.size() - 1));
        return expandTemplate(template, loaded.choices);
    }

    /**
     * 把 {@code "mock.name"} / {@code "mocke.name"} 等前缀剥离，得到用于文件命名的 basename。
     */
    public static String normalizeI18nKey(String i18nKey) {
        if (i18nKey == null) {
            return "";
        }
        String trimmed = i18nKey.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        int dot = trimmed.indexOf('.');
        if (dot >= 0 && dot + 1 < trimmed.length()) {
            return trimmed.substring(dot + 1);
        }
        return trimmed;
    }

    private static LoadedRules loadRules(String baseName, String locale) {
        String cacheKey = baseName + "@" + locale;
        Object cached = CACHE.get(cacheKey);
        if (cached == NULL_MARKER) {
            return null;
        }
        if (cached instanceof LoadedRules) {
            return (LoadedRules) cached;
        }
        LoadedRules loaded = tryLoadFromClasspath(baseName, locale);
        CACHE.put(cacheKey, loaded != null ? loaded : NULL_MARKER);
        return loaded;
    }

    private static LoadedRules tryLoadFromClasspath(String baseName, String locale) {
        for (String tryLocale : localeFallbacks(locale)) {
            String fileName = RESOURCE_BASE + baseName + "_" + tryLocale + ".properties";
            try (InputStream in = MockRuleResolver.class.getClassLoader().getResourceAsStream(fileName)) {
                if (in == null) {
                    continue;
                }
                Properties props = new Properties();
                props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
                String rulesValue = props.getProperty("rules", "");
                if (rulesValue == null || rulesValue.trim().isEmpty()) {
                    logger.debug("Mock rule file {} has empty 'rules' property", fileName);
                    continue;
                }
                List<String> templates = splitTemplates(rulesValue);
                Map<String, String> choices = new LinkedHashMap<>();
                for (String key : props.stringPropertyNames()) {
                    if (!"rules".equalsIgnoreCase(key)) {
                        choices.put(key, props.getProperty(key, ""));
                    }
                }
                if (templates.isEmpty()) {
                    continue;
                }
                return new LoadedRules(templates, choices);
            } catch (Exception e) {
                logger.warn("Failed to load mock rule file {}", fileName, e);
            }
        }
        logger.debug("Mock rule file not found for basename={} locale={}", baseName, locale);
        return null;
    }

    /**
     * locale 降级链：{@code zh-CN} -> {@code zh_CN} -> {@code zh} -> {@code ""}。
     */
    static List<String> localeFallbacks(String locale) {
        List<String> result = new ArrayList<>(3);
        if (locale == null || locale.isEmpty()) {
            return result;
        }
        String underscore = locale.replace("-", "_");
        result.add(underscore);
        int dash = locale.indexOf('-');
        if (dash > 0) {
            result.add(locale.substring(0, dash));
        }
        return result;
    }

    /**
     * 拆分 {@code rules} 属性中逗号分隔的模板，保留 {@code {...}} 内的逗号。
     */
    static List<String> splitTemplates(String rulesValue) {
        List<String> templates = new ArrayList<>();
        int depth = 0;
        StringBuilder cur = new StringBuilder();
        for (int i = 0; i < rulesValue.length(); i++) {
            char c = rulesValue.charAt(i);
            if (c == '{') {
                depth++;
            } else if (c == '}') {
                depth = Math.max(0, depth - 1);
            }
            if (c == ',' && depth == 0) {
                String t = cur.toString().trim();
                if (!t.isEmpty()) {
                    templates.add(t);
                }
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        String tail = cur.toString().trim();
        if (!tail.isEmpty()) {
            templates.add(tail);
        }
        return templates;
    }

    /**
     * 把模板中的 {@code keyword[N]} 替换为 {@code "{choices}[N]"}；
     * 跳过 {@code @keyword[N]}（字符集占位符）与 {@code {choices}[N]}（已展开）。
     */
    static String expandTemplate(String template, Map<String, String> choices) {
        if (template == null || template.isEmpty() || choices == null || choices.isEmpty()) {
            return template;
        }
        Matcher m = PLACEHOLDER_PATTERN.matcher(template);
        StringBuilder out = new StringBuilder(template.length() + 16);
        int last = 0;
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            String key = m.group(1);
            String spec = m.group(2);

            if (start > 0) {
                char prev = template.charAt(start - 1);
                if (prev == '@' || prev == '{') {
                    continue;
                }
            }
            if (end < template.length() && template.charAt(end) == '}') {
                continue;
            }

            String values = choices.get(key);
            if (values == null || values.isEmpty()) {
                continue;
            }

            out.append(template, last, start);
            out.append('{').append(values).append("}[").append(spec).append("]");
            last = end;
        }
        out.append(template, last, template.length());
        return out.toString();
    }

    /** 清空缓存，主要用于测试。 */
    public static void clearCache() {
        CACHE.clear();
    }

    private static final class LoadedRules {
        final List<String> templates;
        final Map<String, String> choices;

        LoadedRules(List<String> templates, Map<String, String> choices) {
            this.templates = templates;
            this.choices = choices;
        }
    }
}
