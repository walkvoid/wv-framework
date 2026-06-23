package com.github.walkvoid.wvframework.apidoc;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * OpenAPI 3 文档配置属性。
 *
 * @author walkvoid
 */
@ConfigurationProperties(prefix = "openapi")
public class OpenApiProperties implements Serializable {

    /** API 基本信息 */
    private ApiInfo apiInfo = new ApiInfo();

    public ApiInfo getApiInfo() { return apiInfo; }
    public void setApiInfo(ApiInfo apiInfo) { this.apiInfo = apiInfo; }

    public static class ApiInfo implements Serializable {
        private String title = "API";
        private String description;
        private String version = "1.0";
        private Contact contact;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public Contact getContact() { return contact; }
        public void setContact(Contact contact) { this.contact = contact; }
    }

    public static class Contact implements Serializable {
        private String name;
        private String email;
        private String url;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
    }
}
