FROM eclipse-temurin:21-jre-alpine

ADD target/order*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]