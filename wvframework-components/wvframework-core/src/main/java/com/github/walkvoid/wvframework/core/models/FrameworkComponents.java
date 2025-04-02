package com.github.walkvoid.wvframework.core.models;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author jiangjunqing
 * @date 2025/3/28
 * @description:
 * @version:
 */
public enum FrameworkComponents {

    CORE("core","com.github.walkvoid.wvframework.core.configuration.CoreAutoConfiguration"),
    ;

    FrameworkComponents(String name, String pkg) {
        this.name = name;
        this.pkg = pkg;
    }

    private String name;

    private String pkg;

    public String getName() {
        return name;
    }

    public String getPkg() {
        return pkg;
    }

    /**
     * all components
     * @return
     */
    public static Map<String, String> all(){
        return Arrays.stream(FrameworkComponents.values())
                .collect(Collectors.toMap(FrameworkComponents::getName, FrameworkComponents::getPkg));
    }

    /**
     * include some components
     * @param includes
     * @return
     */
    public static Map<String, String> include(String[] includes){
        List<String> includeList = Arrays.stream(includes).map(String::toLowerCase).collect(Collectors.toList());
        return Arrays.stream(FrameworkComponents.values())
                .filter(x-> includeList.contains(x.getName()))
                .collect(Collectors.toMap(FrameworkComponents::getName, FrameworkComponents::getPkg));
    }

    /**
     * exclude components of all
     * @param excludes
     * @return
     */
    public static Map<String, String> exclude(String[] excludes){
        List<String> excludeList = Arrays.stream(excludes).map(String::toLowerCase).collect(Collectors.toList());
        return Arrays.stream(FrameworkComponents.values())
                .filter(x -> !excludeList.contains(x.getName()))
                .collect(Collectors.toMap(FrameworkComponents::getName, FrameworkComponents::getPkg));
    }

}
