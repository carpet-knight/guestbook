import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.5.6"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.spring") version "1.5.31"
}

group = "com.ernsthaagsman.guestbook"
version = System.getenv("BUILD_NUMBER") ?: "1.0-SNAPSHOT"


repositories {
    mavenCentral()
}


dependencies {
    implementation(platform("software.amazon.awssdk:bom:2.17.80"))
    implementation("software.amazon.awssdk:s3")
    implementation("software.amazon.awssdk:sqs")

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")

    implementation("org.postgresql:postgresql:42.3.1")
    implementation("com.google.cloud:google-cloud-storage:1.66.0")
    implementation("org.jetbrains:annotations:17.0.0")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    compileOnly("org.jetbrains:annotations:22.0.0")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.data:spring-data-jdbc")
    implementation("com.networknt:json-schema-validator:1.0.57")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

java.sourceCompatibility = JavaVersion.VERSION_11

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.create("teamcity") {
    dependsOn(tasks.test)
    dependsOn(tasks.bootJar)

    doFirst {
        println(" ##teamcity[publishArtifacts '${tasks.bootJar.get().archiveFile.get().asFile.absolutePath}'] ")
    }
}
