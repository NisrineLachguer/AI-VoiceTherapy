package aivoice.mobile.project.ai_voice.service;

import aivoice.mobile.project.ai_voice.model.VoiceAnalysis;
import aivoice.mobile.project.ai_voice.repository.VoiceAnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class AnalysisService {

    @Autowired
    private VoiceAnalysisRepository voiceAnalysisRepository; // Changé depuis VoiceAnalysisService

    public VoiceAnalysis analyzeAndSaveVoice(String userId, MultipartFile audioFile) throws IOException {
        // Sauvegarder le fichier audio
        String filePath = saveAudioFile(audioFile);

        // Analyser le fichier audio (simulé ici)
        VoiceAnalysis analysis = new VoiceAnalysis();
        analysis.setUserId(Long.parseLong(userId));
        analysis.setAudioFilePath(filePath);
        analysis.setTranscription("Transcription simulée");
        analysis.setTroubleDetecte(VoiceAnalysis.SpeechDisorderType.valueOf("BEGAIEMENT"));
        analysis.setSeverityScore(5.0);
        analysis.setAnalysisDetails("Détails simulés de l'analyse");
        analysis.setNeedsTherapy(true);

        return voiceAnalysisRepository.save(analysis); // Maintenant ça fonctionnera
    }

    private String saveAudioFile(MultipartFile audioFile) throws IOException {
        String uploadDir = "uploads/";
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }
        String fileName = UUID.randomUUID() + "_" + audioFile.getOriginalFilename();
        String filePath = uploadDir + fileName;
        audioFile.transferTo(new File(filePath));
        return filePath;
    }
}