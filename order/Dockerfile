FROM eclipse-temurin:21-jre-alpine

COPY target/order*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]