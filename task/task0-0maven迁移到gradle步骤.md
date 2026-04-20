# wv-framework：Maven → Gradle 迁移步骤清单

> 基于当前 Maven 结构（根聚合、wvframework-dependencies BOM、wvframework-parent、多子模块）整理的迁移工作项，按执行顺序列出。

**状态（仓库内已落地）**：`wv-framework` 根目录已使用 **Gradle Wrapper 8.10.2**、`settings.gradle.kts` + 各模块 `build.gradle.kts`、`gradle/libs.versions.toml`；原参与构建的 `pom.xml` 已移除。本地仓库通过 `gradle.properties` 的 `wvframework.maven.repo` 指向 `D:\apache-maven-3.5.3\repo`（可改）。`wvframework-maven-plugins` 仍为 Maven，未加入 Gradle 聚合。

---

## 一、前期准备

| 步骤 | 工作内容 |
|------|----------|
| 1.1 | 选定 Gradle 版本（建议 8.x，与 JDK 21 兼容），并确认本机已安装或使用 wrapper。 |
| 1.2 | 梳理当前 Maven 结构：根 `wv-framework`、`wvframework-dependencies`（BOM）、`wvframework-parent`、各子模块及父子关系（含 components/starters 等聚合模块）。 |
| 1.3 | 导出依赖树与插件使用情况：`mvn dependency:tree`、各 pom 中的 `plugin`/`pluginManagement`，便于在 Gradle 中一一对应。 |
| 1.4 | 决定迁移策略：**一次性全量迁移** 或 **先迁部分模块（如仅 components）再逐步扩大**；若保留 Maven，是否短期双构建（Gradle 与 Maven 并存）。 |

---

## 二、根项目与版本管理

| 步骤 | 工作内容 |
|------|----------|
| 2.1 | 在 **wv-framework 根目录** 新增 `settings.gradle.kts`（或 `.groovy`）：在 `rootProject.name` 下用 `include(...)` 声明所有子工程，对应现有 `<modules>`（如 `wvframework-dependencies`、`wvframework-parent`、`wvframework-models`、`wvframework-annotations`、`wvframework-utils`、`wvframework-components` 及其子模块、`wvframework-tests`、`wvframework-starters` 及其子模块等）。 |
| 2.2 | 在根目录新增 **`build.gradle.kts`**（或根 `build.gradle`）：定义 `group`、`version`（替代 Maven 的 `${revision}`），可放 `gradle.properties` 中由子项目读取。 |
| 2.3 | 在根或 **wvframework-dependencies 等价工程** 中建立 **版本目录（Version Catalog）** 或 **共享的 extra properties**：把原 `wvframework-dependencies/pom.xml` 里 `<properties>` 与 `<dependencyManagement>` 中的版本号迁移到 `gradle/libs.versions.toml`（或集中 buildscript/extra），便于全工程统一版本。 |
| 2.4 | 用 **dependency constraint / BOM 引入** 替代 Maven BOM：对 Spring Boot、Spring Cloud 等，使用 `implementation(platform(...))` 或 `api(platform(...))` 引入对应 BOM；内部 BOM 可做成一个 **只做 `dependencyConstraint` 的 Gradle 子项目** 或通过 root build 的 `subprojects { ... }` 统一应用。 |

---

## 三、多模块结构与依赖关系

| 步骤 | 工作内容 |
|------|----------|
| 3.1 | 为每个 **叶子模块**（如 `wvframework-crypto`、`wvframework-validation`）在其目录下添加 `build.gradle.kts`，声明 `plugins`、`dependencies`、`java`（或 `java-library`）等；子模块对父“项目”的依赖改为 `project(":wvframework-xxx")`。 |
| 3.2 | 对 **纯聚合模块**（如 `wvframework-components`、`wvframework-starters`）：在对应目录放 `build.gradle.kts`，只做 `subprojects { ... }` 或空构建，不产出产物，或使用 `gradle.beforeProject` 等统一配置，避免重复。 |
| 3.3 | 将 Maven 的 **父子继承** 转为 Gradle 的 **多项目 + 公共脚本**：把 `wvframework-parent` 中的通用配置（Java 版本、编码、source jar、flatten 等价逻辑等）抽到 **根 `build.gradle.kts` 的 `subprojects { }`** 或 **`buildSrc` / 共享 convention plugin** 中，让所有子项目 apply。 |
| 3.4 | 梳理并实现 **内部模块依赖**：如 `wvframework-crypto` 不依赖其他内部模块则无需 `project(...)`；依赖了 `wvframework-models`、`wvframework-annotations` 等的，改为 `implementation(project(":wvframework-models"))` 等形式。 |

---

## 四、Java 与编译配置

| 步骤 | 工作内容 |
|------|----------|
| 4.1 | 在公共配置中统一 **Java 工具链** 或 **sourceCompatibility/targetCompatibility**（如 21），与当前 `maven.compiler.release` 保持一致；需要单独 21 的模块（如 crypto）在各自 `build.gradle.kts` 中覆盖。 |
| 4.2 | 配置 **编码** 为 UTF-8（如 `tasks.withType<JavaCompile> { options.encoding = "UTF-8" }`）。 |
| 4.3 | 若使用 **注解处理器**（如 Spring Boot configuration-processor），在对应子模块中声明 `annotationProcessor` 依赖并确保 `java` 插件配置正确。 |

---

## 五、依赖声明迁移

| 步骤 | 工作内容 |
|------|----------|
| 5.1 | 将 **wvframework-dependencies** 的 `<dependencyManagement>` 中每一段转为 Gradle：**BOM 用 platform**，**直接依赖** 在子项目中写 `implementation("group:artifact:version")` 或从 version catalog 取版本。 |
| 5.2 | 处理 **scope**：`compile`/`runtime` → `implementation`/`runtimeOnly`；`test` → `testImplementation`/`testRuntimeOnly`；`optional` → `optional` 或分离为独立 configuration（若需发布 optional 依赖）。 |
| 5.3 | 处理 **exclusions**：在 Gradle 中用 `exclude(group, module)` 或 `exclude(mapOf("group" to ..., "module" to ...))` 替代 Maven 的 `<exclusion>`。 |
| 5.4 | 对 **Spring Boot / Spring Cloud**：通过 `id("org.springframework.boot")` 与 `implementation(platform(...))` 引入 BOM，避免手写大量版本。 |

---

## 六、插件与任务等价

| 步骤 | 工作内容 |
|------|----------|
| 6.1 | **flatten-maven-plugin**：Gradle 无直接等价；若仅为 CI 友好版本号，可用 **Gradle 的 `-Pversion=...` + 发布时替换**，或使用 **gradle-nexus-publish-plugin** 等与 CI 配合；不再生成 `.flattened-pom.xml` 时，可删除此前“编译成功后删除该文件”的 antrun 逻辑。 |
| 6.2 | **maven-source-plugin**：用 **`java` 插件的 `withSourcesJar()`** 或自定义 `sourcesJar` task 生成源码 jar。 |
| 6.3 | **maven-compiler-plugin**：由 **Java 插件 + toolchain 或 sourceCompatibility** 覆盖。 |
| 6.4 | **maven-surefire-plugin**：由 **Gradle 默认的 test 任务** 替代；若有特殊 need 的配置（如 argLine、includes），在 `tasks.test` 或 `tasks.named<Test>("test")` 中配置。 |
| 6.5 | 若有 **自定义 Maven 插件**（如 wvframework-install-maven-plugin）：需改为 **Gradle Task** 或 **Gradle 插件**，在对应模块或 buildSrc 中实现。 |

---

## 七、发布与仓库

| 步骤 | 工作内容 |
|------|----------|
| 7.1 | 配置 **发布仓库**（如 Nexus）：在根或子项目中用 `publishing { repositories { maven { url = ... } } }` 及认证（凭证、环境变量等）。 |
| 7.2 | 配置 **publishing**：对需要发布的子项目应用 `maven-publish` 插件，定义 `publications`（如 `from(components["java"])`），**groupId/artifactId/version** 与当前 Maven 保持一致，便于下游兼容。 |
| 7.3 | 若存在 **install** 到本地：使用 `publishToMavenLocal` 替代 `mvn install`。 |

---

## 八、CI / 脚本与文档

| 步骤 | 工作内容 |
|------|----------|
| 8.1 | 将 CI 中 **Maven 命令**（如 `mvn clean compile test`、`mvn deploy`）改为 **Gradle**：`./gradlew clean build`、`./gradlew publish` 等；必要时保留 `JAVA_HOME` 或使用 Gradle 的 JDK 配置。 |
| 8.2 | 更新 **README / 贡献指南** 中的构建说明：如何用 Gradle 编译、测试、发布；注明 JDK 21 等要求。 |
| 8.3 | 若有 **compile-with-jdk21.cmd** 等脚本：改为调用 `gradlew` 并设置 `JAVA_HOME`。 |

---

## 九、验证与收尾

| 步骤 | 工作内容 |
|------|----------|
| 9.1 | 在 **干净目录** 执行 `./gradlew clean build`（或 `build` 含测试），确认所有需参与构建的子项目编译、测试通过。 |
| 9.2 | 对比 **依赖解析结果**：`./gradlew dependencies` 与 Maven `dependency:tree` 对比，确保无遗漏或错误升级。 |
| 9.3 | 按需保留或删除 Maven 配置：若不再使用 Maven，可删除根及子模块的 **pom.xml**、**.mvn**、以及“编译成功后删除 .flattened-pom.xml”的 antrun；若短期双构建，可保留 pom 并维护两套配置。 |

---

## 十、可选优化

| 步骤 | 工作内容 |
|------|----------|
| 10.1 | 使用 **Gradle Version Catalog**（`libs.versions.toml`）集中管理依赖与插件版本，便于复用和升级。 |
| 10.2 | 将公共逻辑（Java 版本、编码、source jar、发布配置）抽成 **Convention Plugin**（在 `buildSrc` 或独立 `buildLogic` 工程），减少各子项目重复。 |
| 10.3 | 配置 **Gradle Build Cache** 与 **配置缓存**（`--configuration-cache`）以提升后续构建速度。 |

---

## 模块与层次速览（当前 Maven）

- **根**：`wv-framework`（聚合）
- **BOM/父级**：`wvframework-dependencies` → `wvframework-parent`
- **叶子/库**：`wvframework-models`、`wvframework-annotations`、`wvframework-utils`、`wvframework-tests` 等
- **聚合 + 子模块**：`wvframework-components`（含 cache、core、excel、json、mock、lombok、dao、validation、apidoc、feign、log、crypto）；`wvframework-starters`（含 web-starter、validation-starter）

迁移时建议先做 **settings.gradle + 根 build + 一个简单子模块（如 wvframework-annotations）**，再按依赖关系逐个加入并统一版本与发布配置。
