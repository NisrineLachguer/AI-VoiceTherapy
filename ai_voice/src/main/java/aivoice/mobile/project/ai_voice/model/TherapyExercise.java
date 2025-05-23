package aivoice.mobile.project.ai_voice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "therapy_exercises")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TherapyExercise {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties("exercises") // Ajoutez cette ligne
    private User user;
    
    @Column(nullable = false)
    private String titre;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String instructions;
    
    @Column
    @Enumerated(EnumType.STRING)
    private VoiceAnalysis.SpeechDisorderType troubleCible;
    
    @Column
    private Integer dureeRecommandee; // Durée recommandée en minutes
    
    @Column
    private Integer frequenceRecommandee; // Nombre de fois par semaine
    
    @Column
    private LocalDateTime dateCreation = LocalDateTime.now();
    
    @Column
    private Boolean completed = false;
    
    @Column
    private LocalDateTime dateCompletion;
    
    @Column
    private String audioExemplePath; // Chemin vers un fichier audio d'exemple
}