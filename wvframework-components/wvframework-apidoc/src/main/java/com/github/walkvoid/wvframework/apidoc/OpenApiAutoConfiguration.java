package com.github.walkvoid.wvframework.apidoc;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * OpenAPI 3 (springdoc + Knife4j) 自动配置。
 *
 * <p>配置项前缀 {@code openapi.api-info.*}，示例：
 * <pre>
 * openapi.api-info.title=Zone API
 * openapi.api-info.version=1.0
 * </pre>
 *
 * @author walkvoid
 */
@AutoConfiguration
@EnableConfigurationProperties(OpenApiProperties.class)
public class OpenApiAutoConfiguration {

    @Bean
    public OpenAPI customOpenAPI(OpenApiProperties properties) {
        OpenApiProperties.ApiInfo api = properties.getApiInfo();
        Info info = new Info()
                .title(api.getTitle())
                .description(api.getDescription())
                .version(api.getVersion());
        OpenApiProperties.Contact c = api.getContact();
        if (c != null) {
            info.contact(new Contact().name(c.getName()).email(c.getEmail()).url(c.getUrl()));
        }
        return new OpenAPI().info(info);
    }
}
