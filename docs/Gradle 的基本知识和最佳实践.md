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

#### mavenLocal() 方法
`mavenLocal()` 方法用于在 Gradle 项目中添加一个本地 Maven 缓存仓库，用于查找项目依赖。它使用 `org.gradle.api.artifacts.ArtifactRepositoryContainer#DEFAULT_MAVEN_LOCAL_REPO_NAME` 作为仓库名称，并返回添加的 `MavenArtifactRepository` 实例。

**使用示例**：
```groovy
repositories {
    mavenLocal()
}
```

#### 本地 Maven 仓库位置确定规则
Gradle 会按照以下优先级顺序确定本地 Maven 仓库的位置：
1. 系统属性 `maven.repo.local` 的值（如果已设置）
2. `~/.m2/settings.xml` 文件中的 `<localRepository>` 元素（如果文件存在且已设置）
3. `$M2_HOME/conf/settings.xml` 文件中的 `<localRepository>` 元素（如果文件存在且已设置，其中 `$M2_HOME` 是环境变量）
4. 默认路径 `~/.m2/repository`

#### 设置 maven.repo.local 系统属性的方法

**1. 在 Gradle 命令行中设置**
```bash
# Windows
gradle build -Dmaven.repo.local=C:\path\to\local\repo

# Linux/macOS
gradle build -Dmaven.repo.local=/path/to/local/repo
```

**2. 在 Gradle 构建脚本中设置**
```groovy
allprojects {
    repositories {
        // 先设置系统属性
        System.setProperty('maven.repo.local', '/path/to/local/repo')
        mavenLocal()
        // 其他仓库...
    }
}
```

**3. 在 gradle.properties 文件中设置**
```properties
# gradle.properties
systemProp.maven.repo.local=/path/to/local/repo
```

**4. 在 IDE 中设置**
- **IntelliJ IDEA**：打开 Run/Debug Configurations，选择或创建一个 Gradle 运行配置，在 "VM options" 字段中添加 `-Dmaven.repo.local=/path/to/local/repo`
- **Eclipse**：打开 Run Configurations，选择或创建一个 Gradle 运行配置，在 "Arguments" 标签页的 "VM arguments" 字段中添加 `-Dmaven.repo.local=/path/to/local/repo`

**5. 设置为系统环境变量**
- **Windows**：在系统变量中创建 `MAVEN_OPTS` 变量，值为 `-Dmaven.repo.local=C:\path\to\local\repo`
- **Linux/macOS**：在 `~/.bashrc`、`~/.bash_profile` 或 `~/.zshrc` 中添加 `export MAVEN_OPTS="-Dmaven.repo.local=/path/to/local/repo"`

#### MAVEN_OPTS 环境变量
`MAVEN_OPTS` 是 Maven 构建工具的标准环境变量名称，用于向 Maven 启动的 JVM 传递参数。虽然它是 Maven 的环境变量，但 Gradle 在某些情况下也会考虑它，特别是当设置 `maven.repo.local` 时。

**MAVEN_OPTS 的用途**：
1. **设置 JVM 内存参数**：`export MAVEN_OPTS="-Xms512m -Xmx1024m"`
2. **设置系统属性**：`export MAVEN_OPTS="-Dmaven.repo.local=/path/to/local/repo"`
3. **其他 JVM 参数**：如启用远程调试等

**注意事项**：
- 路径必须是绝对路径，不能使用相对路径
- 确保指定的目录存在，否则 Maven/Gradle 可能会使用默认路径
- 不同操作系统的路径分隔符不同（Windows 使用 `\`，Linux/macOS 使用 `/`）
- 如果同时在多个地方设置了该属性，优先级顺序为：命令行参数 > 构建脚本 > 系统环境变量

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