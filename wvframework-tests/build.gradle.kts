dependencies {
    implementation(project(":wvframework-utils"))
    testImplementation(libs.spring.boot.starter.test)
    compileOnly(libs.spring.context)
    compileOnly(libs.spring.boot.starter.web)
    compileOnly(libs.spring.boot.starter)
    compileOnly(libs.spring.boot)
    implementation(libs.jackson.datatype.jsr310)
    implementation(libs.slf4j.api)
    implementation(libs.spring.boot.starter.validation)
}
