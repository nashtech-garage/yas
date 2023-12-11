FROM openjdk:21-jdk-alpine
RUN apk add --no-cache bash
COPY wait-for-it.sh wait-for-it.sh
RUN chmod +x wait-for-it.sh
COPY target/storefront-bff-0.0.1-SNAPSHOT.jar storefront-bff-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/storefront-bff-0.0.1-SNAPSHOT.jar"]