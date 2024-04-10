FROM eclipse-temurin:21-jre-alpine
ADD target/payment*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]