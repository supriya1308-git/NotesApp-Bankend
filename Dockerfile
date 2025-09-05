FROM eclipse-temurin:17-jdk-alpine
COPY Notes-Backend/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]