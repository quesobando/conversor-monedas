plugins {
    java
    id("org.springframework.boot") version "3.3.4"
    id("io.gatling.gradle") version "3.15.1.2"
    id("jacoco")
    id("org.sonarqube") version "5.1.0.4882"
}

group = "com.conversor"
version = "1.0.0"
description = "Backend del conversor de monedas (Spring Boot + Neon + Frankfurter API)"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES))

    // Web (REST controllers)
    implementation("org.springframework.boot:spring-boot-starter-web")

    // WebClient para consumir Frankfurter API
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // JPA + Postgres (Neon)
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")

    // Validaciones
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Lombok 
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    // Tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// ------------------------------------------------------------
// Config de Gatling. El plugin crea el source set "gatling"
// automáticamente: src/gatling/java, src/gatling/resources.
// ------------------------------------------------------------
gatling {
    includeMainOutput = false
    includeTestOutput = false
}

// El plugin de Gatling compila su propio source set con un JVM
tasks.withType<JavaCompile> {
    options.release.set(21)
}

sonar {
    properties {
        property("sonar.projectKey", "leniaso_conversor-monedas")
        property("sonar.organization", "leniaso")   // verifica en SonarCloud
        property("sonar.projectName", "conversor-monedas")
        property("sonar.host.url", "https://sonarcloud.io")

        // Ruta del reporte XML de JaCoCo (relativa al módulo backend)
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "build/reports/jacoco/test/jacocoTestReport.xml"
        )

        // Excluidos solo de cobertura
        property(
            "sonar.coverage.exclusions",
            "**/config/**,**/dto/**,**/model/**,**/*Application.java"
        )

        // Excluidos de todo el análisis
        property(
            "sonar.exclusions",
            "bin/**,src/gatling/**,**/generated/**,**/build/**"
        )
    }
}

tasks.named("sonar") {
    dependsOn(tasks.jacocoTestReport)
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(false)
    }
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}