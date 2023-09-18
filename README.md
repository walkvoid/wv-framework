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
|--pom.xml: 全局父pom，依赖管理
```
### 项目特点
- 干净的依赖
```text
和其他项目不同，wv-framework针对第三方的类库都使用<scope>provided</scope>,这代表wv-framework不会替你引入任何第三方类库，所有的
的三方类库都需要你的手动引入。经过长期的项目实践这是十分有益的，意味着你可以完全掌控你的项目依赖树。
```