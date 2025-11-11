
# MicroserviceBackend
# 🚀 SpeedyGo - Microservice Backend

## 📘 Description du projet

**SpeedyGo** est une application web distribuée de transport et livraison qui connecte les **utilisateurs** avec des **chauffeurs professionnels**.  
Elle permet la **réservation de trajets**, la **livraison de colis**, la **gestion des paiements**, et l’**authentification sécurisée** à travers une architecture **microservices** moderne.


📈 Fonctionnalités principales

🔐 Authentification centralisée avec Keycloak

🚗 Réservation et gestion des trajets (Trip Service)

📦 Livraison de colis avec estimation de prix (Parcel Service)

💳 Paiements sécurisés 

👤 Gestion utilisateurs et rôles (User Service)

📍 Intégration de Google Maps (optimisation d’itinéraire)

💬 Notifications et messagerie 


🧠 . Keycloak – Serveur d’authentification (Port 8080)
🔸 Description :

Keycloak est le cœur de la sécurité du système.
Il gère :

la création des utilisateurs,

les rôles (Admin, Driver, User),

les clients (API Gateway, microservices),

🚪 . API Gateway – Point d’entrée unique (Port 8090)
🔸 Description :

Le Gateway fait office de filtre et de répartiteur :

reçoit toutes les requêtes du frontend,

vérifie le token JWT avec Keycloak,

redirige la requête vers le microservice approprié (via Eureka).

🧩 . Eureka Server – Service Discovery (Port 8761)
🔸 Description :

Gère l’enregistrement et la découverte des microservices.

Chaque microservice communique avec les autres via leurs noms logiques.

⚙️ . Config Server – Configuration centralisée (Port 8888)
🔸 Description :

Le Config Server centralise tous les fichiers application.properties des microservices.

🧾 . Sécurité inter-services

Tous les microservices communiquent via HTTP interne (Docker Network).

Le frontend est le seul accès externe via le Gateway (8090).

Les rôles et permissions sont vérifiés à chaque requête.

🧭 Ordre de démarrage recommandé

1️⃣ Lancer Keycloak
2️⃣ Lancer les bases de données (MySQL et PostgreSQL)
3️⃣ Démarrer Eureka Server
4️⃣ Démarrer Config Server
5️⃣ Lancer les microservices :

user-service

trip-service

parcel-service
6️⃣ Lancer API Gateway
7️⃣ Lancer le Frontend Angular (port 4200)
