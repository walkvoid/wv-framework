# wvframework-dependencies（已迁移至 Gradle）

原 Maven BOM（`pom.xml`）中的版本与依赖约束已迁移到：

- `gradle/libs.versions.toml`（Version Catalog）
- 根 `build.gradle.kts` 中为各子模块统一引入 `spring-boot-dependencies`、`spring-cloud-dependencies` 的 **platform(BOM)**

请勿再使用本目录下的 Maven POM；构建请使用仓库根目录的 `./gradlew`。
