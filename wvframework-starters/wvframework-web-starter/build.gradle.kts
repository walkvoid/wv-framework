import org.gradle.language.jvm.tasks.ProcessResources

dependencies {
    api(project(":wvframework-validation"))
    api(project(":wvframework-excel"))
}

tasks.named<ProcessResources>("processResources") {
    val tokens = mapOf("revision" to project.version.toString())
    filesMatching("**/*.properties") { expand(tokens) }
    filesMatching("**/*.imports") { expand(tokens) }
    filesMatching("**/*.json") { expand(tokens) }
    filesMatching("**/*.factories") { expand(tokens) }
}
