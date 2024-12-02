plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.allopen") version "2.1.0"
    kotlin("plugin.jpa") version "2.1.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.7"
    id("io.quarkus")
    id("jacoco")
}

repositories {
    mavenCentral()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project
val kotestVersion: String by project
val jacksonVersion = "2.18.2"

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))

    // Core
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-config-yaml")

    // Kotlin
    implementation("io.quarkus:quarkus-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // REST
    implementation("io.quarkus:quarkus-resteasy-reactive")
    implementation("io.quarkus:quarkus-resteasy-reactive-jackson")
    implementation("io.quarkus:quarkus-hibernate-validator")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

    // Data
    implementation("io.quarkus:quarkus-jdbc-postgresql")
    implementation("io.quarkus:quarkus-hibernate-orm")
    implementation("io.quarkus:quarkus-hibernate-orm-panache-kotlin")
    implementation("io.quarkus:quarkus-hibernate-envers")

    // Auth
    implementation("io.quarkus:quarkus-oidc")
    implementation("com.auth0:auth0:2.15.0")

    // Monitoring
    implementation("io.quarkus:quarkus-smallrye-health")
    implementation("io.quarkus:quarkus-micrometer")
    implementation("io.quarkus:quarkus-micrometer-registry-prometheus")

    // Other
    implementation("io.quarkus:quarkus-mailer")
    implementation("io.quarkus:quarkus-cache")
    implementation("org.apache.commons:commons-lang3:3.17.0")

    // Tests
    testImplementation("io.github.serpro69:kotlin-faker:1.16.0")
    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.quarkus:quarkus-test-security")
    testImplementation("io.quarkus:quarkus-jacoco")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("io.quarkiverse.mockk:quarkus-junit5-mockk:3.0.0")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-json:$kotestVersion")
    testImplementation("io.kotest:kotest-property-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-framework-datatest:$kotestVersion")
}

group = "org.backstage"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

allOpen {
    annotation("jakarta.ws.rs.Path")
    annotation("jakarta.enterprise.context.ApplicationScoped")
    annotation("jakarta.persistence.Entity")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
    kotlinOptions.javaParameters = true
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)

    configure<JacocoTaskExtension> {
        excludeClassLoaders = listOf("*QuarkusClassLoader")
        setDestinationFile(layout.buildDirectory.file("jacoco/test.exec").get().asFile)
    }
}

tasks.detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom("$projectDir/detekt.yml")

    reports {
        md.required.set(true)
    }
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        csv.required.set(true)
    }
}
