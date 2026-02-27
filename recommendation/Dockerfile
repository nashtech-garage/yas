FROM eclipse-temurin:25-jre-alpine
COPY target/recommendation*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]