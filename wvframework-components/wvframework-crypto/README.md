# wvframework-crypto

与设计文档 `docs/crypto-加密模块-开发设计文档.md` 对齐。**编译目标 JDK 21**；运行需 **JDK 17+**（Spring Boot 3）。

### JDK 21 编译（推荐）

本工程已使用 **Gradle Wrapper**，工具链在根 `build.gradle.kts` 中指定为 **Java 21**。

在 **`wv-framework` 仓库根目录**执行：

```bat
set JAVA_HOME=C:\Program Files\Java\jdk-21.0.10
set PATH=%JAVA_HOME%\bin;%PATH%
gradlew.bat :wvframework-crypto:build
```

或根目录 `gradlew-build.cmd`（可在脚本内取消注释 `JAVA_HOME`）。

> 依赖仓库：默认优先使用 `gradle.properties` 中的 `wvframework.maven.repo`（本地 Maven 目录），其次 `mavenCentral()`。

## 包结构

| 包 | 说明 |
|----|------|
| `autoconfigure` | `CryptoAutoConfiguration`、`CryptoProperties`、`CryptoInstanceFactory` |
| `api` / `model` / `loader` / `algorithm` / `internal` | 核心加解密能力 |

Spring Boot 应用引入本依赖后，通过 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 加载自动配置。

## 配置示例

```yaml
crypto:
  instances:
    icbc:
      algo:
        type: SM2
      configurations:
        signature-algorithm: SM3withSM2
      loaders:
        public-key-pem: |
          -----BEGIN PUBLIC KEY-----
          ...
        private-key-pem: |
          -----BEGIN PRIVATE KEY-----
          ...
  default-instance: icbc   # 可选 @Primary
```

对称密钥：`loaders.symmetric-key-ref` 支持 `base64:...` 或 `hex:...`。

## 依赖说明

- `spring-boot-autoconfigure` 为 **optional**：仅在使用 Spring Boot 时需要；非 Spring 项目若只引用本 JAR 且未引入 Spring，请勿加载 `autoconfigure` 相关类。
