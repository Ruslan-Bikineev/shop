FROM eclipse-temurin:21-jdk-noble AS builder
WORKDIR /app
COPY pom.xml mvnw /app/
COPY .mvn /app/.mvn
RUN ./mvnw -B dependency:resolve
COPY src /app/src
RUN ./mvnw clean package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre-noble
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/shop-service.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","shop-service.jar"]
