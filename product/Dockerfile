FROM eclipse-temurin:21-jre-alpine
ADD target/product*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]