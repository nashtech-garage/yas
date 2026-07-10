FROM eclipse-temurin:25-jre-alpine
COPY target/product*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
# test push docker image. - trigger build 1 hello