import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    base
}

group = providers.gradleProperty("group").get()
version = providers.gradleProperty("version").get()

subprojects {
    val isBom = name == "wvframework-bom"
    
    if (!isBom) {
        apply(plugin = "java-library")
        apply(plugin = "maven-publish")

        group = rootProject.group
        version = rootProject.version

        extensions.configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(21))
            }
            withSourcesJar()
        }

        tasks.withType<JavaCompile>().configureEach {
            options.encoding = "UTF-8"
        }

        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
        }

        afterEvaluate {
            dependencies {
                // 引入 Spring Boot 和 Spring Cloud 的 BOM
                add("implementation", platform("org.springframework.boot:spring-boot-dependencies:3.2.5"))
                add("implementation", platform("org.springframework.cloud:spring-cloud-dependencies:2023.0.1"))
                // 使用 wvframework-bom 管理其他依赖版本
                add("implementation", platform(project(":wvframework-bom")))
                
                // 确保 annotationProcessor 也使用 BOM 管理版本
                add("annotationProcessor", platform("org.springframework.boot:spring-boot-dependencies:3.2.5"))
                add("annotationProcessor", platform(project(":wvframework-bom")))
            }
        }

        extensions.configure<PublishingExtension> {
            publications {
                create<MavenPublication>("mavenJava") {
                    from(components["java"])
                    groupId = project.group.toString()
                    artifactId = project.name
                    version = project.version.toString()
                }
            }
        }
    }
}