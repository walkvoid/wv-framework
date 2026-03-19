import org.gradle.language.jvm.tasks.ProcessResources

dependencies {
    api(project(":wvframework-models"))
    annotationProcessor(libs.spring.boot.configuration.processor)
    implementation(libs.spring.boot.starter.validation)
    compileOnly(libs.spring.web)
    implementation(libs.dubbo.spring.boot.starter) {
        exclude(group = "org.apache.logging.log4j", module = "log4j-api")
        exclude(group = "org.springframework.boot", module = "spring-boot-starter")
    }
}

tasks.named<ProcessResources>("processResources") {
    val tokens = mapOf("revision" to project.version.toString())
    filesMatching("**/*.properties") { expand(tokens) }
    filesMatching("**/*.imports") { expand(tokens) }
    filesMatching("**/*.json") { expand(tokens) }
    filesMatching("**/*.factories") { expand(tokens) }
}
