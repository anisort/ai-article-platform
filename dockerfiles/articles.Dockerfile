FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build
COPY articles/pom.xml articles/
COPY articles/.mvn articles/.mvn
COPY articles/mvnw articles/
COPY articles/src articles/src
WORKDIR /build/articles
RUN chmod +x mvnw && ./mvnw -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /build/articles/target/articles-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 7070
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
