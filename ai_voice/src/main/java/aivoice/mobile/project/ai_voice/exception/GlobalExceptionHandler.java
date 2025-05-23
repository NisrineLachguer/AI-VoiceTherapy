package aivoice.mobile.project.ai_voice.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire global d'exceptions pour l'application AI-VoiceTherapy.
 * Intercepte les exceptions et les transforme en réponses HTTP appropriées.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Gère les exceptions spécifiques à l'application VoiceTherapy.
     *
     * @param ex L'exception à traiter
     * @param request La requête web associée
     * @return Une réponse HTTP avec les détails de l'erreur
     */
    @ExceptionHandler(VoiceTherapyException.class)
    public ResponseEntity<?> handleVoiceTherapyException(VoiceTherapyException ex, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", new Date());
        errorDetails.put("message", ex.getMessage());
        errorDetails.put("details", request.getDescription(false));
        
        return new ResponseEntity<>(errorDetails, ex.getStatus());
    }
    
    /**
     * Gère toutes les autres exceptions non spécifiques.
     *
     * @param ex L'exception à traiter
     * @param request La requête web associée
     * @return Une réponse HTTP avec les détails de l'erreur
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", new Date());
        errorDetails.put("message", ex.getMessage());
        errorDetails.put("details", request.getDescription(false));
        
        return ResponseEntity.internalServerError().body(errorDetails);
    }
}