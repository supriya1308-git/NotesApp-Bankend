FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY . .
RUN apk add --no-cache maven
RUN mvn -f Notes-Backend/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/Notes-Backend/target/*.jar app.jar

# Do NOT hardcode a port
EXPOSE 8080  

# Use Render's PORT variable
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=$PORT"]
