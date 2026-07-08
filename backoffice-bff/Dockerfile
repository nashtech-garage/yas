FROM eclipse-temurin:25-jre-alpine
RUN apk add --no-cache bash
COPY target/*[0-9].jar app.jar
COPY wait-for-it.sh wait-for-it.sh
RUN chmod +x wait-for-it.sh
ENTRYPOINT ["java","-jar","/app.jar"]