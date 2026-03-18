package com.github.walkvoid.wvframework.core.configuration;

import com.github.walkvoid.wvframework.core.models.SwaggerProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3 (springdoc) 自动配置，替代原 Springfox Swagger2。
 * 当 classpath 存在 springdoc 且配置启用时生效。
 *
 * @author walkvoid
 * @version 1.0
 * @date 2025/2/26
 */
@Configuration
@ConditionalOnClass(OpenAPI.class)
@EnableConfigurationProperties(SwaggerProperties.class)
public class SwaggerAutoConfiguration {

    @Bean
    public OpenAPI customOpenAPI(SwaggerProperties swaggerProperties) {
        SwaggerProperties.ApiInfo api = swaggerProperties.getApiInfo();
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
