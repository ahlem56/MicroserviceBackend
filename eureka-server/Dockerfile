# ==============================
# 🧭 SpeedyGo Eureka Server
# ==============================

# Step 1: Build the JAR
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Step 2: Run the app
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
