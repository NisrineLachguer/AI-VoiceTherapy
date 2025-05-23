package com.lachguer.ai_voice.model;

public class VoiceAnalysis {
    private Long id;
    private String transcription;
    private String troubleDetecte;
    private Double severityScore;
    private String analysisDetails;
    private Boolean needsTherapy;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public String getTroubleDetecte() {
        return troubleDetecte;
    }

    public void setTroubleDetecte(String troubleDetecte) {
        this.troubleDetecte = troubleDetecte;
    }

    public Double getSeverityScore() {
        return severityScore;
    }

    public void setSeverityScore(Double severityScore) {
        this.severityScore = severityScore;
    }

    public String getAnalysisDetails() {
        return analysisDetails;
    }

    public void setAnalysisDetails(String analysisDetails) {
        this.analysisDetails = analysisDetails;
    }

    public Boolean getNeedsTherapy() {
        return needsTherapy;
    }

    public void setNeedsTherapy(Boolean needsTherapy) {
        this.needsTherapy = needsTherapy;
    }
}