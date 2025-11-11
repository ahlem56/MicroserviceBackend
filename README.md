# 🚗 SpeedyGo Microservices — Carpooling System

## 🧠 Overview

**SpeedyGo** is a microservices-based application designed to manage a smart transportation ecosystem — integrating **carpooling**, **trip management**, **user authentication**, and **centralized configuration**.  
It follows a **Spring Cloud architecture** with centralized service discovery, API gateway routing, and distributed security via **Keycloak**.

This README focuses on the **Carpooling microservice**, its integration with other modules, and the overall system architecture.

---

## 🧩 Microservices Architecture

The SpeedyGo backend is built with **Spring Boot (v3.3.5)** and orchestrated via **Docker Compose**.  
It includes the following services:

| Service | Description | Tech Stack | Port |
|----------|--------------|------------|------|
| 🧭 `config-server` | Centralized configuration management for all services | Spring Cloud Config | 8888 |
| 🧩 `eureka-server` | Service discovery for microservices | Spring Cloud Netflix Eureka | 8761 |
| 🌐 `api-gateway` | Routes and secures API traffic | Spring Cloud Gateway, OAuth2, Keycloak | 8090 |
| 👤 `speedygo-user-service` | Manages users & authentication data | Symfony (PHP), PostgreSQL | 8084 |
| 🚗 `trip-service` | Handles trip creation & scheduling | Spring Boot | 8082 |
| 🚙 `carpooling-service` | Core microservice for managing carpool rides | Spring Boot + MySQL | 8086 |
| 🔑 `keycloak` | Identity & access management (realms, roles, JWTs) | Keycloak | 8080 |
| 🗄️ `phpMyAdmin` | Web UI for MySQL | phpMyAdmin | 8088 |

All services communicate via **Eureka Service Discovery** and share configuration via **Config Server**.  
Security is handled through **JWT tokens** issued by Keycloak and validated at the Gateway level.

---

## 🚙 Carpooling Microservice

### 🧱 Responsibilities
- Manage **carpool rides** created by drivers  
- Handle **passenger ride requests**  
- Communicate with the **User Service** for user identity (driver/passenger)  
- Register and discover through **Eureka**  
- Secure endpoints via **Keycloak roles**

### 🗃️ Database
- Uses **MySQL 8** (Dockerized)
- Schema: `carpooling_db`
- Entities:
  - `Carpool` (driverId, destination, availableSeats, price, suggestions)
  - `Request` (passengerId, requestedDestination, carpool)

### 📡 Endpoints

| Method | Endpoint | Description | Roles |
|---------|-----------|--------------|--------|
| `GET` | `/carpools` | Get all carpools | USER, DRIVER, ADMIN |
| `GET` | `/carpools/destination/{destination}` | Search carpools by destination | USER, DRIVER, ADMIN |
| `POST` | `/carpools` | Add a new carpool | DRIVER, ADMIN |
| `DELETE` | `/carpools/{id}` | Delete a carpool | DRIVER, ADMIN |
| `GET` | `/requests` | List all requests | USER, DRIVER, ADMIN |
| `POST` | `/requests` | Add a ride request | USER, DRIVER, ADMIN |

---

## 🧰 Technologies Used
- **Java 21**
- **Spring Boot 3.3.5**
- **Spring Cloud Gateway / Eureka / Config**
- **Spring Security + OAuth2**
- **MySQL / JPA / Hibernate**
- **Docker Compose**
- **Lombok / Jakarta / Jackson**

---

## 🧩 Integration Flow

```plaintext
[Angular Frontend]
        │
        ▼
[API Gateway 8090]  ←→  [Keycloak 8080 for JWT Validation]
        │
        ├──► [Carpooling Service 8086] ←→ [MySQL 3307]
        ├──► [Trip Service 8082]
        ├──► [User Service 8084] ←→ [PostgreSQL 5432]
        └──► [Config Server 8888] & [Eureka 8761]
