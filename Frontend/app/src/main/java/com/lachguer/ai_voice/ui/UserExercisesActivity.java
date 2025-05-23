package com.lachguer.ai_voice.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.lachguer.ai_voice.R;
import com.lachguer.ai_voice.adapter.ExercisesAdapter;
import com.lachguer.ai_voice.api.ApiClient;
import com.lachguer.ai_voice.api.ApiService;
import com.lachguer.ai_voice.model.TherapyExercise;
import com.lachguer.ai_voice.utils.SessionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserExercisesActivity extends AppCompatActivity implements ExercisesAdapter.OnExerciseClickListener {

    private static final String TAG = "UserExercisesActivity";
    private RecyclerView recyclerViewExercises;
    private ExercisesAdapter exercisesAdapter;
    private List<TherapyExercise> exercisesList;
    private ProgressBar progressBar;
    private TextView textViewNoExercises;
    private SessionManager sessionManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_exercises);

        // Initialiser les vues
        recyclerViewExercises = findViewById(R.id.recyclerViewExercises);
        progressBar = findViewById(R.id.progressBar);
        textViewNoExercises = findViewById(R.id.textViewNoExercises);

        // Configurer le RecyclerView
        recyclerViewExercises.setLayoutManager(new LinearLayoutManager(this));
        exercisesList = new ArrayList<>();
        exercisesAdapter = new ExercisesAdapter(exercisesList, this);
        recyclerViewExercises.setAdapter(exercisesAdapter);

        // Initialiser le SessionManager et l'API Service
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getClient().create(ApiService.class);

        // Charger les exercices de l'utilisateur
        loadUserExercises();
    }

    private void loadUserExercises() {
        progressBar.setVisibility(View.VISIBLE);

        Long userId = sessionManager.getUserId();

        if (userId != null) {
            Call<List<TherapyExercise>> call = apiService.getUserExercises(userId);
            call.enqueue(new Callback<List<TherapyExercise>>() {
                @Override
                public void onResponse(Call<List<TherapyExercise>> call, Response<List<TherapyExercise>> response) {
                    progressBar.setVisibility(View.GONE);

                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "Response: " + new Gson().toJson(response.body()));

                        exercisesList.clear();
                        exercisesList.addAll(response.body());
                        exercisesAdapter.notifyDataSetChanged();

                        if (exercisesList.isEmpty()) {
                            textViewNoExercises.setVisibility(View.VISIBLE);
                            recyclerViewExercises.setVisibility(View.GONE);
                        } else {
                            Log.e(TAG, "Error response: " + response.errorBody());
                            textViewNoExercises.setVisibility(View.GONE);
                            recyclerViewExercises.setVisibility(View.VISIBLE);
                        }
                    } else {
                        handleErrorResponse(response);
                    }
                }

                @Override
                public void onFailure(Call<List<TherapyExercise>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Erreur API: " + t.getMessage());
                    Toast.makeText(UserExercisesActivity.this,
                            "Erreur de connexion: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    textViewNoExercises.setVisibility(View.VISIBLE);
                    recyclerViewExercises.setVisibility(View.GONE);
                }
            });
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void handleErrorResponse(Response<List<TherapyExercise>> response) {
        try {
            String errorBody = response.errorBody() != null ?
                    response.errorBody().string() : "Erreur inconnue";
            Log.e(TAG, "Erreur API: " + errorBody);
            Toast.makeText(this, "Erreur: " + errorBody, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e(TAG, "Erreur lecture errorBody", e);
        }
        textViewNoExercises.setVisibility(View.VISIBLE);
        recyclerViewExercises.setVisibility(View.GONE);
    }

    @Override
    public void onExerciseClick(TherapyExercise exercise) {
        // Naviguer vers l'activité de détail de l'exercice
        android.content.Intent intent = new android.content.Intent(this, ExerciseDetailActivity.class);
        intent.putExtra("exerciseId", exercise.getId());
        startActivity(intent);
    }
}