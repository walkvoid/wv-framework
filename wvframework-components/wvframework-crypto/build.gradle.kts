dependencies {
    implementation(libs.bcprov)
    implementation(libs.bcpkix)
    compileOnly(libs.spring.boot.autoconfigure)
    annotationProcessor(libs.spring.boot.configuration.processor)
    testImplementation(libs.junit.jupiter)
}
