FROM maven:3.9.9-amazoncorretto-21 AS builder
WORKDIR /app
COPY pom.xml /app/.
RUN mvn -B dependency:resolve
COPY ../.. /app/.
RUN mvn clean package -Dmaven.test.skip=true

FROM amazoncorretto:21-al2-jdk
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/shop-service.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","shop-service.jar"]
