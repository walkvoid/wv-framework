dependencies {
    api(project(":wvframework-models"))
    implementation(libs.spring.boot.starter.validation)
    compileOnly(libs.spring.context)
    compileOnly(libs.spring.boot.starter.web)
    compileOnly(libs.spring.boot.starter)
    compileOnly(libs.spring.boot)
    implementation(libs.jackson.datatype.jsr310)
    implementation(libs.slf4j.api)
}
