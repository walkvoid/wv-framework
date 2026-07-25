package com.github.walkvoid.wvframework.httplog.resolver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 敏感字段脱敏器
 *
 * <p>负责请求体/响应体中 JSON 内容的脱敏处理：
 * <ol>
 *   <li>将 Body 字符串解析为 JSON 树</li>
 *   <li>遍历 JSON 字段，匹配 maskFields 列表中的字段名</li>
 *   <li>将匹配字段的值替换为 "***"</li>
 *   <li>序列化回字符串</li>
 *   <li>非 JSON 内容不做脱敏，直接截断</li>
 * </ol>
 *
 * @author walkvoid
 */
public class SensitiveFieldMasker {

    private static final Logger log = LoggerFactory.getLogger(SensitiveFieldMasker.class);

    private static final String MASK_VALUE = "***";

    private final ObjectMapper objectMapper;

    private final Set<String> maskFields;

    public SensitiveFieldMasker(List<String> maskFields) {
        this.objectMapper = new ObjectMapper();
        this.maskFields = maskFields != null ? Set.copyOf(maskFields) : Collections.emptySet();
    }

    public SensitiveFieldMasker(Set<String> maskFields) {
        this.objectMapper = new ObjectMapper();
        this.maskFields = maskFields != null ? Set.copyOf(maskFields) : Collections.emptySet();
    }

    /**
     * 对 JSON 字符串进行脱敏处理
     *
     * @param body      原始 JSON 字符串
     * @param maxLen    最大长度，超出截断
     * @param extraMask 额外的脱敏字段（来自注解）
     * @return 脱敏后的字符串
     */
    public String mask(String body, int maxLen, Set<String> extraMask) {
        if (body == null || body.isEmpty()) {
            return body;
        }

        // 合并脱敏字段
        Set<String> allMaskFields = this.maskFields;
        if (extraMask != null && !extraMask.isEmpty()) {
            allMaskFields = new java.util.HashSet<>(this.maskFields);
            allMaskFields.addAll(extraMask);
        }

        String result = body;

        // 尝试 JSON 脱敏
        if (!allMaskFields.isEmpty()) {
            try {
                JsonNode jsonNode = objectMapper.readTree(body);
                if (jsonNode.isObject()) {
                    maskObjectNode((ObjectNode) jsonNode, allMaskFields);
                    result = objectMapper.writeValueAsString(jsonNode);
                }
            } catch (Exception e) {
                // 非 JSON 内容，不做脱敏处理
                log.trace("Body is not valid JSON, skip masking: {}", e.getMessage());
            }
        }

        // 截断
        if (result.length() > maxLen) {
            result = result.substring(0, maxLen) + "...(truncated)";
        }

        return result;
    }

    /**
     * 对 JSON 字符串进行脱敏处理（使用默认最大长度）
     */
    public String mask(String body, int maxLen) {
        return mask(body, maxLen, null);
    }

    /**
     * 递归遍历 JSON 节点，对匹配字段进行脱敏
     */
    private void maskObjectNode(ObjectNode objectNode, Set<String> fields) {
        objectNode.fieldNames().forEachRemaining(fieldName -> {
            JsonNode fieldValue = objectNode.get(fieldName);
            if (fields.contains(fieldName)) {
                // 脱敏该字段
                ((ObjectNode) objectNode).put(fieldName, MASK_VALUE);
            } else if (fieldValue.isObject()) {
                // 递归处理嵌套对象
                maskObjectNode((ObjectNode) fieldValue, fields);
            } else if (fieldValue.isArray()) {
                // 处理数组中的对象
                fieldValue.forEach(element -> {
                    if (element.isObject()) {
                        maskObjectNode((ObjectNode) element, fields);
                    }
                });
            }
        });
    }

    /**
     * 获取脱敏字段集合
     */
    public Set<String> getMaskFields() {
        return maskFields;
    }
}
