package com.github.walkvoid.wvframework.core.configuration;

import com.github.walkvoid.wvframework.core.models.SwaggerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author walkvoid
 * @version 1.0
 * @date 2025/2/26
 * @desc
 */

@Configuration
public class SwaggerAutoConfiguration {



    @Bean
    public Docket docket(SwaggerProperties swaggerProperties) {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage()))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(convertApiInfo(swaggerProperties.getApiInfo()));
    }


    public SwaggerProperties getSwaggerProperties(SwaggerProperties swaggerProperties, Environment environment){
        return new SwaggerProperties();
    }

    private static ApiInfo convertApiInfo(SwaggerProperties.ApiInfo apiInfo) {
        return new ApiInfoBuilder()
                .title(apiInfo.getTitle())
                .description(apiInfo.getDescription())
                .version(apiInfo.getVersion())
                .contact(new Contact(
                        apiInfo.getContact().getName(),
                        apiInfo.getContact().getUrl(),
                        apiInfo.getContact().getEmail()))
                .license(apiInfo.getLicense())
                .licenseUrl(apiInfo.getLicenseUrl())
                .build();
    }


}
