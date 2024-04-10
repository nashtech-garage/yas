FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache bash
ADD target/search*.jar app.jar
COPY wait-for-it.sh wait-for-it.sh
RUN chmod +x wait-for-it.sh
ENTRYPOINT ["java", "-jar", "/app.jar"]
