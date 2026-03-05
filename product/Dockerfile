FROM eclipse-temurin:25-jre-alpine
COPY target/product*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]