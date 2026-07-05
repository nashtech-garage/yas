FROM eclipse-temurin:25-jre-alpine
COPY target/webhook*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
# Trigger build and push webhook service - trigger build 1