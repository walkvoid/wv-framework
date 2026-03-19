dependencies {
    compileOnly(libs.spring.boot.starter)
    api(project(":wvframework-utils"))
    compileOnly(libs.springdoc.openapi.webmvc)
}
