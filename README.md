# MicroserviceBackend
# 🚀 SpeedyGo - Microservice Backend

## 📘 Description du projet

**SpeedyGo** est une application web distribuée de transport et livraison qui connecte les **utilisateurs** avec des **chauffeurs professionnels**.  
Elle permet la **réservation de trajets**, la **livraison de colis**, la **gestion des paiements**, et l’**authentification sécurisée** à travers une architecture **microservices** moderne.

Ce projet est développé dans le cadre du module **Application Web Distribuée (5ème année ingénierie)**.

🧠 Description des composants
1️⃣ Eureka Server (8761)

Rôle : Découverte de services

Permet à chaque microservice de s’enregistrer dynamiquement et d’être détecté par les autres sans config statique.

Chaque microservice communique avec d’autres via leur nom logique plutôt que leur adresse IP.

2️⃣ Config Server (8888)

Rôle : Gestion centralisée de la configuration

Centralise les fichiers application.properties de tous les microservices.

Permet de changer les configurations sans redéployer les services.

3️⃣ API Gateway (8090)

Rôle : Point d’entrée unique

Reçoit toutes les requêtes venant du frontend Angular.

Route la requête vers le microservice correspondant (user, parcel, trip…).

Gère :

le CORS (pour l’accès depuis Angular),

le load balancing,

la sécurité (via JWT Keycloak).

4️⃣ Keycloak (8080)

Rôle : Authentification et gestion des rôles

Serveur d’authentification basé sur OAuth2 / OpenID Connect.

Centralise la gestion des utilisateurs et rôles :

ADMIN

DRIVER

USER

Fournit des tokens JWT consommés par les microservices sécurisés.

5️⃣ User Service (Symfony - 8084)

Gère les utilisateurs, profils, rôles, inscriptions et connexions.

Interagit avec Keycloak pour synchroniser les utilisateurs et rôles.

Stocke les données dans PostgreSQL.

6️⃣ Trip Service (Spring Boot - 8082)

Gère la création, la modification et le suivi des trajets.

Intègre Google Maps API pour l’optimisation des itinéraires et la distance.

Offre des fonctionnalités avancées :

suggestion automatique de trajets,

estimation du prix,

notifications au conducteur.

7️⃣ Parcel Service (Spring Boot - 8089)

Gère la livraison de colis :

ajout, suppression, mise à jour,

suivi de statut (pending, shipped, delivered),

calcul dynamique du prix selon poids/distance.

Connecté au service utilisateur pour identifier l’expéditeur et le destinataire.

Base de données : MySQL

🔐 Authentification avec Keycloak
⚙️ Configuration :

URL : http://localhost:8080

Realm : SpeedyGo

Clients :

user-service

trip-service

parcel-service

api-gateway

Rôles : ADMIN, USER, DRIVER

🔒 Principe :

L’utilisateur s’authentifie sur Keycloak.

Keycloak émet un token JWT.

Ce token est envoyé dans les requêtes HTTP (header Authorization: Bearer <token>).

Les microservices vérifient le token avant d’autoriser l’accès.

