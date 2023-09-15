# wv-framework
让java程序员写更少的代码；Let Java programmers write less code;

```text
|--wvframework-annotations: 注解的定义，不涉及到具体的逻辑
|--wvframework-components:
    |--wvframework-cache: 缓存组件
    |--wvframework-core: 核心组件,一般是无法定义具体归属的功能
    |--wvframework-excel: excel操作组件
    |--wvframework-json: json组件
    |--wvframework-lombok: lombok扩展组件
    |--wvframework-mock: mock数据组件
    |--wvframework-dao: dao(数据访问对象)层的一些组件，例如mybatisplus的一些处理
    |--wvframework-validation: 参数校验的一些组件
|--wvframework-models: 模型的定义，没有具体的业务逻辑
|--wvframework-starters:
    |--wvframework-web-starter: web starter
|--wvframework-utils: 工具类
```