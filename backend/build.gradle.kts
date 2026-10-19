plugins {
    java
    id("org.springframework.boot") version "3.3.4"
    id("io.gatling.gradle") version "3.15.1.2"
    id("jacoco")
    id("org.sonarqube") version "7.3.0.8198"
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

sourceSets {
    create("acceptanceTest")
}

dependencies {
    "acceptanceTestImplementation"(platform("org.junit:junit-bom:6.0.3"))
    "acceptanceTestImplementation"("io.karatelabs:karate-junit6:2.1.2")
    "acceptanceTestImplementation"("org.junit.jupiter:junit-jupiter")
    "acceptanceTestRuntimeOnly"("org.junit.platform:junit-platform-launcher")
}

tasks.register<Test>("acceptanceTest") {
    description = "Pruebas de aceptación con Karate contra la API levantada"
    group = "verification"

    testClassesDirs = sourceSets["acceptanceTest"].output.classesDirs
    classpath = sourceSets["acceptanceTest"].runtimeClasspath
    useJUnitPlatform()

    // URL de la API bajo prueba: -DbaseUrl=..., variable BASE_URL o localhost:8080
    systemProperty(
        "baseUrl",
        System.getProperty("baseUrl") ?: System.getenv("BASE_URL") ?: "http://localhost:8080"
    )

    // Depende de un entorno externo: nunca se da por "up-to-date"
    outputs.upToDateWhen { false }

    testLogging {
        events("passed", "failed", "skipped")
        showStandardStreams = true
    }
}

sonar {
    properties {
        property("sonar.projectKey", "quesobando_conversor-monedas")
        property("sonar.organization", "quesobando")
        property("sonar.projectName", "conversor-monedas")
        property("sonar.host.url", "https://sonarcloud.io")

        // Ruta del reporte XML de JaCoCo (relativa al módulo backend)
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "build/reports/jacoco/test/jacocoTestReport.xml"
        )
        
        // Excluidos solo de cobertura: DTOs sin lógica y la clase que solo tiene main()
        property(
            "sonar.coverage.exclusions",
            "**/dto/**,**/ConversorMonedasApplication.java"
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
    toolVersion = "0.8.12"   // 0.8.11+ soporta Java 21
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)   // el reporte se genera solo después de los tests
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)    // este es el que lee SonarCloud
        html.required.set(true)   // para verlo tú localmente
        csv.required.set(false)
    }
}

// Versiones fijadas en gradle.lockfile (junto con gradle/verification-metadata.xml)
dependencyLocking {
    lockAllConfigurations()
}