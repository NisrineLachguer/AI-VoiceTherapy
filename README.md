# AI-VoiceTherapy
*An Automated Platform for Voice Rehabilitation Using Artificial Intelligence*

https://github.com/user-attachments/assets/2b55d93f-16c1-45b3-8d1b-d1676180f91d

## Overview
AI-VoiceTherapy is a mobile app that leverages OpenAI's Whisper model to detect speech disorders and provide personalized therapeutic exercises. The platform aims to make speech therapy more accessible through automated analysis and tailored rehabilitation programs.

## Main Features
* **Automatic Speech Analysis** :Real-time detection of speech disorders using AI
* **Personalized Therapy** : Adapted exercises based on detected conditions
* **Progress Monitoring** : Complete monitoring of rehabilitation progress
* **Professional Integration** : Tools for speech-language pathologists and healthcare professionals

## Conditions Supported
* Stuttering
* Dysphasia
* Dysarthria
* Apraxia of speech

## Technology Stack

### Mobile Application (Android)
* Java with Android SDK
* Material Design user interface
* API communication retrofit
* Audio recording and processing

### Backend Services
* Spring Boot Framework
* MySQL Database
* JWT Authentication
* OpenAI Whisper API Integration

### AI Components
* OpenAI Whisper for speech recognition
* Pattern analysis algorithms
* Severity scoring system (0-10 scale)
* Automatic exercise generation

## Architecture
The application follows a three-tier architecture:
* **Presentation Layer**: Android mobile interface
* **Business Logic**: Spring Boot REST API
* **Data Layer**: MySQL database with JPA/Hibernate

  <img width="764" height="456" alt="Image" src="https://github.com/user-attachments/assets/e7c06e4d-69c0-4905-a8cb-2b0980b6f478" />

## Installation Requirements

### System Requirements
* Android 5.0+ (API 21+)
* Java JDK 8+
* MySQL 8.0+
* Internet connection for AI processing

### Configuration
1. Clone the repository
2. Configure the MySQL database
3. Set the OpenAI API credentials
4. Deploy the Spring Boot backend
5. Compile the Android application

## User Flow
1. **Registration**: Create a user account
2. **Recording**: Capture a voice sample (10-30 seconds)
3. **Analysis**: Automatic processing via the Whisper API
4. **Results**: View the diagnosis and severity score
5. **Therapy**: Access personalized exercises

## Therapeutic Exercises

### Exercise Categories
* **Sentence Building**: Improve grammar and syntax
* **Word-Picture Matching**: Expand your vocabulary
* **Articulation Training**: Pronunciation exercises
* **Prosodic Exercises**: Work on rhythm and intonation

### Progress Tracking
* Session Completion Tracking
* Performance Analytics
* Severity Trend Analysis
* Professional Reports

## Limitations
* Requires professional validation for clinical use
* Audio quality is dependent on the environment
* Internet connection required for analysis
* Currently supports French only

## Development Team
**Nisrine Lachguer** - Student in Computer and Network Engineering, EMSI Marrakech
Contact: nisrinelachguer37@gmail.com

**Ourda Azizi** - Student in Computer and Network Engineering, EMSI Marrakech
Contact: Ourdaazizi2@gmail.com

*Moroccan School of Engineering Sciences (EMSI), Marrakech, Morocco*

## Future Development
* Multi-language support (Arabic, English)
* iOS app
* Offline processing capabilities
* Pediatric adaptation
* Real-time analysis features

## License
MIT License - See the LICENSE file for details

## Repository
https://github.com/NisrineLachguer/AI-VoiceTherapy

*AI-VoiceTherapy: Making speech therapy accessible through artificial intelligence*
