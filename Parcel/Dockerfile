FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copier le JAR déjà compilé depuis ton projet local
COPY target/parcel-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8089

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
