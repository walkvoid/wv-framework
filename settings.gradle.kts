rootProject.name = "wv-framework"

// pluginManagement / dependencyResolutionManagement 在 Kotlin DSL 中单独编译，不能引用本文件顶层的函数或变量，
// 因此下面两处各自内联读取 gradle.properties + gradle-local.properties（逻辑相同）。

pluginManagement {
    repositories {
        mavenLocal()
        maven {
            url = uri(providers.environmentVariable("MAVEN_REPO_PATH"))
        }
        mavenCentral()
        gradlePluginPortal()
    }


}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        maven {
            url = uri(providers.environmentVariable("MAVEN_REPO_PATH"))
        }
        mavenCentral()
    }
}

include(
    ":wvframework-models",
    ":wvframework-annotations",
    ":wvframework-utils",
    ":wvframework-tests",
    ":wvframework-cache",
    ":wvframework-core",
    ":wvframework-excel",
    ":wvframework-json",
    ":wvframework-mock",
    ":wvframework-lombok",
    ":wvframework-dao",
    ":wvframework-validation",
    ":wvframework-apidoc",
    ":wvframework-feign",
    ":wvframework-log",
    ":wvframework-crypto",
    ":wvframework-web-starter",
    ":wvframework-validation-starter",
    ":wvframework-bom",
)

mapOf(
    ":wvframework-models" to "wvframework-models",
    ":wvframework-annotations" to "wvframework-annotations",
    ":wvframework-utils" to "wvframework-utils",
    ":wvframework-tests" to "wvframework-tests",
    ":wvframework-cache" to "wvframework-components/wvframework-cache",
    ":wvframework-core" to "wvframework-components/wvframework-core",
    ":wvframework-excel" to "wvframework-components/wvframework-excel",
    ":wvframework-json" to "wvframework-components/wvframework-json",
    ":wvframework-mock" to "wvframework-components/wvframework-mock",
    ":wvframework-lombok" to "wvframework-components/wvframework-lombok",
    ":wvframework-dao" to "wvframework-components/wvframework-dao",
    ":wvframework-validation" to "wvframework-components/wvframework-validation",
    ":wvframework-apidoc" to "wvframework-components/wvframework-apidoc",
    ":wvframework-feign" to "wvframework-components/wvframework-feign",
    ":wvframework-log" to "wvframework-components/wvframework-log",
    ":wvframework-crypto" to "wvframework-components/wvframework-crypto",
    ":wvframework-web-starter" to "wvframework-starters/wvframework-web-starter",
    ":wvframework-validation-starter" to "wvframework-starters/wvframework-validation-starter",
    ":wvframework-bom" to "wvframework-bom",
).forEach { (name, path) ->
    project(name).projectDir = file(path)
}