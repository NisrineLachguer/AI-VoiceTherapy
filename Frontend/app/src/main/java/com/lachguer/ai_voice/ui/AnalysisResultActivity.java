package com.lachguer.ai_voice.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.lachguer.ai_voice.R;
import com.lachguer.ai_voice.model.RetrofitClient;
import com.lachguer.ai_voice.model.VoiceAnalysis;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnalysisResultActivity extends AppCompatActivity {
    private TextView textViewTranscription;
    private TextView textViewDisorderType;
    private TextView textViewSeverity;
    private TextView textViewDetails;
    private Button buttonViewExercises;
    private ProgressBar progressBar;

    private Long analysisId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_result);

        textViewTranscription = findViewById(R.id.textViewTranscription);
        textViewDisorderType = findViewById(R.id.textViewDisorderType);
        textViewSeverity = findViewById(R.id.textViewSeverity);
        textViewDetails = findViewById(R.id.textViewDetails);
        buttonViewExercises = findViewById(R.id.buttonViewExercises);
        progressBar = findViewById(R.id.progressBar);

        analysisId = getIntent().getLongExtra("analysisId", -1);

        if (analysisId == -1) {
            Toast.makeText(this, "Erreur: aucune analyse trouvée", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadAnalysisDetails();

        buttonViewExercises.setOnClickListener(v -> {
            Intent intent = new Intent(AnalysisResultActivity.this, RecommendedExercisesActivity.class);
            intent.putExtra("analysisId", analysisId);
            startActivity(intent);
        });
    }

    private void loadAnalysisDetails() {
        progressBar.setVisibility(View.VISIBLE);

        RetrofitClient.getApiService().getAnalysisById(analysisId)
                .enqueue(new Callback<VoiceAnalysis>() {
                    @Override
                    public void onResponse(Call<VoiceAnalysis> call, Response<VoiceAnalysis> response) {
                        progressBar.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            VoiceAnalysis analysis = response.body();
                            displayAnalysisResults(analysis);
                            checkNeedsTherapy(analysis.getNeedsTherapy());
                        } else {
                            Toast.makeText(AnalysisResultActivity.this,
                                    "Erreur lors du chargement des résultats", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<VoiceAnalysis> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AnalysisResultActivity.this,
                                "Erreur réseaussss: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayAnalysisResults(VoiceAnalysis analysis) {
        textViewTranscription.setText(analysis.getTranscription());
        textViewDisorderType.setText(getDisorderTypeText(analysis.getTroubleDetecte()));
        textViewSeverity.setText("Sévérité: " + formatSeverity(analysis.getSeverityScore()));
        textViewDetails.setText(analysis.getAnalysisDetails());
    }

    private String getDisorderTypeText(String troubleDetecte) {
        if (troubleDetecte == null) return "Analyse indéterminée";

        switch (troubleDetecte) {
            case "BEGAIEMENT": return "Trouble détecté: Bégaiement";
            case "DYSPHASIE": return "Trouble détecté: Dysphasie";
            case "DYSARTHRIE": return "Trouble détecté: Dysarthrie";
            case "APRAXIE": return "Trouble détecté: Apraxie de la parole";
            case "AUTRE": return "Trouble détecté: Trouble mixte";
            case "AUCUN": return "Aucun trouble détecté";
            default: return "Analyse indéterminée";
        }
    }

    private String formatSeverity(Double severityScore) {
        if (severityScore == null) return "Non déterminée";

        if (severityScore < 3.0) return "Légère (" + String.format("%.1f", severityScore) + "/10)";
        else if (severityScore < 6.0) return "Modérée (" + String.format("%.1f", severityScore) + "/10)";
        else return "Sévère (" + String.format("%.1f", severityScore) + "/10)";
    }

    private void checkNeedsTherapy(Boolean needsTherapy) {
        buttonViewExercises.setVisibility(needsTherapy != null && needsTherapy ? View.VISIBLE : View.GONE);
    }
}