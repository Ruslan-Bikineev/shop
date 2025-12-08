import com.google.protobuf.gradle.id

val grpcVersion by extra("1.77.0")
val protobufVersion by extra("4.33.0")
val liquibaseVersion by extra("4.31.1")
val springGrpcVersion by extra("1.0.0-SNAPSHOT")

val javaVersion = "21"
val pgvVersion = "1.2.1"
val mapstructVersion = "1.6.3"
val jsonwebtokenVersion = "0.12.6"
val nettyTransportVersion = "4.1.103.Final:linux-x86_64"

plugins {
    java
    id("org.springframework.boot") version "4.0.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.google.protobuf") version "0.9.5"
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
    maven {
        url = uri("https://repo.spring.io/milestone")
    }
    maven {
        url = uri("https://repo.spring.io/snapshot")
    }
    maven {
        url = uri("https://buf.build/gen/maven")
    }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:4.0.0")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-json")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.grpc:spring-grpc-spring-boot-starter:$springGrpcVersion")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-liquibase")
    implementation("org.projectlombok:lombok")
    implementation("org.liquibase:liquibase-core:$liquibaseVersion")
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    implementation("io.jsonwebtoken:jjwt-api:$jsonwebtokenVersion")
    implementation("build.buf.protoc-gen-validate:pgv-java-stub:$pgvVersion")

    testImplementation("org.springframework.grpc:spring-grpc-test:$springGrpcVersion")
    testImplementation("io.grpc:grpc-testing:$grpcVersion")
    testImplementation("io.grpc:grpc-inprocess:$grpcVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-oauth2-client")

    runtimeOnly("io.netty:netty-transport-native-epoll:$nettyTransportVersion")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jsonwebtokenVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jsonwebtokenVersion")

    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
    annotationProcessor("org.projectlombok:lombok")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
        id("javapgv") {
            artifact = "build.buf.protoc-gen-validate:protoc-gen-validate:$pgvVersion"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
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

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "com.google.protobuf") {
            useVersion("$protobufVersion")
            because("Align all protobuf dependencies to match protoc 4.x")
        }
        if (requested.group == "io.grpc") {
            useVersion("$grpcVersion")
        }
    }
}