package com.github.walkvoid.wvframework.apidoc;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3 (springdoc + Knife4j) 自动配置。
 * 使用 swagger.* 配置项，与 Spring Boot 3 兼容。
 *
 * @author walkvoid
 * @version 1.0
 * @date 2025/2/26
 */
@Configuration
@EnableConfigurationProperties(Swagger2Properties.class)
public class Swagger2AutoConfiguration {

    @Bean
    public OpenAPI customOpenAPI(Swagger2Properties swagger2Properties) {
        Swagger2Properties.ApiInfo api = swagger2Properties != null ? swagger2Properties.getApiInfo() : null;
        if (api == null) {
            return new OpenAPI().info(new Info().title("API").version("1.0"));
        }
        Info info = new Info()
                .title(api.getTitle())
                .description(api.getDescription())
                .version(api.getVersion())
                .license(api.getLicense() != null ? new License().name(api.getLicense()).url(api.getLicenseUrl()) : null)
                .contact(api.getContact() != null
                        ? new Contact().name(api.getContact().getName()).url(api.getContact().getUrl()).email(api.getContact().getEmail())
                        : null);
        return new OpenAPI().info(info);
    }
}
