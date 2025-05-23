package aivoice.mobile.project.ai_voice.exception;

import org.springframework.http.HttpStatus;

public class VoiceTherapyException extends RuntimeException {
    
    private final HttpStatus status;
    
    public VoiceTherapyException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
    
    public VoiceTherapyException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }
    
    public HttpStatus getStatus() {
        return status;
    }
}