FROM maven:3.9.6-eclipse-temurin-17 AS build

# Créer un répertoire de travail
WORKDIR /app

# Copier le fichier pom.xml et télécharger les dépendances
COPY pom.xml .

# Copier le code source du projet
COPY src ./src

# Compiler et packager l'application
RUN mvn clean package -DskipTests

# Étape 2 : Exécuter l’application avec un JDK léger
FROM eclipse-temurin:17-jdk-alpine

# Créer un répertoire pour l’app
WORKDIR /app

# Copier le JAR depuis l’étape de build
COPY --from=build /app/target/*.jar app.jar

# Exposer le port utilisé par Spring Boot (souvent 8080)
EXPOSE 8085

# Commande de démarrage
ENTRYPOINT ["java", "-jar", "app.jar"]
