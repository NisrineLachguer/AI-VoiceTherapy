# AI-VoiceTherapy - Suivi des Troubles de la Parole et Rééducation avec IA

Une application Spring Boot qui analyse la voix pour détecter des troubles du langage (bégaiement, dysphasie) et propose des exercices de rééducation personnalisés.

## Fonctionnalités

- Analyse de fichiers audio pour détecter des troubles de la parole
- Transcription audio via OpenAI Whisper
- Détection automatique de troubles comme le bégaiement et la dysphasie
- Génération d'exercices de rééducation personnalisés
- Suivi des progrès des utilisateurs

## Technologies utilisées

- Spring Boot 3.x
- JPA/Hibernate
- MySQL
- OpenAI Whisper API
- Maven

## Prérequis

- Java 17+
- MySQL
- Clé API OpenAI

## Installation

1. Clonez le dépôt
2. Configurez votre base de données MySQL dans `application.properties`
3. Ajoutez votre clé API OpenAI dans `application.properties`
4. Exécutez l'application avec Maven:

```bash
./mvnw spring-boot:run
```

## Structure du projet

- `model/` - Entités JPA (User, VoiceAnalysis, TherapyExercise)
- `repository/` - Interfaces de persistance des données
- `service/` - Logique métier et intégration avec OpenAI
- `controller/` - API REST
- `config/` - Configuration de l'application

## API Endpoints

### Utilisateurs
- `POST /api/users/register` - Inscription d'un nouvel utilisateur
- `POST /api/users/login` - Connexion d'un utilisateur
- `GET /api/users/{userId}` - Récupérer les informations d'un utilisateur
- `PUT /api/users/{userId}` - Mettre à jour les informations d'un utilisateur

### Analyses vocales
- `POST /api/analyses/submit` - Soumettre un fichier audio pour analyse
- `GET /api/analyses/user/{userId}` - Récupérer toutes les analyses d'un utilisateur
- `GET /api/analyses/{analysisId}` - Récupérer une analyse spécifique

### Exercices de thérapie
- `GET /api/exercises/user/{userId}` - Récupérer tous les exercices d'un utilisateur
- `PUT /api/exercises/{exerciseId}/complete` - Marquer un exercice comme complété

## Configuration

Dans le fichier `application.properties`, assurez-vous de configurer :

```properties
# Base de données
spring.datasource.url=jdbc:mysql://localhost:3306/ai_voice_therapy
spring.datasource.username=votre_utilisateur
spring.datasource.password=votre_mot_de_passe

# OpenAI API
openai.api.key=votre_cle_api_openai
```

## Licence

Ce projet est sous licence MIT.