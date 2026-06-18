FROM eclipse-temurin:25-jre-alpine
COPY target/cart*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

# Trigger build and push cart service
