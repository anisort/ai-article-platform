FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build
COPY auth/pom.xml auth/
COPY auth/.mvn auth/.mvn
COPY auth/mvnw auth/
COPY auth/src auth/src
WORKDIR /build/auth
RUN chmod +x mvnw && ./mvnw -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /build/auth/target/auth-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
