// TherapyExercisesFragment.java
package com.lachguer.ai_voice.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lachguer.ai_voice.R;
import com.lachguer.ai_voice.adapter.ExercisesAdapter;
import com.lachguer.ai_voice.api.ApiClient;
import com.lachguer.ai_voice.api.ApiService;
import com.lachguer.ai_voice.model.TherapyExercise;
import com.lachguer.ai_voice.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TherapyExercisesFragment extends Fragment implements ExercisesAdapter.OnExerciseClickListener {
    private RecyclerView recyclerViewExercises;
    private ExercisesAdapter exercisesAdapter;
    private List<TherapyExercise> exercisesList;
    private ProgressBar progressBar;
    private TextView textViewNoExercises;
    private SessionManager sessionManager;
    private ApiService apiService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_therapy_exercises, container, false);

        // Initialiser les vues
        recyclerViewExercises = view.findViewById(R.id.recyclerViewExercises);
        progressBar = view.findViewById(R.id.progressBar);
        textViewNoExercises = view.findViewById(R.id.textViewNoExercises);

        // Configurer le RecyclerView
        recyclerViewExercises.setLayoutManager(new LinearLayoutManager(getContext()));
        exercisesList = new ArrayList<>();
        exercisesAdapter = new ExercisesAdapter(exercisesList, this);
        recyclerViewExercises.setAdapter(exercisesAdapter);

        // Initialiser le SessionManager et l'API Service
        sessionManager = new SessionManager(requireContext());
        apiService = ApiClient.getClient().create(ApiService.class);

        // Charger les exercices
        loadExercises();

        return view;
    }

    private void loadExercises() {
        progressBar.setVisibility(View.VISIBLE);

        if (sessionManager.isLoggedIn()) {
            Long userId = sessionManager.getUserId();
            String token = sessionManager.getAuthToken();

            //Call<List<TherapyExercise>> call = apiService.getUserExercises(userId, token);
            Call<List<TherapyExercise>> call = apiService.getUserExercises(userId);
            call.enqueue(new Callback<List<TherapyExercise>>() {
                @Override
                public void onResponse(Call<List<TherapyExercise>> call, Response<List<TherapyExercise>> response) {
                    progressBar.setVisibility(View.GONE);

                    if (response.isSuccessful() && response.body() != null) {
                        exercisesList.clear();
                        exercisesList.addAll(response.body());
                        exercisesAdapter.notifyDataSetChanged();

                        if (exercisesList.isEmpty()) {
                            textViewNoExercises.setVisibility(View.VISIBLE);
                            recyclerViewExercises.setVisibility(View.GONE);
                        } else {
                            textViewNoExercises.setVisibility(View.GONE);
                            recyclerViewExercises.setVisibility(View.VISIBLE);
                        }
                    } else {
                        textViewNoExercises.setVisibility(View.VISIBLE);
                        recyclerViewExercises.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Erreur de chargement des exercices", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<TherapyExercise>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    textViewNoExercises.setVisibility(View.VISIBLE);
                    recyclerViewExercises.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Erreur de connexion", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            progressBar.setVisibility(View.GONE);
            textViewNoExercises.setVisibility(View.VISIBLE);
            textViewNoExercises.setText("Veuillez vous connecter pour voir vos exercices");
            recyclerViewExercises.setVisibility(View.GONE);
        }
    }

    @Override
    public void onExerciseClick(TherapyExercise exercise) {
        Intent intent = new Intent(getActivity(), ExerciseDetailActivity.class);
        intent.putExtra("exerciseId", exercise.getId());
        startActivity(intent);
    }
}