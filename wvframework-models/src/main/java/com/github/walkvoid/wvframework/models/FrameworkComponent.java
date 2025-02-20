package com.github.walkvoid.wvframework.models;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author jiangjunqing
 * @date 2025/2/20
 * @description:
 * @version:
 */
public enum FrameworkComponent {
   CORE,
   CACHE,
   VALIDATION,
   MOCK,
   JSON,
    ;


   public static Optional<FrameworkComponent> from(String name){
       return Arrays.stream(FrameworkComponent.values()).filter(x -> x.name().equalsIgnoreCase(name)).findFirst();
   }
}
