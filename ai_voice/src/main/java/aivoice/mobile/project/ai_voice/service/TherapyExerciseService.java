package aivoice.mobile.project.ai_voice.service;

import aivoice.mobile.project.ai_voice.model.TherapyExercise;
import aivoice.mobile.project.ai_voice.model.User;
import aivoice.mobile.project.ai_voice.model.VoiceAnalysis;
import aivoice.mobile.project.ai_voice.repository.TherapyExerciseRepository;
import aivoice.mobile.project.ai_voice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TherapyExerciseService {

    private final TherapyExerciseRepository therapyExerciseRepository;
    private final UserRepository userRepository;
    
    /**
     * Génère des exercices de thérapie basés sur une analyse vocale
     * @param analysis L'analyse vocale
     * @return Liste des exercices générés
     */
    public List<TherapyExercise> generateExercisesForAnalysis(VoiceAnalysis analysis) {
        List<TherapyExercise> exercises = new ArrayList<>();
        
        // Générer des exercices en fonction du type de trouble détecté
        switch (analysis.getTroubleDetecte()) {
            case BEGAIEMENT:
                exercises.addAll(generateStutteringExercises(analysis.getUser()));
                break;
            case DYSPHASIE:
                exercises.addAll(generateDysphasiaExercises(analysis.getUser()));
                break;
            case DYSARTHRIE:
                exercises.addAll(generateDysarthriaExercises(analysis.getUser()));
                break;
            case APRAXIE:
                exercises.addAll(generateApraxiaExercises(analysis.getUser()));
                break;
            case AUTRE:
                exercises.addAll(generateGeneralExercises(analysis.getUser()));
                break;
            default:
                // Aucun exercice si aucun trouble n'est détecté
                break;
        }
        
        // Sauvegarder les exercices générés
        return therapyExerciseRepository.saveAll(exercises);
    }
    
    /**
     * Récupère tous les exercices d'un utilisateur
     * @param userId ID de l'utilisateur
     * @return Liste des exercices
     */
    public List<TherapyExercise> getUserExercises(Long userId) {
        return therapyExerciseRepository.findByUserId(userId);
    }
    
    /**
     * Récupère les exercices générés pour une analyse spécifique
     * @param analysis L'analyse vocale
     * @return Liste des exercices recommandés
     */
    public List<TherapyExercise> getExercisesForAnalysis(VoiceAnalysis analysis) {
        // Récupérer les exercices pour l'utilisateur et le type de trouble détecté
        List<TherapyExercise> userExercises = therapyExerciseRepository.findByUserId(analysis.getUser().getId());
        
        // Filtrer les exercices qui correspondent au type de trouble détecté dans l'analyse
        return userExercises.stream()
                .filter(exercise -> exercise.getTroubleCible() == analysis.getTroubleDetecte())
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Marque un exercice comme complété
     * @param exerciseId ID de l'exercice
     * @return L'exercice mis à jour
     */
    public TherapyExercise completeExercise(Long exerciseId) {
        TherapyExercise exercise = therapyExerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercice non trouvé"));
        
        exercise.setCompleted(true);
        exercise.setDateCompletion(LocalDateTime.now());
        
        return therapyExerciseRepository.save(exercise);
    }
    
    /**
     * Génère des exercices pour le bégaiement
     */
    private List<TherapyExercise> generateStutteringExercises(User user) {
        List<TherapyExercise> exercises = new ArrayList<>();
        
        // Exercice 1: Respiration contrôlée
        TherapyExercise exercise1 = new TherapyExercise();
        exercise1.setUser(user);
        exercise1.setTitre("Respiration contrôlée");
        exercise1.setDescription("Exercice de respiration pour réduire le bégaiement");
        exercise1.setInstructions("1. Asseyez-vous confortablement\n2. Inspirez lentement par le nez pendant 4 secondes\n3. Retenez votre respiration pendant 2 secondes\n4. Expirez lentement par la bouche pendant 6 secondes\n5. Répétez 10 fois");
        exercise1.setTroubleCible(VoiceAnalysis.SpeechDisorderType.BEGAIEMENT);
        exercise1.setDureeRecommandee(5);
        exercise1.setFrequenceRecommandee(7);
        exercises.add(exercise1);
        
        // Exercice 2: Parole rythmée
        TherapyExercise exercise2 = new TherapyExercise();
        exercise2.setUser(user);
        exercise2.setTitre("Parole rythmée");
        exercise2.setDescription("Utiliser un rythme pour fluidifier la parole");
        exercise2.setInstructions("1. Choisissez un texte court\n2. Lisez-le en tapant doucement du doigt à chaque syllabe\n3. Maintenez un rythme régulier\n4. Augmentez progressivement la vitesse tout en gardant la fluidité");
        exercise2.setTroubleCible(VoiceAnalysis.SpeechDisorderType.BEGAIEMENT);
        exercise2.setDureeRecommandee(10);
        exercise2.setFrequenceRecommandee(5);
        exercises.add(exercise2);
        
        return exercises;
    }
    
    /**
     * Génère des exercices pour la dysphasie
     */
    private List<TherapyExercise> generateDysphasiaExercises(User user) {
        List<TherapyExercise> exercises = new ArrayList<>();
        
        // Exercice 1: Construction de phrases
        TherapyExercise exercise1 = new TherapyExercise();
        exercise1.setUser(user);
        exercise1.setTitre("Construction de phrases");
        exercise1.setDescription("Améliorer la structure grammaticale");
        exercise1.setInstructions("1. Commencez par des phrases simples (sujet-verbe-complément)\n2. Décrivez des images simples avec des phrases complètes\n3. Augmentez progressivement la complexité des phrases");
        exercise1.setTroubleCible(VoiceAnalysis.SpeechDisorderType.DYSPHASIE);
        exercise1.setDureeRecommandee(15);
        exercise1.setFrequenceRecommandee(3);
        exercises.add(exercise1);
        
        // Exercice 2: Association mot-image
        TherapyExercise exercise2 = new TherapyExercise();
        exercise2.setUser(user);
        exercise2.setTitre("Association mot-image");
        exercise2.setDescription("Renforcer le vocabulaire et la dénomination");
        exercise2.setInstructions("1. Utilisez des cartes avec des images d'objets courants\n2. Nommez chaque objet\n3. Formez une phrase simple avec le mot\n4. Répétez régulièrement avec de nouveaux mots");
        exercise2.setTroubleCible(VoiceAnalysis.SpeechDisorderType.DYSPHASIE);
        exercise2.setDureeRecommandee(10);
        exercise2.setFrequenceRecommandee(4);
        exercises.add(exercise2);
        
        return exercises;
    }
    
    /**
     * Génère des exercices pour la dysarthrie
     */
    private List<TherapyExercise> generateDysarthriaExercises(User user) {
        List<TherapyExercise> exercises = new ArrayList<>();
        
        // Exercice 1: Articulation
        TherapyExercise exercise1 = new TherapyExercise();
        exercise1.setUser(user);
        exercise1.setTitre("Exercices d'articulation");
        exercise1.setDescription("Améliorer la précision articulatoire");
        exercise1.setInstructions("1. Pratiquez des mouvements exagérés des lèvres et de la langue\n2. Répétez des séries de syllabes (pa-ta-ka)\n3. Lisez à haute voix en exagérant chaque consonne");
        exercise1.setTroubleCible(VoiceAnalysis.SpeechDisorderType.DYSARTHRIE);
        exercise1.setDureeRecommandee(10);
        exercise1.setFrequenceRecommandee(7);
        exercises.add(exercise1);
        
        return exercises;
    }
    
    /**
     * Génère des exercices pour l'apraxie
     */
    private List<TherapyExercise> generateApraxiaExercises(User user) {
        List<TherapyExercise> exercises = new ArrayList<>();
        
        // Exercice 1: Séquences motrices
        TherapyExercise exercise1 = new TherapyExercise();
        exercise1.setUser(user);
        exercise1.setTitre("Séquences motrices orales");
        exercise1.setDescription("Améliorer la planification motrice de la parole");
        exercise1.setInstructions("1. Pratiquez des séquences de mouvements de la bouche (sourire, puis arrondir les lèvres)\n2. Répétez des séquences de sons de plus en plus complexes\n3. Utilisez des indices visuels pour faciliter la production");
        exercise1.setTroubleCible(VoiceAnalysis.SpeechDisorderType.APRAXIE);
        exercise1.setDureeRecommandee(15);
        exercise1.setFrequenceRecommandee(5);
        exercises.add(exercise1);
        
        return exercises;
    }
    
    /**
     * Génère des exercices généraux pour les troubles de la parole
     */
    private List<TherapyExercise> generateGeneralExercises(User user) {
        List<TherapyExercise> exercises = new ArrayList<>();
        
        // Exercice 1: Lecture à haute voix
        TherapyExercise exercise1 = new TherapyExercise();
        exercise1.setUser(user);
        exercise1.setTitre("Lecture à haute voix");
        exercise1.setDescription("Améliorer la fluidité et l'articulation");
        exercise1.setInstructions("1. Choisissez un texte de votre niveau\n2. Lisez-le à haute voix lentement\n3. Enregistrez-vous et écoutez\n4. Identifiez les points à améliorer");
        exercise1.setTroubleCible(VoiceAnalysis.SpeechDisorderType.AUTRE);
        exercise1.setDureeRecommandee(10);
        exercise1.setFrequenceRecommandee(3);
        exercises.add(exercise1);
        
        return exercises;
    }

    public Optional<TherapyExercise> getExerciseById(Long exerciseId) {
        return therapyExerciseRepository.findById(exerciseId);
    }
}