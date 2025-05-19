package com.github.walkvoid.wvframework.apidoc;

import java.io.Serializable;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author walkvoid
 * @version 1.0
 * @date 2025/2/26
 * @desc 在项目中如果添加了下面的依赖，编译后，会在target/classes/META-INF下自动生成spring-configuration-metadata.json
 * <dependency>
 *    <groupId>org.springframework.boot</groupId>
 *    <artifactId>spring-boot-configuration-processor</artifactId>
 *    <optional>true</optional>
 * </dependency>
 */

@ConfigurationProperties(prefix = "swagger")
public class Swagger2Properties implements Serializable {

    /**
     * basePackage
     */
    private String basePackage;

    /**
     * apiInfo
     */
    private ApiInfo apiInfo;


    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public ApiInfo getApiInfo() {
        return apiInfo;
    }

    public void setApiInfo(ApiInfo apiInfo) {
        this.apiInfo = apiInfo;
    }

    public class ApiInfo implements Serializable {
        private String title;
        private String description;
        private String version;
        private String licenseUrl;
        private String license;
        private Contact contact;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getLicenseUrl() {
            return licenseUrl;
        }

        public void setLicenseUrl(String licenseUrl) {
            this.licenseUrl = licenseUrl;
        }

        public String getLicense() {
            return license;
        }

        public void setLicense(String license) {
            this.license = license;
        }

        public Contact getContact() {
            return contact;
        }

        public void setContact(Contact contact) {
            this.contact = contact;
        }

    }

    public class Contact implements Serializable {
        private String name;
        private String email;
        private String url;


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getUrl() {
            return url;
        }
        public void setUrl(String url) {
            this.url = url;
        }
    }



}
