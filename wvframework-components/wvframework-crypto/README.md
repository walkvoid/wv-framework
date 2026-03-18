# wvframework-crypto

与设计文档 `docs/crypto-加密模块-开发设计文档.md` 对齐。**编译目标 JDK 21**；运行需 **JDK 17+**（Spring Boot 3）。

### JDK 21 编译（推荐）

本模块 **编译目标为 `--release 21`**，请使用 **JDK 21**。

1. **一键脚本**（默认 `JAVA_HOME=C:\Program Files\Java\jdk-21.0.10`，可按需改脚本）：  
   在 `wvframework-crypto` 目录双击或执行：  
   `compile-with-jdk21.cmd`

2. **命令行**（在 **`wv-framework` 根目录**，即包含 `wvframework-dependencies` 的目录）：

```bat
set JAVA_HOME=C:\Program Files\Java\jdk-21.0.10
set PATH=%JAVA_HOME%\bin;%PATH%
mvn -pl wvframework-components/wvframework-crypto -am clean compile test
```

> 请勿仅在 `wvframework-crypto` 子目录单独执行 `mvn`（父 POM `${revision}` / flatten 会导致解析失败）；务必带 `-pl ... -am` 从根目录构建，或使用上述脚本。

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
