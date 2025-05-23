package aivoice.mobile.project.ai_voice.controller;

import aivoice.mobile.project.ai_voice.exception.VoiceTherapyException;
import aivoice.mobile.project.ai_voice.model.TherapyExercise;
import aivoice.mobile.project.ai_voice.model.VoiceAnalysis;
import aivoice.mobile.project.ai_voice.service.AnalysisService;
import aivoice.mobile.project.ai_voice.service.TherapyExerciseService;
import aivoice.mobile.project.ai_voice.service.VoiceAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/analyses")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class VoiceAnalysisController {

    private final VoiceAnalysisService voiceAnalysisService;
    private final TherapyExerciseService therapyExerciseService;
    @Autowired
    private AnalysisService analysisService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> analyzeVoice(
            @RequestHeader("Authorization") String token,
            @RequestPart("userId") String userIdStr,
            @RequestPart("audioFile") MultipartFile audioFile) {

        try {
            Long userId = Long.parseLong(userIdStr);

            // Vérifications préalables du fichier
            if (audioFile.isEmpty()) {
                return ResponseEntity.badRequest().body("Fichier audio vide");
            }

            // Vérification du type MIME
            String contentType = audioFile.getContentType();
            if (contentType == null || !contentType.startsWith("audio/")) {
                return ResponseEntity.badRequest().body("Le fichier doit être un fichier audio");
            }

            // Vérification de l'extension
            String filename = audioFile.getOriginalFilename();
            if (filename == null || !filename.matches(".*\\.(mp3|wav|m4a|flac|ogg|webm)$")) {
                return ResponseEntity.badRequest().body("Format de fichier non supporté");
            }

            // Log des informations du fichier
            log.info("Traitement du fichier: {}, Taille: {}, Type: {}",
                    filename, audioFile.getSize(), contentType);

            VoiceAnalysis analysis = voiceAnalysisService.analyzeVoice(userId, audioFile);
            return ResponseEntity.ok(analysis);

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("ID utilisateur invalide");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            log.error("Erreur E/S", e);
            return ResponseEntity.internalServerError().body("Erreur de traitement audio");
        } catch (Exception e) {
            log.error("Erreur inattendue", e);
            return ResponseEntity.internalServerError().body("Erreur serveur");
        }
    }

    /**
     * Soumet un fichier audio pour analyse et détection de troubles du langage
     *
     * @param userId ID de l'utilisateur
     * @param audioFile Fichier audio à analyser
     * @return L'analyse vocale créée avec les résultats de détection

        HERE
    }*/

    /**
     * Récupère toutes les analyses d'un utilisateur
     *
     * @param userId ID de l'utilisateur
     * @return Liste des analyses vocales
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<VoiceAnalysis>> getUserAnalyses(@PathVariable Long userId) {
        List<VoiceAnalysis> analyses = voiceAnalysisService.getUserAnalyses(userId);
        return ResponseEntity.ok(analyses);
    }

    /**
     * Récupère une analyse spécifique
     *
     * @param analysisId ID de l'analyse
     * @return L'analyse vocale demandée
     */
    @GetMapping("/{analysisId}")
    public ResponseEntity<VoiceAnalysis> getAnalysisById(@PathVariable Long analysisId) {
        try {
            VoiceAnalysis analysis = voiceAnalysisService.getAnalysisById(analysisId);
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            throw new VoiceTherapyException("Analyse non trouvée", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Récupère les exercices recommandés pour une analyse spécifique
     *
     * @param analysisId ID de l'analyse
     * @return Liste des exercices de thérapie recommandés
     */
    @GetMapping("/{analysisId}/exercises")
    public ResponseEntity<List<TherapyExercise>> getRecommendedExercises(@PathVariable Long analysisId) {
        try {
            VoiceAnalysis analysis = voiceAnalysisService.getAnalysisById(analysisId);
            List<TherapyExercise> exercises = therapyExerciseService.getExercisesForAnalysis(analysis);
            return ResponseEntity.ok(exercises);
        } catch (Exception e) {
            throw new VoiceTherapyException("Impossible de récupérer les exercices recommandés", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Supprime une analyse vocale
     *
     * @param analysisId ID de l'analyse à supprimer
     * @return Réponse vide avec statut 204 (No Content)
     */
    @DeleteMapping("/{analysisId}")
    public ResponseEntity<Void> deleteAnalysis(@PathVariable Long analysisId) {
        try {
            voiceAnalysisService.deleteAnalysis(analysisId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new VoiceTherapyException("Impossible de supprimer l'analyse", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}