package aivoice.mobile.project.ai_voice.controller;

import aivoice.mobile.project.ai_voice.model.TherapyExercise;
import aivoice.mobile.project.ai_voice.model.User;
import aivoice.mobile.project.ai_voice.repository.UserRepository;
import aivoice.mobile.project.ai_voice.service.TherapyExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;
    private final TherapyExerciseService therapyExerciseService;
    
    /**
     * Inscription d'un nouvel utilisateur
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(user.getEmail())) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Cet email est déjà utilisé");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Enregistrer le nouvel utilisateur
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }
    
    /**
     * Connexion d'un utilisateur
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("motDePasse");
        
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent() && userOpt.get().getMotDePasse().equals(password)) {
            return ResponseEntity.ok(userOpt.get());
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Email ou mot de passe incorrect");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    /**
     * Récupère les informations d'un utilisateur
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(userOpt.get());
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Utilisateur non trouvé");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    /**
     * Met à jour les informations d'un utilisateur
     */
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody User userDetails) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setNom(userDetails.getNom());
            user.setPrenom(userDetails.getPrenom());
            user.setTelephone(userDetails.getTelephone());
            // Ne pas mettre à jour l'email et le mot de passe ici pour des raisons de sécurité
            // Créer des endpoints dédiés pour ces opérations sensibles
            
            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(updatedUser);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Utilisateur non trouvé");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    /**
     * Récupère tous les exercices d'un utilisateur
     * @param userId ID de l'utilisateur
     * @return Liste des exercices de l'utilisateur
     */
    @GetMapping("/{userId}/exercises")
    public ResponseEntity<List<TherapyExercise>> getUserExercises(@PathVariable Long userId) {
        List<TherapyExercise> exercises = therapyExerciseService.getUserExercises(userId);
        return ResponseEntity.ok(exercises);
    }
}