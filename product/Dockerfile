FROM maven:3.8.6-eclipse-temurin-17-alpine as build
RUN mkdir -p /build
WORKDIR /build

COPY src/ src/
COPY pom.xml ./

RUN mvn -U clean package -DskipTests=true

FROM openjdk:17-jdk-alpine

WORKDIR /build

COPY --from=build /build/target/product-0.0.1-SNAPSHOT.jar ./product-0.0.1-SNAPSHOT.jar

# This line should be commented when debuging
ENTRYPOINT ["java", "-jar", "/build/product-0.0.1-SNAPSHOT.jar"]