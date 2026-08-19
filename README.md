# Gestion des Affectations des Employés

Une application moderne et élégante pour la gestion du personnel, des lieux et des affectations.
Construite avec **Spring Boot (Backend API REST)** et **JavaFX (Client Lourd Desktop)** selon une architecture MVC, sécurisée par **JWT**.

---

##  Aperçu

Ce projet a été conçu avec un souci du détail UI/UX (Thème Bleu Nuit / Minimaliste).
* **Backend :** API RESTful avec Spring Boot 3, Spring Security, Hibernate, Soft Delete.
* **Frontend :** Application JavaFX MVC, Responsive, Requêtes HTTP asynchrones, mapping Gson.

##  Prérequis

- **Java 17** (JDK 17 ou supérieur)
- **Maven** (3.8+)
- **MySQL** (Serveur actif sur le port 3306)

## Installation et Démarrage

### 1. Base de données
Assurez-vous d'avoir une base de données MySQL vide nommée `gestion_affectations`.
```sql
CREATE DATABASE gestion_affectations;
```

### 2. Démarrer le Backend (API REST)
Le backend va générer automatiquement les tables et le compte administrateur par défaut.

```bash
cd backend
mvn clean install
mvn spring-boot:run
```
> Le serveur backend démarrera sur **http://localhost:8080**

### 3. Démarrer le Frontend (JavaFX)
Ouvrez un nouveau terminal et lancez l'application cliente :

```bash
cd frontend
mvn clean compile
mvn javafx:run
```

##  Authentification par défaut

Lors du premier lancement, un utilisateur administrateur est automatiquement créé.
* **Identifiant :** `admin`
* **Mot de passe :** `admin`

*(Assurez-vous que le backend est bien lancé avant de vous connecter).*

##  Fonctionnalités Principales

- **Authentification Sécurisée (JWT) :** Protection complète des endpoints API.
- **Gestion des Employés (CRUD) :** Ajout, modification, et suppression logique (Soft Delete).
- **Gestion des Lieux (CRUD) :** Administration des sites géographiques et de leur capacité.
- **Affectations :** Système pour lier un employé à un lieu avec dates de début et de fin.
- **Interface Fluide :** Chargements asynchrones, fenêtres dynamiques et feedbacks visuels.
