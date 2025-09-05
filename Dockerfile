FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests
EXPOSE 10000
ENTRYPOINT ["java","-jar","target/*.jar"]