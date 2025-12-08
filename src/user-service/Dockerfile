FROM gradle:8.7.0-jdk21 AS builder
WORKDIR /app
COPY settings.gradle.kts build.gradle.kts gradlew /app/
COPY gradle /app/gradle
RUN ./gradlew build --no-daemon --stacktrace -x test || true
COPY . /app/.
RUN ./gradlew clean build --no-daemon -x test

FROM amazoncorretto:21-al2-jdk
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar /app/user-service.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","user-service.jar"]