package aivoice.mobile.project.ai_voice.service;

import aivoice.mobile.project.ai_voice.exception.VoiceTherapyException;
import aivoice.mobile.project.ai_voice.model.User;
import aivoice.mobile.project.ai_voice.model.VoiceAnalysis;
import aivoice.mobile.project.ai_voice.repository.UserRepository;
import aivoice.mobile.project.ai_voice.repository.VoiceAnalysisRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoiceAnalysisService {

    private final VoiceAnalysisRepository voiceAnalysisRepository;
    private final UserRepository userRepository;
    
    @org.springframework.context.annotation.Lazy
    private final OpenAIService openAIService;
    
    @org.springframework.context.annotation.Lazy
    private final TherapyExerciseService therapyExerciseService;
    
    private final Path audioStorageLocation = Paths.get("uploads/audio");
    
    /**
     * Initialise le répertoire de stockage des fichiers audio
     */
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(audioStorageLocation);
        } catch (IOException e) {
            log.error("Impossible de créer le répertoire de stockage des fichiers audio", e);
        }
    }
    
    /**
     * Analyse un fichier audio pour un utilisateur donné
     * @param userId ID de l'utilisateur
     * @param audioFile Fichier audio à analyser
     * @return L'analyse vocale créée
     */
    public VoiceAnalysis   analyzeVoice(Long userId, MultipartFile audioFile) throws IOException {
        // Vérifier que l'utilisateur existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        // Sauvegarder le fichier audio
        String filename = UUID.randomUUID() + "_" + audioFile.getOriginalFilename();
        Path targetLocation = audioStorageLocation.resolve(filename);
        Files.copy(audioFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        
        // Transcrire l'audio avec OpenAI Whisper
        String transcription = openAIService.transcribeAudio(audioFile);
        
        // Analyser la transcription pour détecter des troubles
        Map<String, Object> analysisResults = openAIService.analyzeTranscription(transcription);
        
        // Créer et sauvegarder l'analyse vocale
        VoiceAnalysis analysis = new VoiceAnalysis();
        analysis.setUser(user);
        analysis.setAudioFilePath(targetLocation.toString());
        analysis.setDateAnalyse(LocalDateTime.now());
        analysis.setTranscription(transcription);
        analysis.setTroubleDetecte((VoiceAnalysis.SpeechDisorderType) analysisResults.get("disorderType"));
        analysis.setAnalysisDetails((String) analysisResults.get("details"));
        analysis.setSeverityScore((Double) analysisResults.get("severityScore"));
        analysis.setNeedsTherapy((Boolean) analysisResults.get("needsTherapy"));
        
        VoiceAnalysis savedAnalysis = voiceAnalysisRepository.save(analysis);
        
        // Si un trouble est détecté et nécessite une thérapie, générer des exercices
        if (savedAnalysis.getNeedsTherapy()) {
            therapyExerciseService.generateExercisesForAnalysis(savedAnalysis);
        }
        
        return savedAnalysis;
    }
    
    /**
     * Récupère toutes les analyses vocales d'un utilisateur
     * @param userId ID de l'utilisateur
     * @return Liste des analyses vocales
     */
    public List<VoiceAnalysis> getUserAnalyses(Long userId) {
        return voiceAnalysisRepository.findByUser_Id(userId);
    }
    
    /**
     * Récupère une analyse vocale par son ID
     * @param analysisId ID de l'analyse
     * @return L'analyse vocale
     */
    public VoiceAnalysis getAnalysisById(Long analysisId) {
        return voiceAnalysisRepository.findById(analysisId)
                .orElseThrow(() -> new VoiceTherapyException("Analyse vocale non trouvée", HttpStatus.NOT_FOUND));
    }
    
    /**
     * Supprime une analyse vocale par son ID
     * @param analysisId ID de l'analyse à supprimer
     */
    public void deleteAnalysis(Long analysisId) {
        VoiceAnalysis analysis = getAnalysisById(analysisId);
        
        // Supprimer le fichier audio associé si nécessaire
        try {
            Path audioPath = Paths.get(analysis.getAudioFilePath());
            if (Files.exists(audioPath)) {
                Files.delete(audioPath);
            }
        } catch (IOException e) {
            log.warn("Impossible de supprimer le fichier audio associé à l'analyse {}", analysisId, e);
        }
        
        // Supprimer l'analyse de la base de données
        voiceAnalysisRepository.delete(analysis);
    }
}