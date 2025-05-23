package com.lachguer.ai_voice.model;

public class TherapyExercise {
    private Long id;
    private String titre;
    private String description;
    private String instructions;
    private String dureeRecommandee;
    private String frequenceRecommandee;
    private Boolean completed;
    private String troubleCible;


    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getDureeRecommandee() {
        return dureeRecommandee;
    }

    public void setDureeRecommandee(String dureeRecommandee) {
        this.dureeRecommandee = dureeRecommandee;
    }

    public String getFrequenceRecommandee() {
        return frequenceRecommandee;
    }

    public void setFrequenceRecommandee(String frequenceRecommandee) {
        this.frequenceRecommandee = frequenceRecommandee;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }



    public String getTroubleCible() {
        return troubleCible;
    }

    public void setTroubleCible(String troubleCible) {
        this.troubleCible = troubleCible;
    }

}