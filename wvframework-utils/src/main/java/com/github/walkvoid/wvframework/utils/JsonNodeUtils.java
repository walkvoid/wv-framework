package com.github.walkvoid.wvframework.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;

/**
 * jackson {@link JsonNode} 读取工具。路径按层级字段传入，避免连续 {@code path().asText()}。
 */
public final class JsonNodeUtils {

    private JsonNodeUtils() {
    }

    /**
     * null、缺失节点或 JSON null。
     */
    public static boolean isAbsent(JsonNode node) {
        return node == null || node.isMissingNode() || node.isNull();
    }

    /**
     * 按字段层级取值，等价于连续 {@code node.path(a).path(b)}。node 为 null 时返回 MissingNode。
     */
    public static JsonNode path(JsonNode node, String... fields) {
        if (node == null) {
            return MissingNode.getInstance();
        }
        if (fields == null || fields.length == 0) {
            return node;
        }
        JsonNode current = node;
        for (String field : fields) {
            if (current == null || current.isMissingNode()) {
                return MissingNode.getInstance();
            }
            current = current.path(field);
        }
        return current;
    }

    /**
     * 路径上的字段是否存在（含 JSON null，不含缺失）。
     */
    public static boolean has(JsonNode node, String... fields) {
        if (node == null || fields == null || fields.length == 0) {
            return false;
        }
        JsonNode current = node;
        for (int i = 0; i < fields.length - 1; i++) {
            if (current == null || !current.has(fields[i])) {
                return false;
            }
            current = current.get(fields[i]);
        }
        return current != null && current.has(fields[fields.length - 1]);
    }

    /**
     * 读取文本，缺失或 JSON null 时返回空串。
     */
    public static String asText(JsonNode node, String... fields) {
        return asTextOr(node, "", fields);
    }

    /**
     * 读取文本，缺失或 JSON null 时返回 {@code defaultValue}。
     */
    public static String asTextOr(JsonNode node, String defaultValue, String... fields) {
        JsonNode target = path(node, fields);
        if (isAbsent(target)) {
            return defaultValue;
        }
        return target.asText(defaultValue);
    }

    /**
     * 按顺序取第一个非空白文本（同级备选字段，如 aibotid / botid）。
     */
    public static String firstText(JsonNode node, String defaultValue, String... fields) {
        if (fields == null) {
            return defaultValue;
        }
        for (String field : fields) {
            String text = asTextOr(node, null, field);
            if (text != null && !text.isBlank()) {
                return text;
            }
        }
        return defaultValue;
    }

    /**
     * 读取 int，缺失或无法转换时返回 {@code defaultValue}。
     */
    public static int asInt(JsonNode node, int defaultValue, String... fields) {
        JsonNode target = path(node, fields);
        if (isAbsent(target)) {
            return defaultValue;
        }
        return target.asInt(defaultValue);
    }

    /**
     * 读取 long，缺失或无法转换时返回 {@code defaultValue}。
     */
    public static long asLong(JsonNode node, long defaultValue, String... fields) {
        JsonNode target = path(node, fields);
        if (isAbsent(target)) {
            return defaultValue;
        }
        return target.asLong(defaultValue);
    }

    /**
     * 读取 boolean，缺失或无法转换时返回 {@code defaultValue}。
     */
    public static boolean asBoolean(JsonNode node, boolean defaultValue, String... fields) {
        JsonNode target = path(node, fields);
        if (isAbsent(target)) {
            return defaultValue;
        }
        return target.asBoolean(defaultValue);
    }
}
