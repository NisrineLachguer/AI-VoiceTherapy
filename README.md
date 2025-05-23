# AI-VoiceTherapy

*Automated Voice Rehabilitation Platform Using Artificial Intelligence*

## Overview

AI-VoiceTherapy is a mobile application that leverages OpenAI's Whisper model to detect speech disorders and provide personalized therapeutic exercises. The platform aims to make speech therapy more accessible through automated analysis and tailored rehabilitation programs.

## Core Features

- **Automated Speech Analysis**: Real-time detection of speech disorders using AI
- **Personalized Therapy**: Customized exercises based on detected conditions
- **Progress Tracking**: Comprehensive monitoring of rehabilitation progress
- **Professional Integration**: Tools for speech therapists and healthcare providers

## Supported Conditions

- Stuttering
- Dysphasia
- Dysarthria
- Apraxia of Speech

## Technology Stack

### Mobile Application (Android)
- Java with Android SDK
- Material Design UI
- Retrofit for API communication
- Audio recording and processing

### Backend Services
- Spring Boot framework
- MySQL database
- JWT authentication
- OpenAI Whisper API integration

### AI Components
- OpenAI Whisper for speech recognition
- Pattern analysis algorithms
- Severity scoring system (0-10 scale)
- Automated exercise generation

## Architecture

The application follows a three-tier architecture:
- **Presentation Layer**: Android mobile interface
- **Business Logic**: Spring Boot REST API
- **Data Layer**: MySQL database with JPA/Hibernate

## Installation Requirements

### System Prerequisites
- Android 5.0+ (API 21+)
- Java JDK 8+
- MySQL 8.0+
- Internet connection for AI processing

### Configuration
1. Clone repository
2. Configure MySQL database
3. Set OpenAI API credentials
4. Deploy Spring Boot backend
5. Build Android application

## Usage Workflow

1. **Registration**: Create user account
2. **Recording**: Capture voice sample (10-30 seconds)
3. **Analysis**: Automatic processing via Whisper API
4. **Results**: View diagnosis and severity score
5. **Therapy**: Access personalized exercises

## Therapeutic Exercises

### Exercise Categories
- **Sentence Construction**: Grammar and syntax improvement
- **Word-Image Association**: Vocabulary enhancement
- **Articulation Training**: Pronunciation exercises
- **Prosodic Exercises**: Rhythm and intonation work

### Progress Monitoring
- Session completion tracking
- Performance analytics
- Severity trend analysis
- Professional reporting

## Limitations

- Requires professional validation for clinical use
- Audio quality dependent on environment
- Internet connection required for analysis
- Currently supports French language only

## Development Team

**Nissrine Lachguer** - Software Engineering Student, EMSI Marrakesh  
Contact: nisrinelachguer37@gmail.com

**Ourda Azizi** - Software Engineering Student, EMSI Marrakesh  
Contact: Ourdaazizi2@gmail.com

*École Marocaine des Sciences de l'Ingénieur (EMSI), Marrakesh, Morocco*

## Future Development

- Multi-language support (Arabic, Berber)
- iOS application
- Offline processing capabilities
- Pediatric adaptation
- Real-time analysis features

## License

MIT License - See LICENSE file for details

## Repository

[https://github.com/NisrineLachguer/AI-VoiceTherapy](https://github.com/NisrineLachguer/AI-VoiceTherapy)

---

*AI-VoiceTherapy: Making speech therapy accessible through artificial intelligence*
