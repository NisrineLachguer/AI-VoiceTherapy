package aivoice.mobile.project.ai_voice.controller;

import aivoice.mobile.project.ai_voice.dto.TherapyExerciseDTO;
import aivoice.mobile.project.ai_voice.model.TherapyExercise;
import aivoice.mobile.project.ai_voice.service.TherapyExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TherapyExerciseController {

    private final TherapyExerciseService therapyExerciseService;
    
    /**
     * Récupère tous les exercices d'un utilisateur
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TherapyExerciseDTO>> getUserExercises(@PathVariable Long userId) {
        // Utiliser le service au lieu du repository directement
        List<TherapyExercise> exercises = therapyExerciseService.getUserExercises(userId);
        List<TherapyExerciseDTO> dtos = exercises.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    private TherapyExerciseDTO convertToDTO(TherapyExercise exercise) {
        TherapyExerciseDTO dto = new TherapyExerciseDTO();
        dto.setId(exercise.getId());
        dto.setTitre(exercise.getTitre());
        dto.setDescription(exercise.getDescription());
        dto.setInstructions(exercise.getInstructions());
        dto.setTroubleCible(String.valueOf(exercise.getTroubleCible()));
        dto.setDureeRecommandee(exercise.getDureeRecommandee());
        dto.setFrequenceRecommandee(exercise.getFrequenceRecommandee());
        dto.setCompleted(exercise.getCompleted());
        return dto;
    }
    
    /**
     * Marque un exercice comme complété
     */
    @PutMapping("/{exerciseId}/complete")
    public ResponseEntity<TherapyExercise> completeExercise(@PathVariable Long exerciseId) {
        try {
            TherapyExercise exercise = therapyExerciseService.completeExercise(exerciseId);
            return ResponseEntity.ok(exercise);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/{exerciseId}")
    public ResponseEntity<TherapyExerciseDTO> getExerciseById(@PathVariable Long exerciseId) {
        TherapyExercise exercise = therapyExerciseService.getExerciseById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercice non trouvé"));

        return ResponseEntity.ok(convertToDTO(exercise));
    }
}