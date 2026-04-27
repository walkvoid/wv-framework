dependencies {
    compileOnly(libs.spring.boot.starter)
    compileOnly(libs.spring.boot.starter.security)
    api(project(":wvframework-utils"))
    compileOnly(libs.springdoc.openapi.webmvc)
}