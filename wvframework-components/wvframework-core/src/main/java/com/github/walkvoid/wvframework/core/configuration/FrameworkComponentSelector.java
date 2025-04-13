package com.github.walkvoid.wvframework.core.configuration;

import com.github.walkvoid.wvframework.core.annotations.EnableFramework;

import java.util.Map;

import com.github.walkvoid.wvframework.models.FrameworkComponents;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author jiangjunqing
 * @date 2025/3/28
 * @description:
 * @version:
 */
public class FrameworkComponentSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(EnableFramework.class.getName());
        String[] includes = (String[])annotationAttributes.get("includes");
        String[] excludes = (String[])annotationAttributes.get("excludes");
        if (includes.length > 1 && excludes.length > 1) {
            throw new IllegalArgumentException("includes and excludes has one valid at same time.");
        }
        if (includes.length >1) {
            return FrameworkComponents.include(includes).values().toArray(new String[0]);
        }
        if (excludes.length >1) {
            return FrameworkComponents.exclude(excludes).values().toArray(new String[0]);
        }
        return FrameworkComponents.all().values().toArray(new String[0]);
    }
}
