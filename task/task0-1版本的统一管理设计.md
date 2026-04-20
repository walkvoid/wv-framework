# 版本的统一管理设计

## 1. 背景

在多模块项目中，依赖版本管理是一个重要的问题。如果每个模块都单独管理依赖版本，容易导致版本不一致，从而引发各种问题，如：

- 依赖冲突
- 兼容性问题
- 维护困难
- 升级复杂

为了解决这些问题，我们需要一种统一的依赖版本管理方案，确保所有模块使用相同的依赖版本。

## 2. 设计方案

我们采用 BOM (Bill of Materials) 模式来实现依赖版本的统一管理。BOM 是一种特殊的 POM 文件，用于集中管理依赖版本，其他项目可以通过引入 BOM 来统一使用这些版本。

### 2.1 实现步骤

1. **创建 wvframework-bom 模块**：在 wv-framework 项目中创建一个专门的 BOM 模块，用于集中管理所有第三方依赖的版本。

2. **配置 BOM 模块**：在 BOM 模块中定义所有第三方依赖的版本约束，包括 Spring Boot、Spring Cloud、Jackson、MyBatis 等常用依赖。

3. **修改 wv-framework 项目**：让 wv-framework 内部的所有模块使用 BOM 管理依赖版本。

4. **配置 zone 项目**：让 zone 项目引用 wv-framework 项目，并使用 BOM 管理依赖版本。

## 3. 具体实现

### 3.1 创建 wvframework-bom 模块

在 `settings.gradle.kts` 中添加 wvframework-bom 模块：

```kotlin
include(
    ":wvframework-annotations",
    ":wvframework-components",
    ":wvframework-models",
    ":wvframework-utils",
    ":wvframework-tests",
    ":wvframework-starters",
    ":wvframework-bom"
)
```

### 3.2 配置 wvframework-bom 模块

在 `wvframework-bom/build.gradle.kts` 中配置 BOM 模块：

```kotlin
plugins {
    id("java-platform")
    id("maven-publish")
}

group = rootProject.group
version = rootProject.version

javaPlatform {
    allowDependencies()
}

publishing {
    publications {
        create<MavenPublication>("mavenBom") {
            from(components["javaPlatform"])
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
        }
    }
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    constraints {
        // Spring Boot BOM
        api(platform(libs.spring.boot.bom))
        
        // Spring Cloud BOM
        api(platform(libs.spring.cloud.bom))
        
        // 第三方依赖版本约束
        api(libs.jackson.annotations)
        api(libs.jackson.databind)
        api(libs.jackson.datatype.jsr310)
        
        api(libs.spring.boot.starter)
        api(libs.spring.boot.starter.web)
        api(libs.spring.boot.starter.validation)
        api(libs.spring.boot.starter.test)
        api(libs.spring.boot.starter.data.redis)
        api(libs.spring.boot.starter.data.jdbc)
        api(libs.spring.boot.configuration.processor)
        api(libs.spring.boot.autoconfigure)
        
        api(libs.spring.context)
        api(libs.spring.web)
        
        api(libs.slf4j.api)
        
        api(libs.springdoc.openapi.webmvc)
        api(libs.knife4j.openapi3)
        
        api(libs.dubbo.spring.boot.starter)
        api(libs.mybatis)
        api(libs.mybatis.plus.boot3)
        
        api(libs.feign.okhttp)
        
        api(libs.bcprov)
        api(libs.bcpkix)
        api(libs.junit.jupiter)
    }
}
```

### 3.3 修改 wv-framework 项目

在 `build.gradle.kts` 中修改子项目的依赖管理：

```kotlin
afterEvaluate {
    dependencies {
        // 使用 wvframework-bom 管理所有依赖版本
        add("implementation", platform(project(":wvframework-bom")))
    }
}
```

### 3.4 配置 zone 项目

在 `zone/settings.gradle.kts` 中添加对 wv-framework 项目的引用：

```kotlin
// 包含 wv-framework 项目
includeBuild("../wv-framework")
```

在 `zone/build.gradle.kts` 中修改依赖管理：

```kotlin
afterEvaluate {
    if (!isGateway) {
        dependencies {
            // 使用 wvframework-bom 管理所有依赖版本
            add("implementation", platform("com.github.walkvoid:wvframework-bom"))
            // Lombok 等由 BOM 管版本；annotationProcessor 需单独挂 platform，否则会出现 lombok:. 
            add("compileOnly", platform("com.github.walkvoid:wvframework-bom"))
            add("annotationProcessor", platform("com.github.walkvoid:wvframework-bom"))
        }
    }
}
```

## 4. 使用方式

在 zone 项目中，当需要引入第三方依赖时，不需要指定版本号，直接使用即可：

```kotlin
dependencies {
    // 不需要指定版本，由 wvframework-bom 管理
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("org.mybatis:mybatis")
    // ... 其他依赖
}
```

## 5. 优势

1. **版本统一**：所有项目使用相同的依赖版本，避免版本冲突
2. **管理集中**：依赖版本集中在 wvframework-bom 中管理，便于统一升级
3. **使用简单**：在 zone 项目中不需要关心依赖版本，直接引用即可
4. **维护方便**：当需要升级依赖版本时，只需要修改 wvframework-bom 中的版本号

## 6. 后续维护

1. **版本升级**：当需要升级依赖版本时，只需要修改 `wvframework-bom/build.gradle.kts` 中的版本号
2. **添加新依赖**：当需要添加新的依赖时，在 `wvframework-bom/build.gradle.kts` 中添加对应的依赖配置
3. **构建验证**：每次修改依赖版本后，需要执行构建命令验证依赖是否正确解析

## 7. 构建命令

### 7.1 构建 wvframework-bom 模块

```bash
./gradlew :wvframework-bom:build
```

### 7.2 验证 zone 项目依赖

```bash
./gradlew build
```

## 8. 总结

通过 BOM 模式，我们实现了依赖版本的统一管理，解决了多模块项目中依赖版本不一致的问题。这种方案不仅提高了项目的可维护性，也减少了依赖冲突的风险，为项目的长期发展奠定了良好的基础。