import com.google.protobuf.gradle.id

val liquibaseVersion by extra("4.31.1")

val javaVersion = "25"
val pgvVersion = "1.3.3"
val jsonwebtokenVersion = "0.12.6"

plugins {
    java
    id("org.springframework.boot") version "4.1.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.google.protobuf") version "0.9.6"
}

group = "org.springframework.grpc"
version = "1.0.0-SNAPSHOT"
description = "Spring gRPC Server Sample"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-grpc-server")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-json")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-liquibase")
    implementation("org.liquibase:liquibase-core:$liquibaseVersion")
    implementation("io.jsonwebtoken:jjwt-api:$jsonwebtokenVersion")
    implementation("build.buf.protoc-gen-validate:pgv-java-stub:$pgvVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-grpc-server-test")

    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jsonwebtokenVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jsonwebtokenVersion")

    compileOnly("org.projectlombok:lombok")

    annotationProcessor("org.projectlombok:lombok")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc"
    }
    plugins {
        id("javapgv") {
            artifact = "build.buf.protoc-gen-validate:protoc-gen-validate:$pgvVersion"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("javapgv") {
                    option("lang=java")
                }
            }
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}