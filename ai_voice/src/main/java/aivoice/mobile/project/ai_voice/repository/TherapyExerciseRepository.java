package aivoice.mobile.project.ai_voice.repository;

import aivoice.mobile.project.ai_voice.model.TherapyExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TherapyExerciseRepository extends JpaRepository<TherapyExercise, Long> {
    
    List<TherapyExercise> findByUserId(Long userId);
    
    List<TherapyExercise> findByUserIdAndCompleted(Long userId, Boolean completed);
}