FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY . .
RUN apk add --no-cache maven
RUN mvn -f Notes-Backend/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/Notes-Backend/target/*.jar app.jar
EXPOSE 10000
ENTRYPOINT ["java","-jar","app.jar"]