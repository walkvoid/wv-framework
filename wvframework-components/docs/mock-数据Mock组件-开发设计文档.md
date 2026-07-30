# wvframework-mock 数据 Mock 组件 - 开发设计文档
> **创建时间**: 2026-07-25  
> **技术栈**: Java 17+、Spring Boot 3.x、Spring AOP、Jackson
---
## 1 模块概述
### 1.1 模块名称
**wvframework-mock**（已有子模块，与 `wvframework-httplog`、`wvframework-crypto` 等并列）
### 1.2 模块定位
提供基于注解的**接口级数据 Mock** 能力，支持以下四种拦截场景：
1. **Controller 方法** — 外部请求该方法时，直接返回 Mock 数据，不执行业务逻辑
2. **Feign Client Facade 方法** — 远程调用时，不调用远端服务，直接返回 Mock 数据
3. **@HttpExchange 接口方法** — 声明式 HTTP 调用时，直接返回 Mock 数据
4. **Dubbo Provider 接口方法** — 服务提供方拦截，直接返回 Mock 数据
通过注解 **`@Mock`** 支持**类级**和**方法级**两种用法：
- **类级**：标注在 Controller 类上，表示该类所有请求方法都需要 Mock，作为运行时开关
- **方法级**：标注在具体请求方法上，更细粒度地控制该方法是否 Mock、Mock 请求体还是响应体等
同时通过**字段级语义 Mock 注解**（如 `@MockName`、`@MockIdCardNo`、`@MockAddress` 等）实现基于响应体字段类型的精细化 Mock 数据生成。
**核心设计理念**：
- **`@Mock` 运行时开关**：可标注在 `@Controller` 类或请求方法上。**类级标注**：表示该 Controller 所有请求方法都启用 Mock，作为运行时总开关；**方法级标注**：在类级基础上实现更细粒度的控制，可决定该方法是否 Mock、Mock 请求体还是响应体等。默认请求体和响应体都 Mock
- **字段级 `@MockXxx`**：标注在返回实体的属性上，根据字段语义（姓名、身份证、地址等）生成符合业务含义的 Mock 数据
- **默认 Mock 策略**：请求体和响应体字段都有默认 Mock 值。如果 String 类型字段没有任何 `@MockXxx` 注解，直接默认返回一个随机字符串；数值类型返回随机数，日期类型返回随机日期等。如果字段级注解未指定 `lang`，默认使用 `AUTO` 模式，自动获取当前环境的多语言配置
- **多语言支持**：字段级注解通过 `lang` 属性控制生成的数据风格，支持 `zh-CN`（中文）、`en-US`（英文）、`ja-JP`（日文）、`ko-KR`（韩文）等，还可以设置为 `AUTO` 自动复用当前环境的多语言配置进行动态切换，兼容国际化通用处理
- **数据库 Mock 数据源**：支持从数据库读取预配置的 Mock 数据，通过 `@Mock(value = "dbKey")` 指定数据键。例如银行回调 Controller 接口，可配置一条默认银行回调成功的 Mock 数据，请求到达时直接从数据库取出返回
### 1.3 关联模块
| 关联方 | 说明 |
|--------|------|
| wvframework-core | 复用基础工具能力 |
| wvframework-annotations | 注解设计参考（`@EnumField`、`@Pojo` 等） |
| wvframework-httplog | 复用原生 Advisor 拦截机制（可选） |
| 业务应用 | 在 Controller 类或方法 / Feign / @HttpExchange / Dubbo Provider 方法上添加 `@Mock` 即可启用 |
---
## 2 功能要求
### 2.1 功能要求
| 编号 | 要求 | 说明 |
|------|------|------|
| F1 | `@Mock` 运行时开关注解（类 + 方法） | 可标注在类和方法上。类级：该类所有请求方法均启用 Mock；方法级：细粒度控制该方法的 Mock 行为（mocko-KRequest/mocko-KResponse/dbKey 等） |
| F2 | 字段级 Mock 注解体系 | 提供 `@MockName`、`@MockIdCardNo`、`@MockAddress`、`@MockPhone`、`@MockEmail` 等字段注解，标注在 DTO 属性上，根据字段语义生成 Mock 数据 |
| F3 | 多语言（lang）支持 | 字段级注解提供 `lang` 属性，支持 `zh-CN`（中国中文）、`en-US`（美国英文）、`ja-JP`（日本日文）、`ko-KR`（韩国韩文）等，还支持 `AUTO` 模式自动复用当前环境的多语言配置进行动态切换，不同语言生成不同风格的 Mock 数据，兼容国际化通用处理 |
| F4 | 响应体自动解析与填充 | 根据方法返回类型，自动解析实体字段上的 Mock 注解，生成完整的 Mock 对象 |
| F4-1 | 多语言配置文件支持 | 支持通过 `@MockString` 注解从多语言配置文件中随机获取值，数据来源可以是配置文件（KEY-VALUE 格式，VALUE 为逗号分隔的列表），也可以是 26 个英文字母 |
| F4-2 | 复合注解支持 | `@MockName`、`@MockAddress` 等是复合注解，底层基于 `@MockString` 实现，支持从多个配置文件中组合数据 |
| F4-3 | AUTO 自动切换 | 支持 `lang = AUTO` 模式，根据当前 Spring 环境的 locale 配置自动切换多语言，支持从 HTTP 请求头 `Accept-Language` 动态获取 |
| F5 | 集合类型支持 | 返回类型为 `List<T>`、`Page<T>` 等集合类型时，支持配置 Mock 数据条数 |
| F6 | 全局开关 | 支持全局配置开关（`wv.mock.enabled`），注解可覆盖全局配置 |
| F7 | Mock 数据生成策略可扩展 | 通过 SPI 接口 `MockDataGenerator` 支持自定义 Mock 数据生成策略 |
| F8 | Dubbo Provider 支持 | 通过 Dubbo Filter 机制拦截 Dubbo Provider 接口方法上的 `@Mock` 注解 |
| F9 | 默认 Mock 策略 | 字段若没有任何 `@MockXxx` 注解，String 类型默认返回随机字符串，数值类型返回随机数，日期类型返回随机日期等 |
| F10 | 数据库 Mock 数据源 | 支持从数据库读取预配置的 Mock 数据，通过 `@Mock(value = "dbKey")` 指定数据键，用于银行回调等需要固定 Mock 数据的场景 |
| F11 | 请求体 / 响应体分别控制 | 方法级 `@Mock` 可通过 `mocko-KRequest` 和 `mocko-KResponse` 属性分别控制是否 Mock 请求体和响应体，默认两者都 Mock |
### 2.2 非功能要求
| 项 | 要求 |
|----|------|
| 性能 | Mock 数据生成不应成为性能瓶颈；支持简单的缓存策略避免重复生成 |
| 线程安全 | Mock 数据生成器无状态设计，线程安全 |
| 扩展性 | 字段级 Mock 注解、数据生成策略均通过 SPI 接口扩展 |
| 兼容性 | 兼容 Spring MVC（Servlet）场景；Dubbo 场景兼容 Dubbo 3.x |
---
## 3 整体架构设计
### 3.1 模块内部结构
```
wvframework-mock/
└── src/main/java/com/github/walkvoid/wvframework/mock/
    ├── annotation/
    │   ├── Mock.java                              # 核心运行时开关注解（TYPE + METHOD）
    │   ├── MockName.java                          # 字段级：姓名
    │   ├── MockIdCardNo.java                      # 字段级：身份证号
    │   ├── MockAddress.java                       # 字段级：地址
    │   ├── MockPhone.java                         # 字段级：手机号
    │   ├── MockEmail.java                         # 字段级：邮箱
    │   ├── MockDate.java                          # 字段级：日期
    │   ├── MockNumber.java                        # 字段级：数值
    │   └── MockString.java                        # 字段级：通用字符串
    ├── enums/
    │   └── Mocklang.java                       # 国家枚举（zh-CN/en-US/ja-JP/ko-KR/DEFAULT 等）
    ├── model/
    │   ├── MockProperties.java                    # 配置属性类
    │   └── MockDataEntity.java                    # 数据库 Mock 数据实体
    ├── generator/
    │   ├── MockDataGenerator.java                 # 数据生成器 SPI 接口
    │   ├── FieldMockAnnotationResolver.java       # 字段级注解解析器
    │   ├── MockObjectFactory.java                 # Mock 对象工厂（核心：反射 + 注解生成对象）
    │   └── impl/
    │       ├── NameMockDataGenerator.java         # 姓名生成器
    │       ├── IdCardNoMockDataGenerator.java     # 身份证号生成器
    │       ├── AddressMockDataGenerator.java      # 地址生成器
    │       ├── PhoneMockDataGenerator.java        # 手机号生成器
    │       ├── EmailMockDataGenerator.java        # 邮箱生成器
    │       ├── DateMockDataGenerator.java         # 日期生成器
    │       ├── NumberMockDataGenerator.java       # 数值生成器
    │       └── StringMockDataGenerator.java       # 通用字符串生成器（默认兜底）
    ├── store/
    │   ├── MockDataStore.java                     # Mock 数据源 SPI 接口
    │   ├── DatabaseMockDataStore.java             # 数据库实现
    │   └── MockDataStoreAutoConfiguration.java    # 数据源自动配置
    ├── advisor/
    │   ├── MockAdvisor.java                       # 原生 Advisor（Pointcut + Advice）
    │   └── MockMethodInterceptor.java             # 核心 MethodInterceptor（Controller/Feign/@HttpExchange）
    ├── operation/                                 # ★ 元数据抽象层（仿 Spring Cache）
    │   ├── MockOperation.java                     # 不可变元数据对象（合并后的 @Mock 配置）
    │   ├── MockOperationSource.java               # 元数据源 SPI 接口
    │   ├── AnnotationMockOperationSource.java     # 默认实现（ConcurrentHashMap 缓存）
    │   └── MockOperationSourcePointcut.java      # 自定义 Pointcut（StaticMethodMatcherPointcut）
    ├── error/                                     # ★ 异常处理抽象层（仿 CacheErrorHandler）
    │   ├── MockErrorHandler.java                  # 异常处理器 SPI 接口
    │   └── SimpleMockErrorHandler.java            # 默认实现（log 后吞掉）
    ├── dubbo/
    │   └── MockDubboFilter.java                   # Dubbo Filter（Dubbo Provider 场景拦截）
    ├── registry/
    │   └── MockDataGeneratorRegistry.java         # 生成器注册表（注解类型 → 生成器映射）
    ├── autoconfigure/
    │   └── MockAutoConfiguration.java             # Spring Boot 自动配置
    └── util/
        └── MockUtils.java                         # 工具类
```
### 3.2 核心拦截原理
本组件参考 **Spring `@Cacheable` 的拦截链路**，引入三层抽象：
- **Operation 抽象**：`MockOperation` 不可变元数据对象
- **OperationSource 抽象**：`MockOperationSource` 解析 + 缓存元数据
- **ErrorHandler 抽象**：`MockErrorHandler` 拦截异常统一兜底

**核心思路**（详见 §4.5）：
1. 定义 `MockAdvisor`，Pointcut = `MockOperationSourcePointcut`，**在 Pointcut 阶段就调用 `MockOperationSource`**，复用同一份缓存判断是否有 `@Mock` 注解
2. `MockMethodInterceptor` 实现 `MethodInterceptor`，在 `invoke()` 中：
   - 通过 `MockOperationSource.getMockOperation(method, targetClass)` 取元数据（已缓存，无反射开销）
   - 检查 `enabled`、`mockRequest`、`mockResponse` 等属性
   - 若指定了 `value`（数据库 Mock 数据键），优先从 `MockDataStore` 查询数据库中的预配置 Mock 数据
   - 否则通过 `MockObjectFactory` 解析返回类型字段上的字段级 Mock 注解，生成 Mock 对象
   - 字段若没有任何 @MockXxx 注解，String 类型默认返回随机字符串
   - 直接返回 Mock 数据，**不执行原始业务逻辑**
   - 所有可能抛异常的调用（MockDataStore、MockObjectFactory）都包裹在 try-catch 中，由 `MockErrorHandler` 兜底
3. Dubbo 场景通过 `MockDubboFilter`（实现 `org.apache.dubbo.rpc.Filter`）拦截 Provider 端方法
### 3.3 四种拦截场景流程图
```plantuml
@startuml
actor "外部调用方" as Caller
actor "业务代码" as Biz
== 场景1: Controller 入站请求（原生 Advisor 拦截） ==
Caller -> "Spring AOP Proxy" : HTTP Request
"Spring AOP Proxy" -> "MockMethodInterceptor.invoke()" : 匹配 @Mock（类级或方法级）
"MockMethodInterceptor" -> "MockObjectFactory" : 根据返回类型生成 Mock 对象
note right
  优先级：
  1. 方法级 @Mock(value="dbKey") → 数据库查询
  2. MockObjectFactory 按字段注解生成
  3. 无注解字段 → 默认随机值
end note
Caller <-- "MockMethodInterceptor" : 直接返回 Mock 数据（不执行 Controller 逻辑）
== 场景2: Feign 出站请求（原生 Advisor 拦截） ==
Biz -> "Spring AOP Proxy" : Feign 接口方法调用
"Spring AOP Proxy" -> "MockMethodInterceptor.invoke()" : 匹配 @Mock（接口注解）
"MockMethodInterceptor" -> "MockObjectFactory" : 根据返回类型生成 Mock 对象
Biz <-- "MockMethodInterceptor" : 直接返回 Mock 数据（不调用远端服务）
== 场景3: @HttpExchange 出站请求（原生 Advisor 拦截） ==
Biz -> "Spring AOP Proxy" : @HttpExchange 接口方法调用
"Spring AOP Proxy" -> "MockMethodInterceptor.invoke()" : 匹配 @Mock（接口注解）
"MockMethodInterceptor" -> "MockObjectFactory" : 根据返回类型生成 Mock 对象
Biz <-- "MockMethodInterceptor" : 直接返回 Mock 数据（不发 HTTP 请求）
== 场景4: Dubbo Provider 请求（Dubbo Filter 拦截） ==
Caller -> "Dubbo Proxy" : RPC 调用
"Dubbo Proxy" -> "MockDubboFilter.invoke()" : 匹配 @Mock
"MockDubboFilter" -> "MockObjectFactory" : 根据返回类型生成 Mock 对象
Caller <-- "MockDubboFilter" : 直接返回 Mock 数据（不执行 Provider 逻辑）
@enduml
```
### 3.4 核心组件关系
```plantuml
@startuml
package "wvframework-mock" {
    [Mock Annotation] as Anno
    [MockMethodInterceptor] as Interceptor
    [MockObjectFactory] as Factory
    [FieldMockAnnotationResolver] as Resolver
    [MockDataGeneratorRegistry] as Registry
    [MockDataGenerator (SPI)] as Generator
    [MockAdvisor] as Advisor
    [MockDubboFilter] as DubboFilter
    [MockDataStore (SPI)] as DataStore
    [DatabaseMockDataStore] as DbStore
    Advisor --> Interceptor : Pointcut + Advice
    DubboFilter --> Factory : 直接调用
    Interceptor --> Factory : 委托生成 Mock 对象
    Interceptor --> DataStore : 查询数据库 Mock 数据
    Factory --> Resolver : 解析字段级注解
    Factory --> Registry : 查找对应生成器
    Registry --> Generator : 注解类型 → 生成器映射
    Interceptor --> Anno : 从 Method/Class 读取 @Mock
    DbStore ..|> DataStore : 实现
}
@enduml
```
---
## 4 接口设计
### 4.0 任务接口映射表
| 任务ID | 任务名称 | 接口类型 | 接口章节 | 接口名称 | 备注 |
|--------|----------|----------|----------|----------|------|
| MOCK-001 | `@Mock` 运行时开关注解 | 注解 | 4.1 | `Mock` | 核心注解，支持类级和方法级 |
| MOCK-002 | 字段级 Mock 注解体系 | 注解 | 4.2 | `@MockName` / `@MockIdCardNo` / `@MockAddress` 等 | 字段语义级 Mock，无注解字段默认随机值 |
| MOCK-003 | Mock 数据生成器 SPI | SPI | 4.3 | `MockDataGenerator` / `MockDataGeneratorRegistry` | 可扩展生成策略 |
| MOCK-004 | Mock 对象工厂 | 核心引擎 | 4.4 | `MockObjectFactory` / `FieldMockAnnotationResolver` | 反射 + 注解解析 + 默认兜底 |
| MOCK-005 | 原生 Advisor + MethodInterceptor | 核心拦截器 | 4.5 | `MockAdvisor` / `MockMethodInterceptor` | 类级 + 方法级统一拦截 |
| MOCK-006 | Dubbo Filter | Dubbo 拦截器 | 4.6 | `MockDubboFilter` | Dubbo Provider 场景 |
| MOCK-007 | 自动配置与属性 | 配置 | 4.7 | `MockAutoConfiguration` / `MockProperties` | Spring Boot 集成 |
| MOCK-008 | Mock 数据源 SPI | SPI | 4.8 | `MockDataStore` / `DatabaseMockDataStore` | 数据库 Mock 数据存取 |
---
### 4.1 `@Mock` 运行时开关注解（关联任务：MOCK-001）
**类型**：注解  
**文件位置**：`wvframework-mock/src/main/java/.../mock/annotation/Mock.java`  
**关联任务**：MOCK-001
**注解说明**：
`@Mock` 是一个**运行时开关注解**，可标注在**类**和**方法**上，根据标注位置具有不同的语义：
| 属性 | 类型 | 默认值 | 类级语义 | 方法级语义 |
|------|------|--------|----------|------------|
| value | String | `""` | 忽略 | **数据库 Mock 数据键**，从数据库查询预配置 Mock 数据；为空则按字段注解自动生成 |
| enabled | boolean | `true` | 是否启用该类的 Mock（运行时开关） | 是否启用该方法的 Mock（可覆盖类级和全局开关） |
| mocko-KRequest | boolean | `true` | 忽略 | 是否 Mock 请求体（入参） |
| mocko-KResponse | boolean | `true` | 忽略 | 是否 Mock 响应体（返回值） |
| count | int | `1` | 忽略 | 集合类型返回值的 Mock 数据条数 |
| delay | long | `0` | 忽略 | 模拟延迟（毫秒） |
**目标（Target）**：`TYPE` + `METHOD`  
**保留策略**：`RUNTIME`
**类级与方法级的关系**：
- 类级 `@Mock` 表示该类所有请求方法都启用 Mock
- 方法级 `@Mock` 可覆盖类级配置（如类级启用但某个方法级禁用）
- 方法级 `@Mock` 提供更细粒度的控制（mocko-KRequest/mocko-KResponse/dbKey 等）
**代码示例**：
```java
/**
 * Mock 运行时开关注解（类 + 方法）
 * 文件位置：mock/annotation/Mock.java
 *
 * 标注在类上：该类所有请求方法均启用 Mock
 * 标注在方法上：细粒度控制该方法的 Mock 行为
 *
 * 类级：作为运行时开关，控制整个 Controller 的 Mock 启用
 * 方法级：可控制 mocko-KRequest/mocko-KResponse、指定数据库 Mock 数据键等
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mock {
    /**
     * 方法级：数据库 Mock 数据键
     * 非空时优先从 MockDataStore 查询预配置 Mock 数据
     * 为空则按字段级 Mock 注解自动生成
     *
     * 类级：忽略
     */
    String value() default "";
    /** 是否启用 Mock（类级和方法级均有效） */
    boolean enabled() default true;
    /** 是否 Mock 请求体（仅方法级有效，默认 true） */
    boolean mocko-KRequest() default true;
    /** 是否 Mock 响应体（仅方法级有效，默认 true） */
    boolean mocko-KResponse() default true;
    /** 集合类型返回值的 Mock 数据条数（仅方法级有效） */
    int count() default 1;
    /** 模拟延迟（毫秒，仅方法级有效） */
    long delay() default 0;
}
```
**使用示例 — 类级（运行时开关）**：
```java
// ========== 示例1: 类级 @Mock — 整个 Controller 所有方法均 Mock ==========
@Mock
@RestController
@RequestMapping("/api/bank/callback")
public class BankCallbackController {
    @PostMapping("/notify")
    public Result<Callbacko-KRespDTO> onCallbackNotify(@RequestBody Callbacko-KReqDTO req) {
        // 此方法体不会被执行，直接返回 Mock 数据
        return bankService.handleCallback(req);
    }
    @PostMapping("/staten-US")
    public Result<Staten-USRespDTO> queryStaten-US(@RequestBody Staten-USReqDTO req) {
        // 同样被 Mock，不执行
        return bankService.queryStaten-US(req);
    }
}
// ========== 示例2: 类级 @Mock + 方法级细粒度覆盖 ==========
@Mock
@RestController
@RequestMapping("/api/en-USers")
public class en-USerController {
    // 继承类级 @Mock，所有方法默认 Mock
    @GetMapping("/{id}")
    public Result<en-USerInfoRespDTO> geten-USer(@PathVariable Long id) {
        return en-USerService.geten-USer(id);
    }
    // 方法级覆盖：指定数据库 Mock 数据键 + 只 Mock 响应体
    @Mock(value = "en-USer.list.default", mocko-KRequest = false, count = 10)
    @GetMapping("/list")
    public Result<List<en-USerInfoRespDTO>> listen-USers() {
        return en-USerService.listen-USers();
    }
    // 方法级覆盖：禁用该方法 Mock
    @Mock(enabled = false)
    @PostMapping("/create")
    public Result<Long> createen-USer(@RequestBody Createen-USerReqDTO req) {
        // 该方法正常执行业务逻辑
        return en-USerService.createen-USer(req);
    }
}
```
**使用示例 — 方法级（细粒度控制）**：
```java
// ========== 示例3: 仅方法级 @Mock — 指定数据库 Mock 数据 ==========
@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    // 银行回调接口：从数据库读取预配置的"回调成功"Mock 数据
    @Mock(value = "bank.callback.success")
    @PostMapping("/bank/notify")
    public Result<BankCallbacko-KRespDTO> onBankNotify(@RequestBody BankNotifyReqDTO req) {
        return paymentService.handleBankNotify(req);
    }
    // 普通 Mock：按字段注解自动生成
    @Mock
    @GetMapping("/order/{id}")
    public Result<OrderRespDTO> getOrder(@PathVariable Long id) {
        return paymentService.getOrder(id);
    }
    // 只 Mock 响应体，不 Mock 请求体
    @Mock(mocko-KRequest = false)
    @PostMapping("/refund")
    public Result<RefundRespDTO> refund(@RequestBody RefundReqDTO req) {
        // 请求体正常处理，响应体返回 Mock 数据
        return paymentService.refund(req);
    }
}
// ========== 示例4: 标注在 Feign Client 方法上 ==========
@FeignClient(name = "en-USer-service", path = "/api/en-USers")
public interface en-USerFeignClient {
    @Mock
    @GetMapping("/{id}")
    Result<en-USerDTO> geten-USerById(@PathVariable("id") Long id);
}
// ========== 示例5: 标注在 @HttpExchange 接口方法上 ==========
@HttpExchange(url = "http://en-USer-service/api/en-USers")
public interface en-USerHttpApi {
    @Mock
    @GetExchange("/{id}")
    Result<en-USerDTO> geten-USerById(@PathVariable("id") Long id);
}
// ========== 示例6: 标注在 Dubbo Provider 接口上 ==========
@DubboService
public class en-USerServiceProvider implements en-USerService {
    @Mock
    @Override
    public Result<en-USerDTO> geten-USerById(Long id) {
        // 此方法体不会被执行
        return en-USerService.geten-USerById(id);
    }
}
```
---
### 4.2 字段级 Mock 注解体系（关联任务：MOCK-002）
**类型**：注解  
**文件位置**：`wvframework-mock/src/main/java/.../mock/annotation/`  
**关联任务**：MOCK-002
**设计说明**：
字段级注解标注在 DTO / VO 实体的属性上，根据字段的业务语义随机生成 Mock 数据。每个语义级注解提供 `lang` 属性，支持按国家/地区生成不同风格的数据，兼容国际化通用处理。
**默认 Mock 策略**：如果字段**没有任何 `@MockXxx` 注解**，框架根据字段类型提供默认的随机值：
| 字段类型 | 默认 Mock 值 |
|----------|-------------|
| `String` | 随机字符串（8~16 位，含字母和数字） |
| `int` / `Integer` | 随机整数（0~10000） |
| `long` / `Long` | 随机长整数（0~1000000） |
| `double` / `Double` | 随机浮点数（0.0~10000.0） |
| `boolean` / `Boolean` | 随机布尔值 |
| `BigDecimal` | 随机金额（0.00~10000.00，保留 2 位小数） |
| `Date` / `LocalDateTime` / `LocalDate` | 随机日期（近一年内） |
| 枚举类型 | 随机选取一个枚举值 |
| 复杂对象 | 递归处理内部字段 |
#### Mocklang（语言枚举）
| 值 | 说明 |
|----|------|
| zh-CN | 中国中文 |
| en-US | 美国英文 |
| ja-JP | 日本日文 |
| ko-KR | 韩国韩文 |
| AUTO | 自动模式，复用当前环境的多语言配置进行自动切换 |
**AUTO 模式说明**：
- 当 lang 设置为 AUTO 时，框架会自动获取当前 Spring 应用的 Locale 配置
- 例如：当前环境 locale 为 zh-CN，则自动使用中文多语言配置生成数据
- 这种方式适用于微服务需要根据请求头 Accept-Language 动态切换多语言的场景
- 框架会从 HTTP 请求头中获取 Accept-Language，并动态切换 Mock 数据的多语言配置
#### `@MockName`（姓名）
| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| lang | Mocklang | `Mocklang.AUTO` | 语言，支持 zh-CN、en-US、ja-JP、ko-KR、AUTO，设置 AUTO 时自动获取当前环境的多语言配置 |
```java
public class en-USerInfoRespDTO {
    @MockName(lang = Mocklang.zh-CN)   // 生成如 "张三"、"李四"
    private String en-USername;
    @MockName(lang = Mocklang.en-US)   // 生成如 "John Smith"
    private String englishName;
    @MockName(lang = Mocklang.ja-JP)   // 生成如 "田中太郎"
    private String japaneseName;
    @MockName(lang = Mocklang.ko-KR)   // 生成如 "김민수"
    private String koreanName;
}
```
#### `@MockIdCardNo`（身份证号）
| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| lang | Mocklang | `Mocklang.AUTO` | 语言，支持 zh-CN、en-US、ja-JP、ko-KR、AUTO，设置 AUTO 时自动获取当前环境的多语言配置 |
#### `@MockAddress`（地址）
| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| lang | Mocklang | `Mocklang.AUTO` | 语言，支持 zh-CN、en-US、ja-JP、ko-KR、AUTO，设置 AUTO 时自动获取当前环境的多语言配置 |
```java
public class en-USerInfoRespDTO {
    @MockAddress(lang = Mocklang.zh-CN)
    // 生成如 "浙江省杭州市西湖区文三路 100 号"
    private String address;
}
public class en-USeren-USDTO {
    @MockAddress(lang = Mocklang.en-US)
    // 生成如 "123 Main St, Springfield, IL 62701"
    private String address;
}
public class en-USerja-JPDTO {
    @MockAddress(lang = Mocklang.ja-JP)
    // 生成如 "東京都渋谷区道玄坂1-2-3"
    private String address;
}
```
#### `@MockPhone`（手机号）
| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| lang | Mocklang | `Mocklang.AUTO` | 语言，支持 zh-CN、en-US、ja-JP、ko-KR、AUTO，设置 AUTO 时自动获取当前环境的多语言配置 |
#### `@MockEmail`（邮箱）
| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| lang | Mocklang | `Mocklang.AUTO` | 语言，设置为 AUTO 时自动获取当前环境的多语言配置，用于生成对应语言的邮箱域名 |
| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| domain | String | `""` | 指定邮箱域名，为空则随机生成 |
#### `@MockDate`（日期）
| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| pattern | String | `"yyyy-MM-dd"` | 日期格式 |
| start | String | `""` | 起始日期（含），为空则不限制 |
| end | String | `""` | 结束日期（含），为空则不限制 |
#### `@MockNumber`（数值）
| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| min | long | `0` | 最小值 |
| max | long | `10000` | 最大值 |
#### @MockString（通用字符串 - 核心注解）
@MockString 是字段级 Mock 注解的核心基础注解，@MockName、@MockAddress 等复合注解底层都基于 @MockString 实现。该注解支持从多语言配置文件中随机获取值，也可使用 26 个英文字母生成随机字符串。
| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| prefix | String | "" | 前缀 |
| length | int | 10 | 字符串长度（当 source 为 LETTERS 时生效） |
| values | @MockString[] | {} | 嵌套的 @MockString 数组，用于组合多个数据源 |
| lang | String | "auto" | 多语言标识，支持 zh-CN、en-US、ja-JP、ko-KR 等，值为 uto 时复用当前环境的多语言配置进行自动切换 |
| source | MockSource | MockSource.AUTO | 数据来源：AUTO（自动）、LETTERS（26 个英文字母）、CONFIG（多语言配置文件） |
| configKey | String | "" | 多语言配置文件 KEY，当 source 为 CONFIG 时必须指定 |
**MockSource 枚举**：
| 值 | 说明 |
|----|------|
| AUTO | 自动模式，根据 lang 配置自动选择数据来源 |
| LETTERS | 从 26 个英文字母（A-Z）中随机生成指定长度的字符串 |
| CONFIG | 从多语言配置文件中随机获取值 |
**完整使用示例**：
**多语言配置文件格式**：
多语言配置文件采用 properties 格式，KEY 为配置项名称，VALUE 为逗号分隔的随机值列表。例如：
``properties
# mock-surname-zh-CN.properties（中文姓氏）
surname=赵,钱,孙,李,周,吴,郑,王,冯,陈,褚,卫,蒋,沈,韩,杨,朱,秦,尤,许,何,吕,施,张,孔,曹,严,华,金,魏,陶,姜
# mock-givenname-zh-CN.properties（中文名字常用字）
givenname=伟,芳,娜,秀英,敏,静,丽,强,磊,军,洋,勇,艳,杰,涛,明,超,秀兰,霞,平,刚,桂英,建华
# mock-provinces-zh-CN.properties（中国省份）
province=北京市,天津市,上海市,重庆市,河北省,山西省,辽宁省,吉林省,黑龙江省,江苏省,浙江省
# mock-cities-zh-CN.properties（中国城市）
city=北京市,上海市,天津市,重庆市,广州市,深圳市,成都市,杭州市,武汉市,西安市,南京市
# mock-counties-zh-CN.properties（中国区县）
county=朝阳区,海淀区,西城区,东城区,丰台区,石景山区,通州区,顺义区,昌平区,大兴区
# mock-streets-zh-CN.properties（街道/路）
street=人民路,中山路,解放路,建设路,文化路,新华路,和平路,光明路,胜利路,工农路
``
**配置文件加载规则**：
1. 配置文件放在 classpath:mock/i18n/ 目录下
2. 文件命名格式：mock-{configKey}-{lang}.properties，如 mock-surname-zh-CN.properties
3. 当 lang 为 uto 时，框架会自动获取当前 Spring 应用的 locale 配置（如 zh-CN），并加载对应的多语言配置文件
4. 如果指定的 lang 对应文件不存在，降级到默认配置文件
**@MockString 使用示例**：
``java
public class UserDTO {
    // 使用多语言配置文件生成姓名
    @MockString(configKey = "surname", lang = "zh-CN")
    @MockString(configKey = "givenname", lang = "zh-CN", count = 2)
    private String name;  // 生成如 "张三"、"李四"、"王五" 等
    // 使用多语言配置文件生成地址
    @MockString(configKey = "province", lang = "zh-CN")
    private String province;
    
    @MockString(configKey = "city", lang = "zh-CN")  
    private String city;
    
    @MockString(configKey = "county", lang = "zh-CN")
    private String county;
    // 使用 26 个英文字母生成随机字符串
    @MockString(source = MockSource.LETTERS, length = 8)  // 生成如 "aKf9xQm7"
    private String randomCode;
    // auto 模式：自动使用当前环境的多语言配置
    @MockString(configKey = "surname", lang = "auto")
    private String autoName;
}
``
/**
 * 用户信息响应 DTO
 * 文件位置：业务模块 DTO
 *
 * 字段上标注不同的 Mock 注解，MockObjectFactory 自动解析并生成 Mock 数据
 * 没有标注任何 @MockXxx 的 String 字段，默认返回随机字符串
 */
public class en-USerInfoRespDTO {
    @MockName(lang = Mocklang.zh-CN)
    private String en-USername;          // Mock: "张三"
    @MockIdCardNo(lang = Mocklang.zh-CN)
    private String idCardNo;          // Mock: "330106199001011234"
    @MockPhone(lang = Mocklang.zh-CN)
    private String phone;             // Mock: "13800138000"
    @MockAddress(lang = Mocklang.zh-CN)
    private String address;           // Mock: "浙江省杭州市西湖区文三路 100 号"
    @MockEmail
    private String email;             // Mock: "abc123@qq.com"
    @MockDate(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime; // Mock: "2026-01-15 10:30:00"
    @MockNumber(min = 1, max = 100)
    private Integer age;              // Mock: 25
    // 没有任何 @MockXxx 注解的 String 字段 → 默认随机字符串
    private String remark;            // Mock: "a3Kf9xQm"（随机串）
   // 没有任何 @MockXxx 注解的 Long 字段 → 默认随机数
    private Long score;               // Mock: 583920（随机数）
}
```
<!-- APPEND_MARKER_1 -->
---
### 4.3 Mock 数据生成器 SPI（关联任务：MOCK-003）
**类型**：SPI 接口 + 注册表 + 内置实现  
**文件位置**：`wvframework-mock/src/main/java/.../mock/generator/`  
**关联任务**：MOCK-003
#### `MockDataGenerator<T>`（SPI 接口）
```java
/**
 * Mock 数据生成器 SPI 接口
 * 文件位置：mock/generator/MockDataGenerator.java
 *
 * @param <T> 生成的数据类型
 */
public interface MockDataGenerator<T> {
    /**
     * 生成 Mock 数据
     * @param lang 国家/地区
     * @return 生成的 Mock 数据
     */
    T generate(Mocklang lang);
}
```
#### `MockDataGeneratorRegistry`（注册表）
```java
/**
 * 生成器注册表：注解类型 → 生成器实例
 * 文件位置：mock/registry/MockDataGeneratorRegistry.java
 *
 * Spring Boot 启动时自动发现所有 MockDataGenerator Bean 并注册
 */
public class MockDataGeneratorRegistry {
    // Class<? extends Annotation> → MockDataGenerator<?>
    private final Map<Class<? extends Annotation>, MockDataGenerator<?>> generatorMap = new ConcurrentHashMap<>();
    public void register(Class<? extends Annotation> annotationType, MockDataGenerator<?> generator) {
        generatorMap.put(annotationType, generator);
    }
    public MockDataGenerator<?> getGenerator(Class<? extends Annotation> annotationType) {
        return generatorMap.get(annotationType);
    }
    public boolean hasGenerator(Class<? extends Annotation> annotationType) {
        return generatorMap.containsKey(annotationType);
    }
}
```
#### 内置生成器实现
| 生成器 | 对应注解 | 生成示例（zh-CN） | 生成示例（en-US） |
|--------|----------|---------------|---------------|
| `NameMockDataGenerator` | `@MockName` | "张三" | "John Smith" |
| `IdCardNoMockDataGenerator` | `@MockIdCardNo` | "330106199001011234" | "123-45-6789" |
| `AddressMockDataGenerator` | `@MockAddress` | "浙江省杭州市西湖区文三路 100 号" | "123 Main St, Springfield, IL 62701" |
| `PhoneMockDataGenerator` | `@MockPhone` | "13800138000" | "+1 (202) 555-0123" |
| `EmailMockDataGenerator` | `@MockEmail` | "abc123@qq.com" | "john.doe@gmail.com" |
| `DateMockDataGenerator` | `@MockDate` | "2026-03-15" | "2026-03-15" |
| `NumberMockDataGenerator` | `@MockNumber` | 42 | 42 |
| `StringMockDataGenerator` | `@MockString` | "a3Kf9xQm7b" | "a3Kf9xQm7b" |
**默认兜底生成器**：当字段没有任何 `@MockXxx` 注解时，`MockObjectFactory` 使用内置的默认类型生成策略：
```java
/**
 * 默认类型生成策略（无 @MockXxx 注解时使用）
 * 文件位置：mock/generator/impl/DefaultTypeGenerator.java
 */
public class DefaultTypeGenerator {
    public static Object generate(Class<?> type) {
        if (type == String.class) {
            // 随机字符串 8~16 位，含字母和数字
            return RandomStringUtils.randomAlphanumeric(8, 16);
        } else if (type == int.class || type == Integer.class) {
            return ThreadLocalRandom.current().nextInt(0, 10001);
        } else if (type == long.class || type == Long.class) {
            return ThreadLocalRandom.current().nextLong(0, 1000001L);
        } else if (type == double.class || type == Double.class) {
            return ThreadLocalRandom.current().nextDouble(0.0, 10000.0);
        } else if (type == boolean.class || type == Boolean.class) {
            return ThreadLocalRandom.current().nextBoolean();
        } else if (type == BigDecimal.class) {
            return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(0.0, 10000.0))
                    .setScale(2, RoundingMode.HALF_UP);
        } else if (type == Date.class || type == LocalDateTime.class || type == LocalDate.class) {
            return generateRandomDate();
        } else if (type.isEnum()) {
            Object[] constants = type.getEnumConstants();
            return constants[ThreadLocalRandom.current().nextInt(constants.length)];
        }
        return null;
    }
}
```
---
### 4.4 Mock 对象工厂（关联任务：MOCK-004）
**类型**：核心引擎  
**文件位置**：`wvframework-mock/src/main/java/.../mock/generator/MockObjectFactory.java`  
**关联任务**：MOCK-004
#### `MockObjectFactory`
```java
/**
 * Mock 对象工厂：根据返回类型 + 字段级注解，反射生成 Mock 对象
 * 文件位置：mock/generator/MockObjectFactory.java
 *
 * 核心流程：
 * 1. 解析返回类型的泛型参数
 * 2. 反射创建实例
 * 3. 遍历字段，查找 @MockXxx 注解
 * 4. 有注解 → 从 Registry 查找生成器
 * 5. 无注解 → 使用 DefaultTypeGenerator 默认策略
 * 6. 嵌套复杂对象递归处理（最大深度 3 层）
 */
public class MockObjectFactory {
    private final MockDataGeneratorRegistry registry;
    private static final int MAX_DEPTH = 3;
    public MockObjectFactory(MockDataGeneratorRegistry registry) {
        this.registry = registry;
    }
    /**
     * 根据返回类型生成 Mock 对象
     */
    public Object createMockObject(Type returnType, Mocklang lang, int count) {
        Class<?> rawType = resolveRawType(returnType);
        // 集合类型：生成 count 个元素
        if (Collection.class.isAssignableFrom(rawType)) {
            Type elementType = resolveGenericType(returnType);
            return createCollection(rawType, elementType, lang, count);
        }
        return createInstance(rawType, lang, 0);
    }
    /**
     * 创建单个对象实例
     */
    private Object createInstance(Class<?> type, Mocklang lang, int depth) {
        if (depth > MAX_DEPTH) return null;
        // 基本类型 / 包装类型 / String → 默认值
        if (isSimpleType(type)) {
            return DefaultTypeGenerator.generate(type);
        }
        Object instance = instantiate(type);
        for (Field field : getAllFields(type)) {
            field.setAccessible(true);
            Object value = resolveFieldValue(field, lang, depth);
            if (value != null) {
                field.set(instance, value);
            }
        }
        return instance;
    }
    /**
     * 解析字段值：优先 @MockXxx 注解 → 默认类型生成
     */
    private Object resolveFieldValue(Field field, Mocklang lang, int depth) {
        // 查找字段上的 Mock 注解
        Annotation mockAnnotation = FieldMockAnnotationResolver.resolve(field);
        if (mockAnnotation != null) {
            MockDataGenerator<?> generator = registry.getGenerator(mockAnnotation.annotationType());
            if (generator != null) {
                return generator.generate(lang);
            }
        }
        // 无注解 → 默认类型生成
        return DefaultTypeGenerator.generate(field.getType());
    }
}
```
#### `FieldMockAnnotationResolver`
```java
/**
 * 字段级 Mock 注解解析器
 * 文件位置：mock/generator/FieldMockAnnotationResolver.java
 *
 * 扫描字段上的所有注解，返回第一个已注册的 Mock 注解
 */
public class FieldMockAnnotationResolver {
    private static final Set<Class<? extends Annotation>> MOCK_ANNOTATIONS = Set.of(
        MockName.class, MockIdCardNo.class, MockAddress.class,
        MockPhone.class, MockEmail.class, MockDate.class,
        MockNumber.class, MockString.class
    );
    public static Annotation resolve(Field field) {
        for (Class<? extends Annotation> annoType : MOCK_ANNOTATIONS) {
            Annotation anno = field.getAnnotation(annoType);
            if (anno != null) return anno;
        }
        return null;  // 无 Mock 注解 → 使用默认策略
    }
}
```
---
﻿### 4.5 原生 Advisor + MethodInterceptor（关联任务：MOCK-005）
**类型**：核心拦截器  
**文件位置**：`wvframework-mock/src/main/java/.../mock/advisor/` + `mock/operation/` + `mock/error/`  
**关联任务**：MOCK-005
#### 4.5.1 整体架构
本组件参考 **Spring `@Cacheable` 的拦截链路**（参见 Spring Cache 的 `CacheInterceptor` + `BeanFactoryCacheOperationSourceAdvisor` + `AnnotationCacheOperationSource` + `CacheErrorHandler`）实现，引入三层抽象：
```
调用方 → MockAdvisor (Pointcut + Advice)
              │
              ├─ MockOperationSourcePointcut  (StaticMethodMatcherPointcut)
              │       └─ MockOperationSource.getMockOperation(method, targetClass)
              │              └─ AnnotationMockOperationSource
              │                     └─ ConcurrentHashMap<CacheKey, MockOperation>  (元数据缓存)
              │
              └─ MockMethodInterceptor (MethodInterceptor)
                     ├─ 读取 MockOperation（已缓存）
                     ├─ isMockEnabled() —— 全局 / 方法类型 / 注解开关
                     ├─ MockObjectFactory.createMockObject()
                     │       ├─ MockDataStore (value 有值时优先)
                     │       └─ 字段级 @MockXxx 反射生成
                     └─ MockErrorHandler  (异常兜底)
                            ├─ handleMockDataStoreError
                            ├─ handleMockObjectError
                            └─ handleInvocationError
```
#### 4.5.2 各组件职责
| 类 | 职责 | 对应 Spring Cache 类 |
|----|------|----------------------|
| `MockOperation` | 不可变元数据对象，保存合并后的 `@Mock` 配置（含 method、targetClass） | `CacheableOperation` |
| `MockOperationSource` (SPI) | "查找 Mock 元数据"的 SPI 接口 | `CacheOperationSource` |
| `AnnotationMockOperationSource` | 默认实现：解析 `@Mock` + 合并方法级 / 类级 + `ConcurrentHashMap` 缓存 | `AnnotationCacheOperationSource` |
| `MockOperationSourcePointcut` | 自定义 `StaticMethodMatcherPointcut`，在 Pointcut 阶段就调用 `OperationSource` | `CacheOperationSourcePointcut` |
| `MockAdvisor` | 注册 `Pointcut + Advice` | `BeanFactoryCacheOperationSourceAdvisor` |
| `MockMethodInterceptor` | 运行时拦截，读取 `MockOperation`，调用 `MockObjectFactory` | `CacheInterceptor` |
| `MockErrorHandler` (SPI) | 拦截异常处理：数据源挂了 / Mock 对象生成失败时降级 | `CacheErrorHandler` |
| `SimpleMockErrorHandler` | 默认实现：log 后吞掉，不阻断业务请求 | `SimpleCacheErrorHandler` |
#### 4.5.3 `MockOperation`（不可变元数据）
仿照 `CacheableOperation`，把 `@Mock` 解析为运行时不可变对象，实现基于 `Method + targetClass` 的 `equals/hashCode` 作为缓存 Key。
```java
/**
 * Mock 操作元数据（不可变对象）
 * 文件位置：mock/operation/MockOperation.java
 */
public class MockOperation {
    private final String value;          // 数据键
    private final boolean enabled;       // 合并后的 enabled
    private final boolean mockRequest;   // 合并后的 mockRequest
    private final boolean mockResponse;  // 合并后的 mockResponse
    private final int count;             // 集合条数
    private final long delay;            // 延迟
    private final String description;    // 描述
    private final Method method;         // 触发方法
    private final Class<?> targetClass;  // 触发类
    // getters + equals/hashCode + CacheKey 内部类
}
```
#### 4.5.4 `MockOperationSource` + `AnnotationMockOperationSource`（元数据源 + 缓存）
```java
/**
 * 元数据源 SPI
 * 文件位置：mock/operation/MockOperationSource.java
 */
public interface MockOperationSource {
    MockOperation getMockOperation(Method method, Class<?> targetClass);
}

/**
 * 默认实现：基于注解 + ConcurrentHashMap 缓存
 * 文件位置：mock/operation/AnnotationMockOperationSource.java
 *
 * 关键点：
 * 1. 用 computeIfAbsent 保证只解析一次
 * 2. 合并方法级 + 类级 @Mock（方法级优先，未显式设置时回落到类级）
 * 3. 通过 AnnotationUtils.findAnnotation 支持接口 / 父类上的 @Mock
 * 4. 通过 BridgeMethodResolver 处理桥接方法
 */
public class AnnotationMockOperationSource implements MockOperationSource {
    private final ConcurrentMap<MockOperation.CacheKey, MockOperation> operationCache
            = new ConcurrentHashMap<>(256);

    @Override
    public MockOperation getMockOperation(Method method, Class<?> targetClass) {
        Mock classMock = findClassAnnotation(targetClass);
        Mock methodMock = findMethodAnnotation(method, targetClass);
        if (classMock == null && methodMock == null) return null;

        MockOperation.CacheKey key = new MockOperation.CacheKey(method, targetClass);
        return operationCache.computeIfAbsent(key,
            k -> parseMockOperation(method, targetClass, methodMock, classMock));
    }
}
```
**收益**：
- 同一种方法无论被调用多少次，只解析一次 `@Mock` 注解（之前每次 invoke 都做反射）
- Pointcut 与 Interceptor 共享同一份缓存，匹配阶段也零反射开销
- 业务方继承 `AnnotationMockOperationSource` 即可自定义合并 / 优先级逻辑
#### 4.5.5 `MockOperationSourcePointcut`（真正的 Pointcut）
仿照 Spring Cache 的 `CacheOperationSourcePointcut`，继承 `StaticMethodMatcherPointcut`，在 Pointcut 阶段就调用 `OperationSource`。
```java
/**
 * 基于 MockOperationSource 的 Pointcut
 * 文件位置：mock/operation/MockOperationSourcePointcut.java
 */
public class MockOperationSourcePointcut extends StaticMethodMatcherPointcut {
    private final MockOperationSource operationSource;

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        // 跳过 Object / 内部方法
        if (method.getDeclaringClass().equals(Object.class)) return false;
        // ★ 关键：把"是否有 @Mock"的判定交给 OperationSource
        return operationSource.getMockOperation(method, targetClass) != null;
    }
}
```
**对比 `AnnotationMatchingPointcut`**：
| 维度 | `AnnotationMatchingPointcut`（旧） | `MockOperationSourcePointcut`（新） |
|------|-----------------------------------|--------------------------------------|
| 接口 / 父类上的 `@Mock` | 需要显式 `checkInherited=true` | 默认支持（由 OperationSource 统一处理） |
| `enabled=false` 场景 | 仍会创建代理，运行时再跳过 | Pointcut 阶段就剪枝，不创建代理 |
| 反射开销 | 每次 Pointcut 评估都做反射 | 命中 OperationSource 缓存，零反射 |
#### 4.5.6 `MockErrorHandler` + `SimpleMockErrorHandler`（异常兜底）
仿照 `CacheErrorHandler`：
```java
/**
 * Mock 拦截异常处理器（SPI）
 * 文件位置：mock/error/MockErrorHandler.java
 */
public interface MockErrorHandler {
    void handleMockDataStoreError(Throwable ex, MockOperation operation, String key);
    void handleMockObjectError(Throwable ex, MockOperation operation);
    void handleInvocationError(Throwable ex, MockOperation operation, MethodInvocation invocation);
}

/**
 * 默认实现：log 后吞掉，不阻断业务
 * 文件位置：mock/error/SimpleMockErrorHandler.java
 */
public class SimpleMockErrorHandler implements MockErrorHandler {
    @Override
    public void handleMockDataStoreError(Throwable ex, MockOperation op, String key) {
        log.warn("[Mock] MockDataStore read failed, key={}, ...", key, ...);
    }
    // ... 其他方法类似
}
```
**调用方包裹示例**（在 `MockMethodInterceptor` 中）：
```java
try {
    Object fromStore = mockObjectFactory.getMockDataByKey(operation.getValue());
    if (fromStore != null) return fromStore;
} catch (Throwable ex) {
    errorHandler.handleMockDataStoreError(ex, operation, operation.getValue());
    // 降级：继续走自动生成
}
```
**收益**：
- Mock 数据源（数据库 / Redis）挂了不会拖垮业务请求
- 业务方可以注入自定义 `MockErrorHandler` 实现"失败时切换备用数据源 / 发告警 / 抛出"等策略
#### 4.5.7 `MockAdvisor`
```java
/**
 * Mock Advisor
 * 文件位置：mock/advisor/MockAdvisor.java
 *
 * 相比旧实现：使用基于 OperationSource 的 Pointcut，
 * 替换了 AnnotationMatchingPointcut。
 */
public class MockAdvisor extends AbstractPointcutAdvisor {
    private final Pointcut pointcut;
    private final MethodInterceptor advice;

    public MockAdvisor(MethodInterceptor interceptor, MockOperationSource operationSource) {
        this.pointcut = new MockOperationSourcePointcut(operationSource);
        this.advice = interceptor;
    }

    @Override public Pointcut getPointcut() { return pointcut; }
    @Override public Advice getAdvice() { return advice; }
}
```
#### 4.5.8 `MockMethodInterceptor`
```java
/**
 * Mock 方法拦截器
 * 文件位置：mock/advisor/MockMethodInterceptor.java
 *
 * 关键变化：
 * 1. 注入 MockOperationSource，元数据从缓存读取
 * 2. 注入 MockErrorHandler，所有可能抛异常的调用都包裹在 try-catch 里
 * 3. 不再直接调用 method.getAnnotation()
 */
public class MockMethodInterceptor implements MethodInterceptor {
    private final MockObjectFactory mockObjectFactory;
    private final MockProperties properties;
    private final MockOperationSource operationSource;
    private final MockErrorHandler errorHandler;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        Class<?> targetClass = invocation.getThis() != null
                ? invocation.getThis().getClass()
                : method.getDeclaringClass();

        // 1. 从 OperationSource 取元数据（带缓存）
        MockOperation operation;
        try {
            operation = operationSource.getMockOperation(method, targetClass);
        } catch (Throwable ex) {
            errorHandler.handleInvocationError(ex, null, invocation);
            return invocation.proceed();
        }
        if (operation == null) return invocation.proceed();

        // 2. 开关判断
        if (!isMockEnabled(operation, method)) return invocation.proceed();

        // 3. 延迟 / Mock 请求体 / Mock 响应体（异常都走 errorHandler）
        handleDelay(operation);
        if (operation.isMockRequest()) handleMockRequest(invocation.getArguments(), operation);
        if (operation.isMockResponse()) return createMockObject(invocation, method, operation);

        return invocation.proceed();
    }
}
```
#### 4.5.9 与 Spring `@Cacheable` 拦截链路的对照
| 维度 | Spring `@Cacheable` | 本组件 `@Mock` |
|------|---------------------|----------------|
| 拦截器 | `CacheInterceptor` | `MockMethodInterceptor` |
| Advisor | `BeanFactoryCacheOperationSourceAdvisor` | `MockAdvisor` |
| Pointcut | `CacheOperationSourcePointcut` (StaticMethodMatcher) | `MockOperationSourcePointcut` |
| 元数据源 | `CacheOperationSource` | `MockOperationSource` |
| 元数据实现 | `AnnotationCacheOperationSource` (带缓存) | `AnnotationMockOperationSource` (带缓存) |
| 元数据对象 | `CacheableOperation` | `MockOperation` |
| 异常处理 | `CacheErrorHandler` | `MockErrorHandler` |
| 默认 ErrorHandler | `SimpleCacheErrorHandler` (吞掉) | `SimpleMockErrorHandler` (吞掉) |
| 缓存后端 | `CacheManager` → Redis / Caffeine | `MockDataStore` → DB / Redis（可选） |
| SpEL 支持 | ✅ (`key="#id"`, `condition=`, `unless=`) | ❌（静态值） |
| 多注解组合 | ✅ (`@Caching`) | ❌ |
| 同步模式 | ✅ (`sync=true`) | ❌ |
参考价值：本组件的三层抽象（`Operation` + `OperationSource` + `ErrorHandler`）直接借鉴自 Spring Cache 的成熟设计，后续如需支持 SpEL / `@Mocking` 组合 / sync 模式，只需在 `MockOperation` 与 `AnnotationMockOperationSource` 上扩展即可，骨架无需变更。
### 4.6 Dubbo Filter（关联任务：MOCK-006）
**类型**：Dubbo 拦截器  
**文件位置**：`wvframework-mock/src/main/java/.../mock/dubbo/MockDubboFilter.java`  
**关联任务**：MOCK-006
```java
/**
 * Dubbo Mock Filter：拦截 Provider 端方法上的 @Mock 注解
 * 文件位置：mock/dubbo/MockDubboFilter.java
 *
 * 通过 @Activate(group = "provider") 确保仅在 Provider 端生效
 */
@Activate(group = "provider")
public class MockDubboFilter implements Filter {
    private final MockObjectFactory mockObjectFactory;
    private final MockProperties properties;
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (!properties.isEnabled()) {
            return invoker.invoke(invocation);
        }
        // 查找 @Mock 注解（方法级 > 类级）
        Method method = findMethod(invoker, invocation);
        Mock mock = resolveMockAnnotation(method);
        if (mock == null || !mock.enabled()) {
            return invoker.invoke(invocation);
        }
        // 生成 Mock 数据
        Object mocko-KResult = mockObjectFactory.createMockObject(
            method.getGenericReturnType(),
            properties.getDefaultlang(),
            mock.count()
        );
        return AsyncRpcResult.newResult(mocko-KResult);
    }
    private Mock resolveMockAnnotation(Method method) {
        if (method == null) return null;
        Mock mock = method.getAnnotation(Mock.class);
        if (mock != null) return mock;
        return method.getDeclaringClass().getAnnotation(Mock.class);
    }
}
```
**SPI 注册文件**：`META-INF/dubbo/org.apache.dubbo.rpc.Filter`
```
mockFilter=com.github.walkvoid.wvframework.mock.dubbo.MockDubboFilter
```
---
### 4.7 自动配置与属性（关联任务：MOCK-007）
**类型**：Spring Boot 配置  
**文件位置**：
- `.../mock/model/MockProperties.java`
- `.../mock/autoconfigure/MockAutoConfiguration.java`
**关联任务**：MOCK-007
#### 配置属性
```yaml
wv:
  mock:
    enabled: true                    # 全局开关
    default-count: 1                 # 默认集合数据条数（注解 count 优先）
    scan-packages:                   # 额外扫描的包路径（字段级注解扫描范围）
      - com.github.walkvoid
    dubbo:
      enabled: true                  # Dubbo Filter 开关
    store:
      enabled: true                  # 数据库 Mock 数据源开关
      table: wv_mock_data            # Mock 数据表名
```
#### 自动配置类
```java
/**
 * Mock 自动配置
 * 文件位置：mock/autoconfigure/MockAutoConfiguration.java
 *
 * 核心逻辑：
 * 1. 读取 wv.mock.* 配置
 * 2. 注册 MockDataGeneratorRegistry 并自动发现所有 MockDataGenerator Bean
 * 3. 注册 MockObjectFactory
 * 4. 注册 MockAdvisor（统一拦截 Controller + Feign + @HttpExchange）
 * 5. 条件注册 MockDataStore（数据库 Mock 数据源）
 * 6. 条件注册 MockDubboFilter（Dubbo 场景）
 */
@Configuration
@ConditionalOnProperty(name = "wv.mock.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(MockProperties.class)
public class MockAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public MockDataGeneratorRegistry mockDataGeneratorRegistry(
            ObjectProvider<List<MockDataGenerator<?>>> generatorProvider) {
        MockDataGeneratorRegistry registry = new MockDataGeneratorRegistry();
        registerBuiltinGenerators(registry);
        List<MockDataGenerator<?>> generators = generatorProvider.getIfAvailable();
        if (generators != null) {
            generators.forEach(registry::register);
        }
        return registry;
    }
    @Bean
    @ConditionalOnMissingBean
    public FieldMockAnnotationResolver fieldMockAnnotationResolver(MockDataGeneratorRegistry registry) {
        return new FieldMockAnnotationResolver(registry);
    }
    @Bean
    @ConditionalOnMissingBean
    public MockObjectFactory mockObjectFactory(MockDataGeneratorRegistry registry,
                                                FieldMockAnnotationResolver resolver) {
        return new MockObjectFactory(registry, resolver);
    }
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public MockAdvisor mockAdvisor(MockObjectFactory factory, MockDataStore dataStore, MockProperties properties) {
        return new MockAdvisor(new MockMethodInterceptor(factory, dataStore, properties));
    }
    // 数据库 Mock 数据源条件注册
    @Bean
    @ConditionalOnProperty(name = "wv.mock.store.enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnBean(DataSource.class)
    @ConditionalOnMissingBean
    public MockDataStore mockDataStore(DataSource dataSource, MockProperties properties) {
        return new DatabaseMockDataStore(dataSource, properties);
    }
    // Dubbo Filter 条件注册
    @Bean
    @ConditionalOnClass(name = "org.apache.dubbo.rpc.Filter")
    @ConditionalOnProperty(name = "wv.mock.dubbo.enabled", havingValue = "true", matchIfMissing = true)
    public MockDubboFilter mockDubboFilter(MockObjectFactory factory, MockDataStore dataStore, MockProperties properties) {
        return new MockDubboFilter(factory, dataStore, properties);
    }
    private void registerBuiltinGenerators(MockDataGeneratorRegistry registry) {
        registry.register(new NameMockDataGenerator());
        registry.register(new IdCardNoMockDataGenerator());
        registry.register(new AddressMockDataGenerator());
        registry.register(new PhoneMockDataGenerator());
        registry.register(new EmailMockDataGenerator());
        registry.register(new DateMockDataGenerator());
        registry.register(new NumberMockDataGenerator());
        registry.register(new StringMockDataGenerator());
    }
}
```
**META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports** 中注册 `MockAutoConfiguration`。
---
### 4.8 Mock 数据源 SPI（关联任务：MOCK-008）
**类型**：SPI 接口 + 数据库实现  
**文件位置**：`wvframework-mock/src/main/java/.../mock/store/`  
**关联任务**：MOCK-008
**设计说明**：
支持从数据库读取预配置的 Mock 数据。典型场景：银行回调接口需要返回固定的"回调成功"Mock 数据，通过 `@Mock(value = "bank.callback.success")` 指定数据键，框架从数据库查询对应的 JSON 数据并反序列化返回。
| 接口/类 | 职责 |
|---------|------|
| `MockDataStore` | Mock 数据源 SPI 接口，定义 `getMockData(String key)` 方法 |
| `DatabaseMockDataStore` | 数据库实现，从 `wv_mock_data` 表读取 Mock 数据 |
**代码示例**：
```java
/**
 * Mock 数据源 SPI 接口
 * 文件位置：mock/store/MockDataStore.java
 *
 * 支持从不同来源获取预配置的 Mock 数据
 * 默认实现为数据库，业务方可自定义实现（如从 Redis、配置文件读取）
 */
public interface MockDataStore {
    /**
     * 根据数据键获取预配置的 Mock 数据
     * @param key 数据键（如 "bank.callback.success"）
     * @return Mock 数据的 JSON 字符串，未找到返回 null
     */
    String getMockData(String key);
}
```
```java
/**
 * 数据库 Mock 数据源实现
 * 文件位置：mock/store/DatabaseMockDataStore.java
 *
 * 核心逻辑：
 * 1. 从 wv_mock_data 表查询 mock_key 对应的 mock_data
 * 2. 支持缓存（简单内存缓存，避免每次查询数据库）
 * 3. 未找到返回 null，降级为 MockObjectFactory 自动生成
 */
public class DatabaseMockDataStore implements MockDataStore {
    private final JdbcTemplate jdbcTemplate;
    private final MockProperties properties;
    private final Map<String, String> cache = new ConcurrentHashMap<>();
    @Override
    public String getMockData(String key) {
        // 先查缓存
        String cached = cache.get(key);
        if (cached != null) {
            return cached;
        }
        // 查数据库
        String tableName = properties.getStore().getTable();
        String sql = "SELECT mock_data FROM " + tableName
                   + " WHERE mock_key = ? AND enabled = 1 LIMIT 1";
        List<String> results = jdbcTemplate.queryForList(sql, String.class, key);
        if (!results.isEmpty()) {
            String data = results.get(0);
            cache.put(key, data);
            return data;
        }
        return null;
    }
}
```
**使用示例**：
```java
// 银行回调 Controller：从数据库读取预配置的"回调成功"Mock 数据
@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    /**
     * 银行回调通知接口
     * value = "bank.callback.success" 对应 wv_mock_data 表中的 mock_key
     * 当请求到达时，框架从数据库查询该 key 对应的 JSON 数据直接返回
     */
    @Mock(value = "bank.callback.success")
    @PostMapping("/bank/notify")
    public Result<BankCallbacko-KRespDTO> onBankNotify(@RequestBody BankNotifyReqDTO req) {
        // 不执行业务逻辑，直接返回数据库中配置的 Mock 数据
        return paymentService.handleBankNotify(req);
    }
}
```
对应数据库表数据示例：
```sql
INSERT INTO wv_mock_data (mock_key, mock_data, description, enabled)
VALUES (
    'bank.callback.success',
    '{"code":"0000","msg":"success","data":{"transactionId":"TXN202607250001","staten-US":"SUCCESS","amount":10000.00}}',
    '银行回调成功Mock数据',
    1
);
```
