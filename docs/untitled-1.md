# Gradle 的基本知识和最佳实践

## 1. 前言

对于从 Maven 切换到 Gradle 的开发者来说，理解 Gradle 的核心概念和配置方式是顺利过渡的关键。本文将介绍 Gradle 的基本知识和最佳实践，帮助你快速适应 Gradle 构建系统。

## 2. Gradle 核心概念

### 2.1 构建脚本

- **settings.gradle.kts**：项目级配置文件，定义项目结构和仓库配置
- **build.gradle.kts**：模块级配置文件，定义依赖和构建任务

### 2.2 仓库配置

Gradle 支持多种仓库类型，主要包括：
- **mavenCentral()**：Maven 中央仓库
- **gradlePluginPortal()**：Gradle 插件门户
- **mavenLocal()**：本地 Maven 仓库
- **自定义 Maven 仓库**：通过 URL 配置

## 3. 仓库配置最佳实践

### 3.1 仓库顺序配置

**推荐顺序**：
1. **mavenCentral()**：中央仓库，包含大量依赖和插件
2. **gradlePluginPortal()**：官方插件门户，确保获取所有 Gradle 插件
3. **自定义 Maven 仓库**：如公司内部仓库
4. **mavenLocal()**：本地 Maven 缓存

**配置示例**：
```kotlin
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("file:///D:/apache-maven-3.5.3/repo")
        }
        mavenLocal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        mavenCentral()
        maven {
            url = uri("file:///D:/apache-maven-3.5.3/repo")
        }
        mavenLocal()
    }
}
```

### 3.2 环境变量配置

使用环境变量配置 Maven 仓库路径，提高配置灵活性：

**方法 1：使用系统环境变量**
```kotlin
maven {
    url = uri(providers.environmentVariable("MAVEN_REPO_PATH").getOrElse("file:///D:/apache-maven-3.5.3/repo"))
}
```

**方法 2：使用系统属性**
```properties
# gradle.properties
systemProp.maven.repo.local=D:/custom/maven/repo
```

## 4. 从 Maven 到 Gradle 的迁移

### 4.1 核心差异

| 特性 | Maven | Gradle |
|------|-------|--------|
| 构建脚本 | XML 格式 | Kotlin/Groovy DSL |
| 依赖管理 | 集中式 | 灵活的依赖解析 |
| 插件系统 | 基于生命周期 | 基于任务 |
| 性能 | 相对较慢 | 增量构建，更快 |

### 4.2 迁移步骤

1. **分析现有 Maven 项目**：了解依赖、插件和构建流程
2. **创建 Gradle 配置文件**：
   - `settings.gradle.kts`：配置项目结构和仓库
   - `build.gradle.kts`：配置依赖和构建任务
3. **转换依赖声明**：将 Maven 依赖转换为 Gradle 格式
4. **迁移构建逻辑**：将 Maven 插件和生命周期转换为 Gradle 任务
5. **测试构建**：确保迁移后的构建正常工作

### 4.3 常用命令对比

| Maven 命令 | Gradle 命令 | 说明 |
|------------|-------------|------|
| `mvn clean` | `gradle clean` | 清理构建产物 |
| `mvn compile` | `gradle compileJava` | 编译源代码 |
| `mvn test` | `gradle test` | 运行测试 |
| `mvn package` | `gradle assemble` | 打包项目 |
| `mvn install` | `gradle publishToMavenLocal` | 发布到本地仓库 |
| `mvn deploy` | `gradle publish` | 发布到远程仓库 |

## 5. 高级配置技巧

### 5.1 本地 Maven 仓库配置

**自定义本地仓库路径**：
- 通过系统属性：`-Dmaven.repo.local=D:/custom/repo`
- 通过 IDE 设置：在 IDE 中配置 Maven 本地仓库路径
- 通过环境变量：设置 `MAVEN_OPTS="-Dmaven.repo.local=/path/to/repo"`

### 5.2 依赖解析策略

- **优先使用本地仓库**：减少网络请求
- **合理配置仓库顺序**：提高构建速度和可靠性
- **使用镜像仓库**：对于国内用户，配置 Maven 中央仓库镜像

### 5.3 构建优化

- **增量构建**：Gradle 自动支持增量构建，只重新构建修改的部分
- **并行构建**：通过 `--parallel` 选项启用并行构建
- **构建缓存**：使用构建缓存加速构建过程

## 6. 常见问题及解决方案

### 6.1 依赖冲突

- **问题**：不同依赖版本冲突
- **解决方案**：使用 `resolutionStrategy` 强制指定版本

### 6.2 构建失败

- **问题**：依赖下载失败
- **解决方案**：检查网络连接，配置镜像仓库，清理本地缓存

### 6.3 性能问题

- **问题**：构建速度慢
- **解决方案**：启用增量构建，配置合适的仓库顺序，使用构建缓存

## 7. 总结

从 Maven 切换到 Gradle 可能需要一些适应时间，但 Gradle 提供的灵活性、性能和现代构建功能值得投入。通过本文介绍的基本知识和最佳实践，你可以更顺利地完成迁移过程，并充分发挥 Gradle 的优势。

### 关键要点

- **合理配置仓库顺序**：提高构建速度和可靠性
- **使用环境变量**：增强配置灵活性
- **理解依赖解析机制**：避免依赖冲突
- **利用 Gradle 特性**：如增量构建、任务依赖等
- **保持与 Maven 生态的兼容性**：通过 `mavenLocal()` 等配置

希望本文能帮助你快速掌握 Gradle 的核心概念和最佳实践，顺利完成从 Maven 到 Gradle 的过渡。