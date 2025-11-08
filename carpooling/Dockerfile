# ==============================
# 🐳 Carpooling Microservice
# ==============================

# Step 1: Build the JAR (if using Maven)
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Step 2: Run the Spring Boot app
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Expose the port used in application.properties
EXPOSE 8086

# Start the app
ENTRYPOINT ["java", "-jar", "app.jar"]
