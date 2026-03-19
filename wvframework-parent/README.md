# wvframework-parent（已迁移至 Gradle）

原 Maven 父 POM 中的编译、源码包、flatten 等约定已迁移到：

- 根目录 `build.gradle.kts` 的 `subprojects { ... }`（Java 21 工具链、`sourcesJar`、测试 JUnit Platform、`maven-publish` 等）

请勿再使用本目录下的 Maven POM。
