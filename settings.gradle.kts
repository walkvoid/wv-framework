rootProject.name = "wv-framework"

// pluginManagement / dependencyResolutionManagement 在 Kotlin DSL 中单独编译，不能引用本文件顶层的函数或变量，
// 因此下面两处各自内联读取 gradle.properties + gradle-local.properties（逻辑相同）。

pluginManagement {
    val merged = java.util.Properties()
    settings.rootDir.resolve("gradle.properties").takeIf { f -> f.exists() }?.reader()?.use { r -> merged.load(r) }
    settings.rootDir.resolve("gradle-local.properties").takeIf { f -> f.exists() }?.reader()?.use { local ->
        val overlay = java.util.Properties()
        overlay.load(local)
        merged.putAll(overlay)
    }
    val multi = merged.getProperty("wvframework.maven.repos", "")
        .split(",")
        .map { s -> s.trim() }
        .filter { s -> s.isNotEmpty() }
    val localMavenUrls: List<String> =
        if (multi.isNotEmpty()) {
            multi
        } else {
            val single = merged.getProperty("wvframework.maven.repo", "file:///D:/apache-maven-3.5.3/repo").trim()
            if (single.isEmpty()) emptyList() else listOf(single)
        }
    repositories {
        localMavenUrls.forEachIndexed { index: Int, repoUrl: String ->
            maven {
                name = "localMavenRepo-$index"
                url = uri(repoUrl)
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    val merged = java.util.Properties()
    settings.rootDir.resolve("gradle.properties").takeIf { f -> f.exists() }?.reader()?.use { r -> merged.load(r) }
    settings.rootDir.resolve("gradle-local.properties").takeIf { f -> f.exists() }?.reader()?.use { local ->
        val overlay = java.util.Properties()
        overlay.load(local)
        merged.putAll(overlay)
    }
    val multi = merged.getProperty("wvframework.maven.repos", "")
        .split(",")
        .map { s -> s.trim() }
        .filter { s -> s.isNotEmpty() }
    val localMavenUrls: List<String> =
        if (multi.isNotEmpty()) {
            multi
        } else {
            val single = merged.getProperty("wvframework.maven.repo", "file:///D:/apache-maven-3.5.3/repo").trim()
            if (single.isEmpty()) emptyList() else listOf(single)
        }
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        localMavenUrls.forEachIndexed { index: Int, repoUrl: String ->
            maven {
                name = "localMavenRepo-$index"
                url = uri(repoUrl)
            }
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
).forEach { (name, path) ->
    project(name).projectDir = file(path)
}
