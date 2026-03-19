# wv-framework 行空框架
让java程序员写更少的代码；Let Java programmers write less code;

### 项目结构
```text
|--wvframework-annotations: 注解的定义，不涉及到具体的逻辑
|--wvframework-components:
    |--wvframework-cache: 缓存组件
    |--wvframework-core: 核心组件
    |--wvframework-excel: excel操作组件
    |--wvframework-json: json组件，涉及jackson的注解也会放这里
    |--wvframework-lombok: lombok扩展组件
    |--wvframework-mock: mock数据组件
    |--wvframework-dao: dao(数据访问对象)层的一些组件，例如mybatisplus的一些扩张
    |--wvframework-validation: 参数校验的一些组件
|--wvframework-models: 模型的定义，没有具体的业务逻辑
|--wvframework-starters:
    |--wvframework-web-starter: web starter
|--wvframework-utils: 工具类
|--build.gradle.kts / settings.gradle.kts: Gradle 多模块构建
|--gradle/libs.versions.toml: 第三方依赖版本（原 BOM）
```

### 构建（Gradle Wrapper）

- **JDK**：建议 **21**（与工具链一致）。
- **本地 Maven 缓存**：
  - 单个路径：`gradle.properties` 里的 `wvframework.maven.repo`；
  - **多个路径**：`wvframework.maven.repos=url1,url2`（逗号分隔，按顺序查找）；
  - **家里/公司不同**：复制 `gradle-local.properties.example` 为 **`gradle-local.properties`**（已 `.gitignore`），只在本机写 `wvframework.maven.repo` 或 `wvframework.maven.repos`，会覆盖 `gradle.properties`。
  - 之后为 **Maven Central**。
- **常用命令**（在 `wv-framework` 根目录）：

```bat
gradlew.bat build
gradlew.bat :wvframework-crypto:test
```

- **发布到本地 Maven**：`gradlew.bat publishToMavenLocal`

> `wvframework-maven-plugins` 仍为 Maven 工程，未纳入 Gradle 多模块；其余库模块已迁移为 Gradle。

### 项目特点
- 干净的依赖
```text
和其他项目不同，wv-framework针对第三方的类库都使用<scope>provided</scope>,这代表wv-framework不会替你引入任何第三方类库，所有的
的三方类库都需要你的手动引入。经过长期的项目实践这是十分有益的，意味着你可以完全掌控你的项目依赖树。
```