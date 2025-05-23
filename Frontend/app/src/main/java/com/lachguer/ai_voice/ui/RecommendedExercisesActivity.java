package com.lachguer.ai_voice.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.lachguer.ai_voice.R;
import com.lachguer.ai_voice.adapter.ExercisesAdapter;
import com.lachguer.ai_voice.model.RetrofitClient;
import com.lachguer.ai_voice.model.TherapyExercise;
import java.util.List;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecommendedExercisesActivity extends AppCompatActivity {
    private RecyclerView recyclerViewExercises;
    private ExercisesAdapter exercisesAdapter;
    private ProgressBar progressBar;
    private TextView textViewNoExercises;

    private Long analysisId;
    private List<TherapyExercise> exercisesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended_exercises);

        analysisId = getIntent().getLongExtra("analysisId", -1);

        recyclerViewExercises = findViewById(R.id.recyclerViewExercises);
        progressBar = findViewById(R.id.progressBar);
        textViewNoExercises = findViewById(R.id.textViewNoExercises);

        setupRecyclerView();

        if (analysisId != -1) {
            loadRecommendedExercises();
        } else {
            Toast.makeText(this, "Erreur: aucune analyse trouvée", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupRecyclerView() {
        recyclerViewExercises.setLayoutManager(new LinearLayoutManager(this));
        exercisesAdapter = new ExercisesAdapter(exercisesList, this::onExerciseClick);
        recyclerViewExercises.setAdapter(exercisesAdapter);
    }

    private void loadRecommendedExercises() {
        progressBar.setVisibility(View.VISIBLE);

        RetrofitClient.getApiService().getRecommendedExercises(analysisId)
                .enqueue(new Callback<List<TherapyExercise>>() {
                    @Override
                    public void onResponse(Call<List<TherapyExercise>> call, Response<List<TherapyExercise>> response) {
                        progressBar.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            exercisesList.clear();
                            exercisesList.addAll(response.body());
                            exercisesAdapter.notifyDataSetChanged();
                            checkExercisesAvailability();
                        } else {
                            textViewNoExercises.setVisibility(View.VISIBLE);
                            textViewNoExercises.setText("Erreur lors du chargement des exercices");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TherapyExercise>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        textViewNoExercises.setVisibility(View.VISIBLE);
                        textViewNoExercises.setText("Erreur réseau: " + t.getMessage());
                    }
                });
    }

    private void checkExercisesAvailability() {
        if (exercisesList.isEmpty()) {
            textViewNoExercises.setVisibility(View.VISIBLE);
            recyclerViewExercises.setVisibility(View.GONE);
        } else {
            textViewNoExercises.setVisibility(View.GONE);
            recyclerViewExercises.setVisibility(View.VISIBLE);
        }
    }

    private void onExerciseClick(TherapyExercise exercise) {
        Intent intent = new Intent(this, ExerciseDetailActivity.class);
        intent.putExtra("exerciseId", exercise.getId());
        startActivity(intent);
    }
}