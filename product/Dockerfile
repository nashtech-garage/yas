FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
ADD target/product-0.0.1-SNAPSHOT.jar product-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/product-0.0.1-SNAPSHOT.jar"]