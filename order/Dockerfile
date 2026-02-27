FROM eclipse-temurin:25-jre-alpine

COPY target/order*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]