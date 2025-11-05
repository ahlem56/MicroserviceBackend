# ------------------------------------------------------------
# 🐳 SpeedyGo API Gateway Dockerfile (Maven build)
# ------------------------------------------------------------

# ====== STAGE 1: Build the app ======
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Build the jar
RUN mvn clean package -DskipTests

# ====== STAGE 2: Run the app ======
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the jar from the builder stage
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
