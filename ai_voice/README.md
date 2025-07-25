# AI-VoiceTherapy - An Automated Platform for Voice Rehabilitation Using Artificial Intelligence
A Spring Boot application that analyzes speech to detect speech disorders (stuttering, dysphasia) and offers personalized rehabilitation exercises.

## Features

- Audio file analysis to detect speech disorders
- Audio transcription via OpenAI Whisper
- Automatic detection of disorders such as stuttering and dysphasia
- Generation of personalized rehabilitation exercises
- User progress tracking

## Technologies Used

- Spring Boot 3.x
- JPA/Hibernate
- MySQL
- OpenAI Whisper API
- Maven

## Prerequisites

- Java 17+
- MySQL
- OpenAI API Key

## Installation

1. Clone the repository
2. Configure your MySQL database in application.properties
3. Add your OpenAI API key to application.properties
4. Run the application with Maven:

```bash
./mvnw spring-boot:run
```

## Project Structure

- `model/` - JPA entities (User, VoiceAnalysis, TherapyExercise)
- `repository/` - Data persistence interfaces
- `service/` - Business logic and integration with OpenAI
- `controller/` - REST API
- `config/` - Application configuration

## API Endpoints

### Users
- `POST /api/users/register` - Register a new user
- `POST /api/users/login` - Log in a user
- `GET /api/users/{userId}` - Retrieve user information
- `PUT /api/users/{userId}` - Update user information

### Voice Analytics
- `POST /api/analyses/submit` - Submit an audio file for analysis
- `GET /api/analyses/user/{userId}` - Retrieve all analyses for a user
- `GET /api/analyses/{analysisId}` - Retrieve a specific analysis

### Therapy Exercises
- `GET /api/exercises/user/{userId}` - Retrieve all of a user's exercises
- `PUT /api/exercises/{exerciseId}/complete` - Mark an exercise as completed

## Configuration

In the `application.properties` file, make sure to configure:

```properties
# Base de données
spring.datasource.url=jdbc:mysql://localhost:3306/ai_voice_therapy
spring.datasource.username=votre_utilisateur
spring.datasource.password=votre_mot_de_passe

# OpenAI API
openai.api.key=votre_cle_api_openai
```

## License

This project is licensed under the MIT License.
