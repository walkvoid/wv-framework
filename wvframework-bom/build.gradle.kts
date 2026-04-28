plugins {
    id("java-platform")
    id("maven-publish")
}

group = rootProject.group
version = rootProject.version

javaPlatform {
    allowDependencies()
}

publishing {
    publications {
        create<MavenPublication>("mavenBom") {
            from(components["javaPlatform"])
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
        }
    }
}

// 版本定义
val springVersion = "6.1.6"
val springBootVersion = "3.2.5"
val springCloudVersion = "2023.0.1"
val jacksonVersion = "2.15.2"
val springDocVersion = "2.3.0"
val knife4jVersion = "4.5.0"
val dubboVersion = "3.3.0"
val mybatisVersion = "3.0.3"
val mybatisPlusVersion = "3.5.5"
val feignVersion = "12.3"
val bouncycastleVersion = "1.70"
val junitVersion = "5.10.2"
val lombokVersion = "1.18.30"

dependencies {
    api(platform("org.springframework.boot:spring-boot-dependencies:${springBootVersion}"))
    
    constraints {
        api("com.github.walkvoid.wvframework:wvframework-annotations:${project.version}")
        api("com.github.walkvoid.wvframework:wvframework-models:${project.version}")
        api("com.github.walkvoid.wvframework:wvframework-utils:${project.version}")
        api("com.github.walkvoid.wvframework:wvframework-json:${project.version}")
        api("com.github.walkvoid.wvframework:wvframework-cache:${project.version}")
        api("com.github.walkvoid.wvframework:wvframework-excel:${project.version}")
        api("com.github.walkvoid.wvframework:wvframework-apidoc:${project.version}")
        api("com.github.walkvoid.wvframework:wvframework-validation-starter:${project.version}")
        
        api("org.springdoc:springdoc-openapi-starter-webmvc-ui:${springDocVersion}")
        api("com.github.xiaoymin:knife4j-openapi3-jakarta-spring-boot-starter:${knife4jVersion}")
        api("org.apache.dubbo:dubbo-spring-boot-starter:${dubboVersion}")
        api("org.mybatis:mybatis:${mybatisVersion}")
        api("com.baomidou:mybatis-plus-spring-boot3-starter:${mybatisPlusVersion}")
        api("io.github.openfeign:feign-okhttp:${feignVersion}")
        api("org.bouncycastle:bcprov-jdk15on:${bouncycastleVersion}")
        api("org.bouncycastle:bcpkix-jdk15on:${bouncycastleVersion}")
        api("org.junit.jupiter:junit-jupiter:${junitVersion}")
    }
}