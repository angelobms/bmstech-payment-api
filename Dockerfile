FROM eclipse-temurin:21-jdk-jammy

COPY ./target/*.jar /app.jar

EXPOSE 8080

CMD ["java", "-jar", "/app.jar"]
