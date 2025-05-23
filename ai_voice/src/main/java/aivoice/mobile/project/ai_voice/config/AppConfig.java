package aivoice.mobile.project.ai_voice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class AppConfig {
    
    // La méthode init() de VoiceAnalysisService est maintenant appelée automatiquement 
    // grâce à l'annotation @PostConstruct dans cette classe


    /**
     * Bean RestTemplate pour les appels API externes avec configuration de timeout
     */
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();


        return restTemplate;
    }
}