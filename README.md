# AI-VoiceTherapy
*Plateforme Automatisée de Rééducation Vocale Utilisant l'Intelligence Artificielle*

## Vue d'ensemble
AI-VoiceTherapy est une application mobile qui exploite le modèle Whisper d'OpenAI pour détecter les troubles de la parole et fournir des exercices thérapeutiques personnalisés. La plateforme vise à rendre l'orthophonie plus accessible grâce à l'analyse automatisée et aux programmes de rééducation sur mesure.

## Fonctionnalités Principales
* **Analyse Automatique de la Parole** : Détection en temps réel des troubles de la parole grâce à l'IA
* **Thérapie Personnalisée** : Exercices adaptés basés sur les conditions détectées
* **Suivi des Progrès** : Surveillance complète de l'évolution de la rééducation
* **Intégration Professionnelle** : Outils pour les orthophonistes et les professionnels de santé

## Conditions Prises en Charge
* Bégaiement
* Dysphasie
* Dysarthrie
* Apraxie de la parole

## Stack Technologique

### Application Mobile (Android)
* Java avec Android SDK
* Interface utilisateur Material Design
* Retrofit pour la communication API
* Enregistrement et traitement audio

### Services Backend
* Framework Spring Boot
* Base de données MySQL
* Authentification JWT
* Intégration API OpenAI Whisper

### Composants IA
* OpenAI Whisper pour la reconnaissance vocale
* Algorithmes d'analyse de motifs
* Système de notation de gravité (échelle 0-10)
* Génération automatique d'exercices

## Architecture
L'application suit une architecture à trois niveaux :
* **Couche de Présentation** : Interface mobile Android
* **Logique Métier** : API REST Spring Boot
* **Couche de Données** : Base de données MySQL avec JPA/Hibernate

## Exigences d'Installation

### Prérequis Système
* Android 5.0+ (API 21+)
* Java JDK 8+
* MySQL 8.0+
* Connexion Internet pour le traitement IA

### Configuration
1. Cloner le dépôt
2. Configurer la base de données MySQL
3. Définir les identifiants API OpenAI
4. Déployer le backend Spring Boot
5. Compiler l'application Android

## Flux d'Utilisation
1. **Inscription** : Créer un compte utilisateur
2. **Enregistrement** : Capturer un échantillon vocal (10-30 secondes)
3. **Analyse** : Traitement automatique via l'API Whisper
4. **Résultats** : Visualiser le diagnostic et le score de gravité
5. **Thérapie** : Accéder aux exercices personnalisés

## Exercices Thérapeutiques

### Catégories d'Exercices
* **Construction de Phrases** : Amélioration de la grammaire et de la syntaxe
* **Association Mot-Image** : Enrichissement du vocabulaire
* **Entraînement à l'Articulation** : Exercices de prononciation
* **Exercices Prosodiques** : Travail sur le rythme et l'intonation

### Suivi des Progrès
* Suivi de la completion des sessions
* Analyses de performance
* Analyse des tendances de gravité
* Rapports professionnels

## Limitations
* Nécessite une validation professionnelle pour un usage clinique
* Qualité audio dépendante de l'environnement
* Connexion Internet requise pour l'analyse
* Supporte actuellement uniquement le français

## Équipe de Développement
**Nissrine Lachguer** - Étudiante en Génie informatique et réseau, EMSI Marrakech  
Contact : nisrinelachguer37@gmail.com

**Ourda Azizi** - Étudiante en Génie informatique et réseaux, EMSI Marrakech  
Contact : Ourdaazizi2@gmail.com

*École Marocaine des Sciences de l'Ingénieur (EMSI), Marrakech, Maroc*

## Développement Futur
* Support multi-langues (Arabe, Anglais)
* Application iOS
* Capacités de traitement hors ligne
* Adaptation pédiatrique
* Fonctionnalités d'analyse en temps réel

## Licence
Licence MIT - Voir le fichier LICENSE pour plus de détails

## Dépôt
https://github.com/NisrineLachguer/AI-VoiceTherapy

*AI-VoiceTherapy : Rendre l'orthophonie accessible grâce à l'intelligence artificielle*
