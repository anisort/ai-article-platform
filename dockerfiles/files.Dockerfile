FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build
COPY files/pom.xml files/
COPY files/.mvn files/.mvn
COPY files/mvnw files/
COPY files/src files/src
WORKDIR /build/files
RUN chmod +x mvnw && ./mvnw -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /build/files/target/files-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
