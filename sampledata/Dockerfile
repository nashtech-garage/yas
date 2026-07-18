FROM eclipse-temurin:25-jre-alpine
COPY target/sampledata-1.0-SNAPSHOT.jar app.jar
COPY images /opt/yas/sampledata/images
ENTRYPOINT ["java", "-jar", "/app.jar"]
