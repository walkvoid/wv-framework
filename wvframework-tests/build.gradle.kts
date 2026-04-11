dependencies {
    implementation(project(":wvframework-utils"))
    implementation("org.springframework.boot:spring-boot-starter-test:3.2.5")
    compileOnly("org.springframework:spring-context:6.1.6")
    compileOnly("org.springframework.boot:spring-boot-starter-web:3.2.5")
    compileOnly("org.springframework.boot:spring-boot-starter:3.2.5")
    compileOnly("org.springframework.boot:spring-boot:3.2.5")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
    implementation("org.slf4j:slf4j-api:2.0.11")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.2.5")
}