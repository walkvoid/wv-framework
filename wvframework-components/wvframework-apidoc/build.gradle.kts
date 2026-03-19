dependencies {
    api(project(":wvframework-annotations"))
    compileOnly(libs.spring.boot.starter)
    implementation(project(":wvframework-utils"))
    annotationProcessor(libs.spring.boot.configuration.processor)
    implementation(libs.springdoc.openapi.webmvc)
    implementation(libs.knife4j.openapi3)
}
