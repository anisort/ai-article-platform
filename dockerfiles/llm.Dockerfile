FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build
COPY llm/pom.xml llm/
COPY llm/.mvn llm/.mvn
COPY llm/mvnw llm/
COPY llm/src llm/src
WORKDIR /build/llm
RUN chmod +x mvnw && ./mvnw -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /build/llm/target/llm-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
