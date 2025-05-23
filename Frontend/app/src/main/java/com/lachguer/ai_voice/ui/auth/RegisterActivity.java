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
import com.lachguer.ai_voice.model.RegisterRequest;
import com.lachguer.ai_voice.ui.MainActivity;
import com.lachguer.ai_voice.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private TextInputEditText editNom, editPrenom, editEmail, editPassword, editTelephone;
    private Button btnRegister;
    private TextView textLogin;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Log.d(TAG, "onCreate: Starting RegisterActivity");

        // Initialiser le gestionnaire de session
        sessionManager = new SessionManager(this);

        // Initialiser les vues
        editNom = findViewById(R.id.edit_nom);
        editPrenom = findViewById(R.id.edit_prenom);
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        editTelephone = findViewById(R.id.edit_telephone);
        btnRegister = findViewById(R.id.btn_register);
        textLogin = findViewById(R.id.text_login);

        // Initialiser l'API Service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Configurer le bouton d'inscription
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Configurer le lien de connexion
        textLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retourner à l'écran de connexion
                finish();
            }
        });

        Log.d(TAG, "onCreate: RegisterActivity initialized");
    }

    private void registerUser() {
        String nom = editNom.getText().toString().trim();
        String prenom = editPrenom.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String telephone = editTelephone.getText().toString().trim();

        // Validation simple
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty() || telephone.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "registerUser: Attempting to register user: " + email);

        // Créer la requête d'inscription
        RegisterRequest registerRequest = new RegisterRequest(nom, prenom, email, password, telephone);

        // Appeler l'API
        Log.d(TAG, "registerUser: Sending API request");
        Call<AuthResponse> call = apiService.registerUser(registerRequest);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                Log.d(TAG, "onResponse: HTTP Status: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    Log.d(TAG, "onResponse: Auth response received: " + (authResponse.getToken() != null));

                    // Vérifier si l'inscription est réussie (token présent)
                    if (authResponse.getToken() != null) {

                        Toast.makeText(RegisterActivity.this, "Inscription réussie, veuillez vous connecter", Toast.LENGTH_SHORT).show();
                        navigateToLoginActivity();
                    } else {
                        // Afficher le message d'erreur de l'API
                        String errorMsg = authResponse.getMessage() != null ?
                                authResponse.getMessage() : "Échec de l'inscription";
                        Log.e(TAG, "onResponse: Error message: " + errorMsg);
                        Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "Échec de l'inscription";
                        Log.e(TAG, "onResponse: Error body: " + errorBody);
                        Toast.makeText(RegisterActivity.this, "Échec de l'inscription: " + response.code(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, "onResponse: Error reading error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: Network error", t);
                Toast.makeText(RegisterActivity.this, "Erreur réseau: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToLoginActivity() {
        finish();
    }
}