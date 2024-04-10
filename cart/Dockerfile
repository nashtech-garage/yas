FROM eclipse-temurin:21-jre-alpine
ADD target/cart*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]