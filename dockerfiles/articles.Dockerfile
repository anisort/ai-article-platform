FROM eclipse-temurin:21-jdk
ARG JAR_FILE=target/*.jar
COPY articles/target/articles-0.0.1-SNAPSHOT.jar application.jar
CMD apt-get update -y
ENTRYPOINT ["java", "-jar", "/application.jar"]