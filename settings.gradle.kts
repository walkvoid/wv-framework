import java.io.File
import java.util.Properties

rootProject.name = "wv-framework"

/** 合并 gradle.properties 与 gradle-local.properties（后者覆盖前者，适合家里/公司不同配置） */
fun loadMergedGradleProperties(rootDir: File): Properties {
    val merged = Properties()
    rootDir.resolve("gradle.properties").takeIf { it.exists() }?.reader()?.use { merged.load(it) }
    rootDir.resolve("gradle-local.properties").takeIf { it.exists() }?.reader()?.use { local ->
        val overlay = Properties()
        overlay.load(local)
        merged.putAll(overlay)
    }
    return merged
}

/**
 * 本地 Maven 仓库 URL 列表（按顺序查找，与 Maven 多 mirror 不同，这里是多个独立 repo）。
 * - wvframework.maven.repos：逗号分隔多个 file:/// 或 https:// 地址（推荐）
 * - 若未配置 repos，则回退 wvframework.maven.repo 单个地址
 */
fun localMavenRepoUrls(props: Properties): List<String> {
    val multi = props.getProperty("wvframework.maven.repos", "")
        .split(",")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
    if (multi.isNotEmpty()) return multi
    val single = props.getProperty("wvframework.maven.repo", "file:///D:/apache-maven-3.5.3/repo").trim()
    return if (single.isEmpty()) emptyList() else listOf(single)
}

val mergedProps = loadMergedGradleProperties(settings.rootDir)
val localMavenUrls = localMavenRepoUrls(mergedProps)

pluginManagement {
    repositories {
        localMavenUrls.forEachIndexed { index, url ->
            maven {
                name = "localMavenRepo-$index"
                url = uri(url)
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        localMavenUrls.forEachIndexed { index, url ->
            maven {
                name = "localMavenRepo-$index"
                url = uri(url)
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
