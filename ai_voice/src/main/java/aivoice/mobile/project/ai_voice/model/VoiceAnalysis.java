package aivoice.mobile.project.ai_voice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "voice_analyses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoiceAnalysis {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference // Prevents infinite recursion
    private User user;
    
    @Column(nullable = false)
    private String audioFilePath;
    
    @Column(nullable = false)
    private LocalDateTime dateAnalyse = LocalDateTime.now();
    
    @Column(columnDefinition = "TEXT")
    private String transcription;
    
    @Column
    @Enumerated(EnumType.STRING)
    private SpeechDisorderType troubleDetecte;
    
    @Column(columnDefinition = "TEXT")
    private String analysisDetails;
    
    @Column
    private Double severityScore; // Score de sévérité du trouble (0-10)
    
    @Column
    private Boolean needsTherapy = false;

    public void setUserId(Long userId) {
        if (this.user == null) {
            this.user = new User();
        }
        this.user.setId(userId);
    }

    // Enum pour les types de troubles de la parole
    public enum SpeechDisorderType {
        BEGAIEMENT,
        DYSPHASIE,
        DYSARTHRIE,
        APRAXIE,
        AUTRE,
        AUCUN
    }
}