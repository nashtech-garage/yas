FROM eclipse-temurin:21-jre-alpine
COPY target/webhook*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

