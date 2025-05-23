package aivoice.mobile.project.ai_voice.service;

import aivoice.mobile.project.ai_voice.dto.AuthResponse;
import aivoice.mobile.project.ai_voice.dto.LoginRequest;
import aivoice.mobile.project.ai_voice.dto.RegisterRequest;
import aivoice.mobile.project.ai_voice.model.User;
import aivoice.mobile.project.ai_voice.repository.UserRepository;

import aivoice.mobile.project.ai_voice.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            return AuthResponse.builder()
                    .message("Cet email est déjà utilisé")
                    .build();
        }

        // Créer un nouvel utilisateur
        User user = new User();
        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user.setEmail(request.getEmail());
        user.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        user.setTelephone(request.getTelephone());

        // Sauvegarder l'utilisateur
        userRepository.save(user);

        // Générer le token JWT
        String token = jwtService.generateToken(user.getEmail());

        // Retourner la réponse
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(user.getEmail())
                .message("Inscription réussie")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        try {
            // Authentifier l'utilisateur
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getMotDePasse()
                    )
            );

            // Récupérer l'utilisateur
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Générer le token JWT
            String token = jwtService.generateToken(user.getEmail());

            // Retourner la réponse
            return AuthResponse.builder()
                    .token(token)
                    .userId(user.getId())
                    .nom(user.getNom())
                    .prenom(user.getPrenom())
                    .email(user.getEmail())
                    .message("Connexion réussie")
                    .build();
        } catch (Exception e) {
            return AuthResponse.builder()
                    .message("Email ou mot de passe incorrect")
                    .build();
        }
    }
}