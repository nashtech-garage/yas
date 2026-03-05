FROM eclipse-temurin:25-jre-alpine
COPY target/inventory*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]