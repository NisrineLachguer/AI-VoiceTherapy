package aivoice.mobile.project.ai_voice.service;


import aivoice.mobile.project.ai_voice.model.VoiceAnalysis;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public OpenAIService(@org.springframework.context.annotation.Lazy RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }
    
    // Méthode d'initialisation pour éviter les dépendances circulaires
    public void init() {
        log.debug("Initialisation du service OpenAI");
        // Vérifier la disponibilité de FFmpeg
        checkFFmpegAvailability();
    }
    
    /**
     * Vérifie si FFmpeg est disponible sur le système
     * Cette méthode est appelée lors de l'initialisation du service
     */
    private void checkFFmpegAvailability() {
        try {
            net.bramp.ffmpeg.FFmpeg ffmpeg;
            if (ffmpegPath != null && !ffmpegPath.isEmpty()) {
                ffmpeg = new net.bramp.ffmpeg.FFmpeg(ffmpegPath);
            } else {
                ffmpeg = new net.bramp.ffmpeg.FFmpeg();
            }
            
            // Vérifier la version de FFmpeg
            String version = ffmpeg.version();
            log.info("FFmpeg disponible: {}", version);
        } catch (Exception e) {
            log.warn("FFmpeg n'est pas disponible sur le système: {}", e.getMessage());
            log.warn("Les conversions audio peuvent échouer. Veuillez installer FFmpeg ou configurer le chemin correct dans application.properties");
        }
    }

    /**
     * Transcrit un fichier audio en texte en utilisant OpenAI Whisper
     * @param audioFile Le fichier audio à transcrire
     * @return La transcription textuelle du fichier audio
     * @throws IOException Si une erreur survient lors de la lecture du fichier ou de l'appel API
     * @throws IllegalStateException Si la clé API n'est pas configurée
     * @throws RuntimeException Si l'API OpenAI retourne une erreur
     */

    @Value("${ffmpeg.path:}")
    private String ffmpegPath;
    
    @Value("${ffprobe.path:}")
    private String ffprobePath;
    
    private File convertToWav(MultipartFile audioFile) throws IOException {
        File inputFile = File.createTempFile("input_", ".tmp");
        audioFile.transferTo(inputFile);
        File outputFile = File.createTempFile("output_", ".wav");

        net.bramp.ffmpeg.builder.FFmpegBuilder builder = new net.bramp.ffmpeg.builder.FFmpegBuilder()
                .setInput(inputFile.getAbsolutePath())
                .overrideOutputFiles(true)
                .addOutput(outputFile.getAbsolutePath())
                .setAudioCodec("pcm_s16le")
                .setAudioChannels(1)
                .setAudioSampleRate(16000)
                .done();

        try {
            // Initialize FFmpeg with path if provided, otherwise use system path
            net.bramp.ffmpeg.FFmpeg ffmpeg;
            net.bramp.ffmpeg.FFprobe ffprobe;
            
            if (ffmpegPath != null && !ffmpegPath.isEmpty()) {
                ffmpeg = new net.bramp.ffmpeg.FFmpeg(ffmpegPath);
            } else {
                ffmpeg = new net.bramp.ffmpeg.FFmpeg();
            }
            
            if (ffprobePath != null && !ffprobePath.isEmpty()) {
                ffprobe = new net.bramp.ffmpeg.FFprobe(ffprobePath);
            } else {
                ffprobe = new net.bramp.ffmpeg.FFprobe();
            }
            
            net.bramp.ffmpeg.FFmpegExecutor executor = new net.bramp.ffmpeg.FFmpegExecutor(ffmpeg, ffprobe);
            executor.createJob(builder).run();
            return outputFile;
        } catch (Exception e) {
            log.error("Audio conversion failed: {}", e.getMessage(), e);
            throw new IOException("Audio conversion failed: " + e.getMessage(), e);
        } finally {
            inputFile.delete();
        }
    }    /**
     * Version modifiée de transcribeAudio utilisant la conversion WAV
     */
    public String transcribeAudio(MultipartFile audioFile) throws IOException {
        validateApiKey();

        // Convertir en WAV si nécessaire
        File audioToSend;
        String filename = audioFile.getOriginalFilename();
        String fileExtension = filename != null ?
                filename.substring(filename.lastIndexOf(".") + 1).toLowerCase() : "";

        if ("wav".equals(fileExtension)) {
            // Utiliser directement le fichier si déjà en WAV
            audioToSend = createTempAudioFile(audioFile, "wav");
        } else {
            try {
                // Essayer d'abord avec FFmpeg
                audioToSend = convertToWav(audioFile);
            } catch (IOException e) {
                log.warn("Conversion FFmpeg échouée, tentative avec méthode alternative: {}", e.getMessage());
                // Fallback à la méthode Java si FFmpeg échoue
                audioToSend = convertToWavUsingJava(audioFile);
            }
        }

        try {
            // Préparation de la requête
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("Authorization", "Bearer " + apiKey.trim());

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(audioToSend));
            body.add("model", "whisper-1");
            body.add("language", "fr"); // Optionnel selon besoin

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            log.info("Envoi à OpenAI - Fichier converti en WAV: {}", audioToSend.getAbsolutePath());

            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Erreur OpenAI: " + response.getBody());
            }

            return objectMapper.readTree(response.getBody()).path("text").asText();
        } catch (HttpClientErrorException e) {
            log.error("Erreur OpenAI: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Erreur API OpenAI: " + e.getResponseBodyAsString(), e);
        } finally {
            // Nettoyer le fichier temporaire
            if (!audioToSend.delete()) {
                log.warn("Impossible de supprimer le fichier temporaire: {}", audioToSend.getAbsolutePath());
            }
        }
    }
    /**
     * Valide l'intégrité du fichier audio
     * @param audioFile Le fichier audio à valider
     * @throws IOException Si une erreur survient lors de la lecture du fichier
     * @throws IllegalArgumentException Si le fichier n'est pas un fichier audio valide
     */
    private void validateAudioFile(MultipartFile audioFile) throws IOException {
        byte[] fileBytes = audioFile.getBytes();

        // Vérifier la taille minimale du fichier
        if (fileBytes.length < 100) {
            throw new IllegalArgumentException("Le fichier est trop petit pour être un fichier audio valide");
        }

        // Vérifier les signatures de fichiers audio courants
        String fileExtension = getFileExtension(audioFile.getOriginalFilename()).toLowerCase();

        if ("mp3".equals(fileExtension)) {
            // Vérifier la signature MP3 (ID3 ou MPEG frame sync)
            boolean isMp3Valid = (fileBytes.length > 2 &&
                    ((fileBytes[0] == 0x49 && fileBytes[1] == 0x44 && fileBytes[2] == 0x33) || // ID3
                            (fileBytes[0] == (byte)0xFF && (fileBytes[1] & 0xE0) == 0xE0))); // MPEG frame sync

            if (!isMp3Valid) {
                throw new IllegalArgumentException("Le fichier MP3 ne contient pas une signature valide");
            }
        }
    }

    /**
     * Crée un fichier temporaire avec l'extension correcte
     */
    private File createTempAudioFile(MultipartFile audioFile, String extension) throws IOException {
        String tempPrefix = "openai_audio_";
        String tempSuffix = "." + extension;
        File tempFile = File.createTempFile(tempPrefix, tempSuffix);

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(audioFile.getBytes());
        }

        // Vérifier que le fichier a bien été créé
        if (!tempFile.exists() || tempFile.length() == 0) {
            throw new IOException("Échec de la création du fichier temporaire");
        }

        return tempFile;
    }
    /**
     * Nettoie le nom de fichier pour éviter les problèmes
     */
    private String sanitizeFilename(String filename) {
        if (filename == null) return "audio_" + System.currentTimeMillis();
        return filename.replaceAll("[^a-zA-Z0-9.-]", "_");
    }
    
    /**
     * Valide l'intégrité du fichier audio
     * @param audioFile Le fichier audio à valider
     * @throws IOException Si une erreur survient lors de la lecture du fichier
     * @throws IllegalArgumentException Si le fichier n'est pas un fichier audio valide

    private void validateAudioFile(MultipartFile audioFile) throws IOException {
        if (audioFile == null || audioFile.isEmpty()) {
            throw new IllegalArgumentException("Le fichier audio est vide ou non fourni");
        }
        
        byte[] fileBytes = audioFile.getBytes();
        
        // Vérifier la taille minimale du fichier
        if (fileBytes.length < 100) {
            throw new IllegalArgumentException("Le fichier est trop petit pour être un fichier audio valide");
        }
        
        // Vérifier le type MIME
        String contentType = audioFile.getContentType();
        if (contentType == null || !contentType.startsWith("audio/")) {
            throw new IllegalArgumentException("Le type de fichier n'est pas reconnu comme un format audio valide");
        }
        
        // Vérifier les signatures de fichiers audio courants
        String fileExtension = getFileExtension(audioFile.getOriginalFilename()).toLowerCase();
        
        switch (fileExtension) {
            case "mp3":
                // Vérifier la signature MP3 (ID3 ou MPEG frame sync)
                boolean isMp3Valid = (fileBytes.length > 2 &&
                        ((fileBytes[0] == 0x49 && fileBytes[1] == 0x44 && fileBytes[2] == 0x33) || // ID3
                                (fileBytes[0] == (byte)0xFF && (fileBytes[1] & 0xE0) == 0xE0))); // MPEG frame sync
                
                if (!isMp3Valid) {
                    throw new IllegalArgumentException("Le fichier MP3 ne contient pas une signature valide");
                }
                break;
                
            case "wav":
                // Vérifier la signature WAV (RIFF....WAVE)
                boolean isWavValid = (fileBytes.length > 12 &&
                        fileBytes[0] == 'R' && fileBytes[1] == 'I' && fileBytes[2] == 'F' && fileBytes[3] == 'F' &&
                        fileBytes[8] == 'W' && fileBytes[9] == 'A' && fileBytes[10] == 'V' && fileBytes[11] == 'E');
                
                if (!isWavValid) {
                    throw new IllegalArgumentException("Le fichier WAV ne contient pas une signature valide");
                }
                break;
                
            case "m4a":
            case "aac":
                // Vérifier la signature M4A/AAC (ftyp)
                boolean isM4aValid = false;
                for (int i = 4; i < Math.min(fileBytes.length - 4, 100); i++) {
                    if (fileBytes[i] == 'f' && fileBytes[i+1] == 't' && fileBytes[i+2] == 'y' && fileBytes[i+3] == 'p') {
                        isM4aValid = true;
                        break;
                    }
                }
                
                if (!isM4aValid) {
                    throw new IllegalArgumentException("Le fichier M4A/AAC ne contient pas une signature valide");
                }
                break;
                
            case "ogg":
                // Vérifier la signature OGG (OggS)
                boolean isOggValid = (fileBytes.length > 4 &&
                        fileBytes[0] == 'O' && fileBytes[1] == 'g' && fileBytes[2] == 'g' && fileBytes[3] == 'S');
                
                if (!isOggValid) {
                    throw new IllegalArgumentException("Le fichier OGG ne contient pas une signature valide");
                }
                break;
                
            case "flac":
                // Vérifier la signature FLAC (fLaC)
                boolean isFlacValid = (fileBytes.length > 4 &&
                        fileBytes[0] == 'f' && fileBytes[1] == 'L' && fileBytes[2] == 'a' && fileBytes[3] == 'C');
                
                if (!isFlacValid) {
                    throw new IllegalArgumentException("Le fichier FLAC ne contient pas une signature valide");
                }
                break;
                
            default:
                // Pour les autres formats, on se contente de vérifier le type MIME
                log.warn("Format audio non reconnu spécifiquement: {}, validation basique uniquement", fileExtension);
        }
        
        log.debug("Fichier audio validé avec succès: {}", audioFile.getOriginalFilename());
    }*/
    
    
    /**
     * Valide le format de la clé API OpenAI
     * @throws IllegalStateException si la clé API est manquante ou mal formatée
     */
    private void validateApiKey() {
        // Vérifier si la clé est présente
        if (apiKey == null || apiKey.isEmpty() || "OPENAI_API_KEY".equals(apiKey)) {
            throw new IllegalStateException("La clé API OpenAI n'est pas configurée. Veuillez définir une clé valide dans le fichier application.properties.");
        }
        
        // Vérifier le format de la clé
        if (!apiKey.trim().startsWith("sk-")) {
            throw new IllegalStateException("Format de clé API OpenAI invalide. La clé doit commencer par 'sk-'.");
        }
        
        // Vérifier la longueur minimale de la clé
        if (apiKey.trim().length() < 20) {
            throw new IllegalStateException("La clé API OpenAI semble trop courte pour être valide.");
        }
        
        log.debug("Clé API OpenAI validée avec succès");
    }

    /**
     * Analyse une transcription pour détecter des troubles de la parole
     * @param transcription La transcription à analyser
     * @return Les résultats de l'analyse
     */
    public Map<String, Object> analyzeTranscription(String transcription) {
        Map<String, Object> results = new HashMap<>();

        // Analyse pour détecter le bégaiement (répétitions)
        boolean hasStuttering = detectStuttering(transcription);

        // Analyse pour détecter la dysphasie (structure grammaticale incorrecte)
        boolean hasDysphasia = detectDysphasia(transcription);

        // Analyse pour détecter la dysarthrie (problèmes d'articulation)
        boolean hasDysarthria = detectDysarthria(transcription);

        // Analyse pour détecter l'apraxie (difficulté à produire des sons)
        boolean hasApraxia = detectApraxia(transcription);

        // Déterminer le type de trouble principal
        VoiceAnalysis.SpeechDisorderType disorderType = VoiceAnalysis.SpeechDisorderType.AUCUN;
        double severityScore = 0.0;
        String details = "Aucun trouble détecté.";

        if (hasStuttering && hasDysphasia) {
            disorderType = VoiceAnalysis.SpeechDisorderType.AUTRE;
            severityScore = 8.0;
            details = "Troubles mixtes détectés: bégaiement et dysphasie. Recommandation de consultation avec un orthophoniste spécialisé.";
        } else if (hasStuttering) {
            disorderType = VoiceAnalysis.SpeechDisorderType.BEGAIEMENT;
            severityScore = calculateStutteringSeverity(transcription);
            details = "Bégaiement détecté. Caractérisé par des répétitions de syllabes et des blocages dans le flux de parole.";
        } else if (hasDysphasia) {
            disorderType = VoiceAnalysis.SpeechDisorderType.DYSPHASIE;
            severityScore = 6.5;
            details = "Dysphasie détectée. Caractérisée par des difficultés dans la structure grammaticale et l'organisation du discours.";
        } else if (hasDysarthria) {
            disorderType = VoiceAnalysis.SpeechDisorderType.DYSARTHRIE;
            severityScore = calculateDysarthriaSeverity(transcription);
            details = "Dysarthrie détectée. Caractérisée par des difficultés d'articulation et une parole imprécise.";
        } else if (hasApraxia) {
            disorderType = VoiceAnalysis.SpeechDisorderType.APRAXIE;
            severityScore = 7.0;
            details = "Apraxie de la parole détectée. Caractérisée par des difficultés à produire volontairement des sons et des séquences de sons.";
        }

        // Déterminer si une thérapie est nécessaire
        boolean needsTherapy = determineNeedsTherapy(severityScore);

        // Construire et retourner les résultats
        results.put("disorderType", disorderType);
        results.put("details", details);
        results.put("severityScore", severityScore);
        results.put("needsTherapy", needsTherapy);

        return results;
    }

    /**
     * Détecte les signes de bégaiement dans une transcription
     */
    private boolean detectStuttering(String transcription) {
        // Logique avancée pour détecter les répétitions caractéristiques du bégaiement
        // Recherche de motifs comme "je je", "p-p-papa", etc.
        return transcription.matches(".*\\b(\\w+)\\s+\\1\\b.*") ||
                transcription.matches(".*\\b(\\w+)-\\1.*") ||
                transcription.matches(".*\\b(\\w{1,2})-\\1-\\1.*") ||
                transcription.matches(".*\\.{3,}.*"); // Pauses prolongées
    }

    /**
     * Détecte les signes de dysphasie dans une transcription
     */
    private boolean detectDysphasia(String transcription) {
        // Logique avancée pour détecter des structures grammaticales incorrectes
        // Recherche de phrases sans verbe, ordre des mots incorrect, etc.
        return !transcription.matches(".*\\b(est|sont|ai|as|a|avons|avez|ont|suis|es|est|sommes|êtes|sont)\\b.*") ||
                transcription.matches(".*\\b(le|la|les)\\s+(le|la|les)\\b.*") ||
                transcription.toLowerCase().matches(".*\\bmoi\\s+aller\\b.*") ||
                transcription.matches(".*\\b(\\w+)\\s+(\\w+)\\s+\\1\\b.*"); // Répétitions de mots non adjacents
    }

    private boolean detectDysarthria(String transcription) {
        // Détection de la dysarthrie (problèmes d'articulation)
        // Recherche de substitutions de consonnes, omissions, etc.
        return transcription.matches(".*\\b(\\w+)e\\b.*") && !transcription.matches(".*\\br\\w+\\b.*") ||
                transcription.matches(".*\\bs\\w+\\b.*") && transcription.matches(".*\\bt\\w+\\b.*") ||
                transcription.matches(".*\\b(\\w+)\\s+pas\\s+comprendre\\b.*");
    }

    private boolean detectApraxia(String transcription) {
        // Détection de l'apraxie (difficulté à produire des sons)
        // Recherche d'erreurs de séquençage, substitutions incohérentes

        // Pattern 1: Phrases indiquant des difficultés à parler
        boolean pattern1 = transcription.matches(".*\\b(\\w+)\\s+difficile\\s+dire\\b.*");

        // Pattern 2: Inversions de lettres dans des mots (ex: "parlare" au lieu de "parler")
        boolean pattern2 = false;
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("\\b(\\w)(\\w)(\\w*)\\b.*\\b\\2\\1\\3\\b");
        java.util.regex.Matcher m = p.matcher(transcription);
        pattern2 = m.find();

        // Pattern 3: Phrases indiquant des difficultés d'articulation
        boolean pattern3 = transcription.matches(".*\\b(\\w+)\\s+pas\\s+sortir\\b.*");

        return pattern1 || pattern2 || pattern3;
    }

    private double calculateDysarthriaSeverity(String transcription) {
        // Calcul de la sévérité de la dysarthrie basé sur la longueur des phrases et la complexité
        String[] sentences = transcription.split("[.!?]");
        int totalLength = 0;

        for (String sentence : sentences) {
            if (!sentence.trim().isEmpty()) {
                totalLength += sentence.trim().split("\\s+").length;
            }
        }

        double avgLength = sentences.length > 0 ? (double) totalLength / sentences.length : 0;

        // Phrases courtes indiquent potentiellement une dysarthrie plus sévère
        return Math.min(10.0, Math.max(1.0, 10.0 - (avgLength * 0.5)));
    }

    private double calculateStutteringSeverity(String transcription) {
        // Calcul de la sévérité du bégaiement basé sur la fréquence des répétitions
        int repetitionCount = 0;
        String[] words = transcription.split("\\s+");

        for (int i = 0; i < words.length - 1; i++) {
            if (words[i].equals(words[i + 1])) {
                repetitionCount++;
            }
        }

        // Recherche de prolongations de sons (ex: "sssalut")
        int prolongationCount = 0;
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("([a-zA-Z])\\1{2,}");
        java.util.regex.Matcher matcher = pattern.matcher(transcription);
        while (matcher.find()) {
            prolongationCount++;
        }

        // Calcul du score de sévérité (0-10)
        return Math.min(10.0, 3.0 + (repetitionCount * 1.0) + (prolongationCount * 1.5));
    }

    /**
     * Détermine si une thérapie est nécessaire en fonction du score de sévérité
     * @param severityScore Le score de sévérité du trouble
     * @return true si une thérapie est recommandée, false sinon
     */
    private boolean determineNeedsTherapy(double severityScore) {
        // Si le score de sévérité est supérieur à 4, une thérapie est recommandée
        return severityScore > 4.0;
    }

    /**
     * Récupère l'extension d'un nom de fichier
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty() || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
    
    /**
     * Méthode alternative de conversion audio en WAV utilisant Java Sound API
     * Cette méthode est utilisée comme fallback quand FFmpeg n'est pas disponible
     * @param audioFile Le fichier audio à convertir
     * @return Le fichier WAV converti
     * @throws IOException Si une erreur survient lors de la conversion
     */
    private File convertToWavUsingJava(MultipartFile audioFile) throws IOException {
        log.info("Utilisation de la méthode de conversion Java pour le fichier: {}", audioFile.getOriginalFilename());
        
        // Créer un fichier temporaire pour l'entrée
        File inputFile = File.createTempFile("input_java_", "." + getFileExtension(audioFile.getOriginalFilename()));
        audioFile.transferTo(inputFile);
        
        // Créer un fichier temporaire pour la sortie
        File outputFile = File.createTempFile("output_java_", ".wav");
        
        try {
            // Utiliser JAVE pour la conversion
            MultimediaObject multimediaObject = new MultimediaObject(inputFile);
            
            AudioAttributes audio = new AudioAttributes();
            audio.setCodec("pcm_s16le");
            audio.setBitRate(16000);
            audio.setChannels(1);
            audio.setSamplingRate(16000);
            
            EncodingAttributes attrs = new EncodingAttributes();
            attrs.setOutputFormat("wav");
            attrs.setAudioAttributes(audio);
            
            Encoder encoder = new Encoder();
            encoder.encode(multimediaObject, outputFile, attrs);
            
            return outputFile;
        } catch (Exception e) {
            log.error("Échec de la conversion audio avec Java: {}", e.getMessage(), e);
            throw new IOException("Échec de la conversion audio: " + e.getMessage(), e);
        } finally {
            // Nettoyer le fichier d'entrée temporaire
            if (!inputFile.delete()) {
                log.warn("Impossible de supprimer le fichier temporaire: {}", inputFile.getAbsolutePath());
            }
        }
    }
    
    /**
     * Crée un fichier temporaire à partir d'un MultipartFile avec l'extension correcte
     * @param audioFile Le fichier audio source
     * @param extension L'extension à utiliser pour le fichier temporaire
     * @return Le fichier temporaire créé
     * @throws IOException Si une erreur survient lors de la création du fichier
     */
}