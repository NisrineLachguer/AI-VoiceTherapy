package com.lachguer.ai_voice.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.lachguer.ai_voice.R;
import com.lachguer.ai_voice.api.ApiClient;
import com.lachguer.ai_voice.api.ApiService;
import com.lachguer.ai_voice.model.TherapyExercise;
import com.lachguer.ai_voice.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExerciseDetailActivity extends AppCompatActivity {
    private TextView textViewTitle;
    private TextView textViewDescription;
    private TextView textViewInstructions;
    private TextView textViewDuration;
    private TextView textViewFrequency;
    private TextView textViewTargetDisorder;
    private TextView textViewStatus;
    private Button buttonComplete;
    private ProgressBar progressBar;

    private Long exerciseId;
    private TherapyExercise exercise;
    private SessionManager sessionManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detail);

        // Initialisation des vues
        initViews();

        // Initialisation des services
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getClient().create(ApiService.class);

        // Récupération de l'ID de l'exercice
        exerciseId = getIntent().getLongExtra("exerciseId", -1);

        if (exerciseId == -1) {
            showErrorAndFinish("Erreur: aucun exercice trouvé");
            return;
        }

        loadExerciseDetails();

        buttonComplete.setOnClickListener(v -> markExerciseAsCompleted());
    }

    private void initViews() {
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewInstructions = findViewById(R.id.textViewInstructions);
        textViewDuration = findViewById(R.id.textViewDuration);
        textViewFrequency = findViewById(R.id.textViewFrequency);
        textViewTargetDisorder = findViewById(R.id.textViewTargetDisorder);
        textViewStatus = findViewById(R.id.textViewStatus);
        buttonComplete = findViewById(R.id.buttonComplete);
        progressBar = findViewById(R.id.progressBar);
    }

    private void loadExerciseDetails() {
        progressBar.setVisibility(View.VISIBLE);
        setUiEnabled(false);

        String authToken = sessionManager.getAuthToken();

        if (authToken == null) {
            showErrorAndFinish("Session expirée, veuillez vous reconnecter");
            return;
        }

        apiService.getExerciseById(exerciseId)
                .enqueue(new Callback<TherapyExercise>() {
                    @Override
                    public void onResponse(Call<TherapyExercise> call, Response<TherapyExercise> response) {
                        progressBar.setVisibility(View.GONE);
                        setUiEnabled(true);

                        if (response.isSuccessful() && response.body() != null) {
                            exercise = response.body();
                            displayExerciseDetails();
                        } else {
                            showErrorAndFinish("Erreur: impossible de charger les détails");
                        }
                    }

                    @Override
                    public void onFailure(Call<TherapyExercise> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        setUiEnabled(true);
                        showErrorAndFinish("Erreur réseau: " + t.getMessage());
                    }
                });
    }

    private void displayExerciseDetails() {
        if (exercise == null) return;

        textViewTitle.setText(exercise.getTitre());
        textViewDescription.setText(exercise.getDescription());
        textViewInstructions.setText(exercise.getInstructions());
        textViewDuration.setText(String.format("Durée recommandée: %s minutes",
                exercise.getDureeRecommandee() != null ? exercise.getDureeRecommandee() : "N/A"));
        textViewFrequency.setText(String.format("Fréquence recommandée: %s fois par semaine",
                exercise.getFrequenceRecommandee() != null ? exercise.getFrequenceRecommandee() : "N/A"));
        textViewTargetDisorder.setText(String.format("Trouble cible: %s",
                exercise.getTroubleCible() != null ? exercise.getTroubleCible() : "N/A"));

        if (exercise.getCompleted()) {
            textViewStatus.setText("Statut: Complété");
            textViewStatus.setTextColor(ContextCompat.getColor(this, R.color.completed_green));
            buttonComplete.setText("Exercice complété");
            buttonComplete.setBackgroundColor(ContextCompat.getColor(this, R.color.completed_green));
            buttonComplete.setEnabled(false);
        } else {
            textViewStatus.setText("Statut: En cours");
            textViewStatus.setTextColor(ContextCompat.getColor(this, R.color.in_progress_orange));
            buttonComplete.setText("Marquer comme complété");
            buttonComplete.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_color));
            buttonComplete.setEnabled(true);
        }
    }

    private void markExerciseAsCompleted() {
        progressBar.setVisibility(View.VISIBLE);
        setUiEnabled(false);

        String authToken = sessionManager.getAuthToken();

        if (authToken == null) {
            showErrorAndFinish("Session expirée, veuillez vous reconnecter");
            return;
        }

        apiService.completeExercise(exerciseId)
                .enqueue(new Callback<TherapyExercise>() {
                    @Override
                    public void onResponse(Call<TherapyExercise> call, Response<TherapyExercise> response) {
                        progressBar.setVisibility(View.GONE);
                        setUiEnabled(true);

                        if (response.isSuccessful() && response.body() != null) {
                            // Mettez simplement à jour le statut localement
                            exercise.setCompleted(true);
                            displayExerciseDetails();
                            Toast.makeText(ExerciseDetailActivity.this,
                                    "Exercice marqué comme complété!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ExerciseDetailActivity.this,
                                    "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<TherapyExercise> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        setUiEnabled(true);
                        Toast.makeText(ExerciseDetailActivity.this,
                                "Erreur réseau: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setUiEnabled(boolean enabled) {
        buttonComplete.setEnabled(enabled);
    }

    private void showErrorAndFinish(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }
}