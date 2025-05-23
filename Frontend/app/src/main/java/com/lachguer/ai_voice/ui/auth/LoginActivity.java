package com.lachguer.ai_voice.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.lachguer.ai_voice.R;
import com.lachguer.ai_voice.api.ApiClient;
import com.lachguer.ai_voice.api.ApiService;
import com.lachguer.ai_voice.model.AuthResponse;
import com.lachguer.ai_voice.model.LoginRequest;
import com.lachguer.ai_voice.ui.MainActivity;
import com.lachguer.ai_voice.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private TextInputEditText editEmail, editPassword;
    private Button btnLogin;
    private TextView textRegister;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialiser le gestionnaire de session
        sessionManager = new SessionManager(this);
        
        // Vérifier si l'application a été fermée précédemment ou redémarrée depuis le launcher
        boolean appWasClosed = getSharedPreferences("AIVoiceAppState", MODE_PRIVATE).getBoolean("app_closed", false);
        
        if ((isTaskRoot() && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)) || appWasClosed) {
            // Réinitialiser l'état de connexion si l'app est lancée depuis le launcher ou a été fermée
            sessionManager.logout();
            // Réinitialiser le flag app_closed
            getSharedPreferences("AIVoiceAppState", MODE_PRIVATE).edit().putBoolean("app_closed", false).apply();
            Log.d(TAG, "onCreate: Session réinitialisée car l'application a été fermée ou relancée");
        } else if (sessionManager.isLoggedIn()) {
            // Si ce n'est pas un lancement depuis le launcher et que l'utilisateur est connecté
            navigateToMainActivity();
            return;
        }

        // Initialiser les vues
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        btnLogin = findViewById(R.id.btn_login);
        textRegister = findViewById(R.id.text_register);

        // Initialiser l'API Service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Configurer le bouton de connexion
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // Configurer le lien d'inscription
        textRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        Log.d(TAG, "onCreate: Activity initialized");
    }

    private void loginUser() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        // Validation simple
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "loginUser: Attempting login with email: " + email);

        // Créer la requête de connexion
        LoginRequest loginRequest = new LoginRequest(email, password);

        // Appeler l'API
        Log.d(TAG, "loginUser: Sending API request");
        Call<AuthResponse> call = apiService.loginUser(loginRequest);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                Log.d(TAG, "onResponse: HTTP Status: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    Log.d(TAG, "onResponse: Auth response received: " + (authResponse.getToken() != null));

                    // Vérifier si la connexion est réussie (token présent)
                    if (authResponse.getToken() != null) {
                        // Sauvegarder les informations de session
                        sessionManager.createLoginSession(
                                authResponse.getToken(),
                                authResponse.getUserId(),
                                authResponse.getNom(),
                                authResponse.getPrenom(),
                                authResponse.getEmail()
                        );

                        Toast.makeText(LoginActivity.this, "Connexion réussie", Toast.LENGTH_SHORT).show();
                        navigateToMainActivity();
                    } else {
                        // Afficher le message d'erreur de l'API
                        String errorMsg = authResponse.getMessage() != null ?
                                authResponse.getMessage() : "Échec de connexion";
                        Log.e(TAG, "onResponse: Error message: " + errorMsg);
                        Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "Échec de connexion";
                        Log.e(TAG, "onResponse: Error body: " + errorBody);
                        Toast.makeText(LoginActivity.this, "Échec de connexion: " + response.code(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, "onResponse: Error reading error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: Network error", t);
                Toast.makeText(LoginActivity.this, "Erreur réseau: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}