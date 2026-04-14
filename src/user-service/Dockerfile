FROM eclipse-temurin:21-jdk-noble AS builder
WORKDIR /app
COPY settings.gradle.kts build.gradle.kts gradlew /app/
COPY gradle /app/gradle
RUN ./gradlew build --no-daemon --stacktrace -x test || true
COPY src /app/src
RUN ./gradlew clean build --no-daemon -x test

FROM eclipse-temurin:21-jre-noble
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar /app/user-service.jar
RUN curl -L -o /usr/local/bin/grpcurl.tar.gz https://github.com/fullstorydev/grpcurl/releases/latest/download/grpcurl_1.9.3_linux_x86_64.tar.gz \
    && tar -xzf /usr/local/bin/grpcurl.tar.gz -C /usr/local/bin \
    && chmod +x /usr/local/bin/grpcurl \
    && rm -rf /usr/local/bin/grpcurl.tar.gz
EXPOSE 8080
ENTRYPOINT ["java","-jar","user-service.jar"]