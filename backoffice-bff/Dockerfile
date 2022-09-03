FROM openjdk:17-jdk-alpine
RUN apk add --no-cache bash
COPY target/backoffice-bff-0.0.1-SNAPSHOT.jar backoffice-bff-0.0.1-SNAPSHOT.jar
COPY wait-for-it.sh wait-for-it.sh
RUN chmod +x wait-for-it.sh
ENTRYPOINT ["java","-jar","/backoffice-bff-0.0.1-SNAPSHOT.jar"]