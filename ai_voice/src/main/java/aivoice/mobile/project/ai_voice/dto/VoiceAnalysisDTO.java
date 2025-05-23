package aivoice.mobile.project.ai_voice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoiceAnalysisDTO {
    private Long id;
    private String transcription;
    private String troubleDetecte;
    private Double severityScore;
    private String analysisDetails;
    private Boolean needsTherapy;
    private Long userId;


}