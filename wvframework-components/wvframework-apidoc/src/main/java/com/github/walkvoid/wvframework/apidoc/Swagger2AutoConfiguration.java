package com.github.walkvoid.wvframework.apidoc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author walkvoid
 * @version 1.0
 * @date 2025/2/26
 * @desc DocumentationType.SWAGGER_2 autoConfiguration class
 */

//@EnableOpenApi
@EnableSwagger2
@Configuration
public class Swagger2AutoConfiguration {



    @Bean
    public Docket docket(Swagger2Properties swagger2Properties) {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(swagger2Properties.getBasePackage()))
                .paths(PathSelectors.any())
                .build()
                .enable(true)
                .apiInfo(convertApiInfo(swagger2Properties.getApiInfo()));
    }


    @Bean
    public Swagger2Properties getSwaggerProperties(Swagger2Properties swaggerProperties, Environment environment){
        return new Swagger2Properties();
    }

    private static ApiInfo convertApiInfo(Swagger2Properties.ApiInfo apiInfo) {
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
