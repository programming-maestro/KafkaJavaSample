# Use Eclipse Temurin JDK 25 image (or your preferred JDK image)
FROM eclipse-temurin:25-jdk-jammy

WORKDIR /app

# copy the fat jar produced by maven-shade
COPY target/kafka-trading-backend-0.1.0.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
