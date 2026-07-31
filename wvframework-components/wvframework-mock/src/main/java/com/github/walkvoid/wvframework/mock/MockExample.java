package com.github.walkvoid.wvframework.mock;

import com.github.walkvoid.wvframework.mock.annotation.*;
/**
 * Mock 使用示例
 * 
 * <p>展示如何在业务代码中使用 @Mock 注解</p>
 *
 * @author walkvoid
 */
public class MockExample {

    // ==================== Controller 示例 ====================
    
    /**
     * 示例1：类级 Mock - 该类所有方法都启用 Mock
     */
    @Mock(count = 5, delay = 500)
    public static class UserDTO {
        @MockName
        private String name;

        @MockIdCardNo
        private String idCardNo;

        @MockPhone
        private String phone;

        @MockEmail
        private String email;

        @MockAddress(level = MockAddress.Level.FULL)
        private String address;

        @MockNumber(min = 18, max = 60)
        private Integer age;

        @MockNumber(min = 1000, max = 100000, decimal = true, decimals = 2)
        private Double salary;
    }

    /**
     * 示例2：方法级 Mock - 更细粒度控制
     */
    // @RestController
    // @RequestMapping("/api/user")
    // public static class UserController {
    //
    //     /**
    //      * 从数据库获取预配置的 Mock 数据
    //      * value = "bank.callback.success" 对应数据库 wv_mock_data 表的 mock_key
    //      */
    //     @Mock(value = "bank.callback.success")
    //     @PostMapping("/create")
    //     public Result<UserDTO> createUser(@RequestBody UserDTO user) {
    //         // 不执行业务逻辑，直接返回数据库中配置的 Mock 数据
    //         return userService.create(user);
    //     }
    //
    //     /**
    //      * 自动生成 Mock 响应
    //      * 返回 5 条随机的 UserDTO 数据
    //      */
    //     @Mock(count = 5, mockRequest = false)
    //     @GetMapping("/list")
    //     public Result<List<UserDTO>> listUsers() {
    //         return userService.list();
    //     }
    //
    //     /**
    //      * 仅 Mock 响应体，不 Mock 请求体
    //      */
    //     @Mock(count = 3, mockRequest = false, mockResponse = true)
    //     @GetMapping("/search")
    //     public Result<List<UserDTO>> searchUsers(String keyword) {
    //         return userService.search(keyword);
    //     }
    // }


    // ==================== DTO 字段级注解示例 ====================

    /**
     * 示例3：复杂 DTO 使用各种字段级 Mock 注解
     */
    public static class OrderDTO {
        @MockString(length = "16-20", charset = "alphanumeric")
        private String orderNo;

        @MockName
        private String userName;

        @MockPhone(type = MockPhone.Type.MOBILE)
        private String contactPhone;

        @MockEmail(lang = "FIXED", fixedValue = "noreply@company.com")
        private String email;

        @MockAddress(level = MockAddress.Level.DETAIL)
        private String shippingAddress;

        @MockDate(format = "yyyy-MM-dd HH:mm:ss", from = "2024-01-01", to = "2024-12-31", withTime = true)
        private String orderTime;

        @MockNumber(min = 1, max = 100)
        private Integer quantity;

        @MockNumber(min = 100, max = 10000, decimal = true, decimals = 2)
        private Double totalAmount;

        @MockString(values = {"PENDING", "PAID", "SHIPPED", "COMPLETED", "CANCELLED"})
        private String status;
    }


    // ==================== 多语言支持示例 ====================

    /**
     * 示例4：多语言支持
     * 
     * <p>通过 lang 属性控制生成的数据风格：
     * <ul>
     *   <li>zh-CN - 中文</li>
     *   <li>en-US - 英文</li>
     *   <li>ja-JP - 日文</li>
     *   <li>ko-KR - 韩文</li>
     *   <li>AUTO - 自动获取当前环境的多语言配置</li>
     * </ul>
     */
    public static class I18nUserDTO {
        @MockName(lang = "zh-CN")
        private String chineseName;

        @MockName(lang = "en-US")
        private String englishName;

        @MockAddress(lang = "zh-CN")
        private String chineseAddress;

        @MockAddress(lang = "en-US")
        private String englishAddress;

        // 使用 AUTO 自动获取当前语言环境
        @MockName(lang = "AUTO")
        private String autoName;
    }


    // ==================== Feign Client 示例 ====================

    /**
     * 示例5：Feign Client 使用 Mock
     */
    // @FeignClient(name = "user-service", url = "${feign.user.url}")
    // public static interface UserFeignClient {
    //
    //     /**
    //      * Feign 调用也支持 Mock
    //      */
    //     @Mock(value = "feign.user.get")
    //     @GetMapping("/user/{id}")
    //     UserDTO getUser(@PathVariable("id") Long id);
    //
    //     @Mock(count = 10)
    //     @GetMapping("/user/list")
    //     List<UserDTO> listUsers();
    // }


    // ==================== 配置说明 ====================

    /**
     * 配置示例 (application.yml):
     * <pre>
     * wv:
     *   mock:
     *     enabled: true  # 全局开关
     *     controller:
     *       enabled: true
     *       default-delay: 0  # 默认延迟(毫秒)
     *       default-count: 3  # 默认Mock数据条数
     *     feign:
     *       enabled: true
     *     http-exchange:
     *       enabled: true
     *     dubbo:
     *       enabled: true
     *     store:
     *       enabled: true  # 启用数据库数据源
     *       table: wv_mock_data  # 数据表名
     *       cache-expire-seconds: 300
     *     i18n:
     *       default-lang: zh-CN
     *       accept-language-enabled: true  # 从HTTP头获取语言
     * </pre>
     *
     * 数据库表结构 (MySQL):
     * <pre>
     * CREATE TABLE wv_mock_data (
     *     id BIGINT PRIMARY KEY AUTO_INCREMENT,
     *     mock_key VARCHAR(128) NOT NULL COMMENT '数据键',
     *     mock_data TEXT NOT NULL COMMENT 'JSON格式的Mock数据',
     *     description VARCHAR(256) COMMENT '描述',
     *     enabled TINYINT DEFAULT 1 COMMENT '是否启用',
     *     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     *     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
     *     UNIQUE KEY uk_mock_key (mock_key)
     * ) COMMENT='Mock数据表';
     *
     * INSERT INTO wv_mock_data (mock_key, mock_data, description, enabled)
     * VALUES (
     *     'bank.callback.success',
     *     ''{"code":"0000","msg":"success","data":{"transactionId":"TXN202607250001","status":"SUCCESS","amount":10000.00}}'',
     *     '银行回调成功Mock数据',
     *     1
     * );
     * </pre>
     */
    public static void main(String[] args) {
        System.out.println("Mock 使用示例代码");
    }
}
