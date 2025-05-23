package aivoice.mobile.project.ai_voice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TherapyExerciseDTO {
    private Long id;
    private String titre;
    private String description;
    private String instructions;
    private String troubleCible;
    private Integer dureeRecommandee;
    private Integer frequenceRecommandee;
    private Boolean completed;


}
