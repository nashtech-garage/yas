FROM eclipse-temurin:25-jre-alpine

COPY target/*[0-9].jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]