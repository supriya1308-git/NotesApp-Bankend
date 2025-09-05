FROM maven:3.8.6-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn -f Notes-Backend/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/Notes-Backend/target/*.jar app.jar  # ← FIXED: Added = sign
EXPOSE 10000
ENTRYPOINT ["java","-jar","app.jar"]