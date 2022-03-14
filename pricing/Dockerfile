FROM openjdk:17-jdk-alpine
WORKDIR /opt/app
ARG JAR_FILE=target/pricing*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar"]