package aivoice.mobile.project.ai_voice.repository;

import aivoice.mobile.project.ai_voice.model.VoiceAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoiceAnalysisRepository extends JpaRepository<VoiceAnalysis, Long> {
    List<VoiceAnalysis> findByUser_Id(Long userId);

}