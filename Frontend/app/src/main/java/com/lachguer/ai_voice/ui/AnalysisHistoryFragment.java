package com.lachguer.ai_voice.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.lachguer.ai_voice.R;
import com.lachguer.ai_voice.adapter.AnalysisHistoryAdapter;
import com.lachguer.ai_voice.model.RetrofitClient;
import com.lachguer.ai_voice.model.VoiceAnalysis;
import com.lachguer.ai_voice.utils.SessionManager;
import java.util.List;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnalysisHistoryFragment extends Fragment {
    private RecyclerView recyclerViewHistory;
    private AnalysisHistoryAdapter historyAdapter;
    private ProgressBar progressBar;
    private TextView textViewNoHistory;

    private List<VoiceAnalysis> analysisList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analysis_history, container, false);

        recyclerViewHistory = view.findViewById(R.id.recyclerViewHistory);
        progressBar = view.findViewById(R.id.progressBar);
        textViewNoHistory = view.findViewById(R.id.textViewNoHistory);

        setupRecyclerView();
        loadAnalysisHistory();

        return view;
    }

    private void setupRecyclerView() {
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        historyAdapter = new AnalysisHistoryAdapter(analysisList, this::onAnalysisClick);
        recyclerViewHistory.setAdapter(historyAdapter);
    }

    private void loadAnalysisHistory() {
        progressBar.setVisibility(View.VISIBLE);

        SessionManager sessionManager = new SessionManager(requireContext());
        Long userId = sessionManager.getUserId();

        if (userId == null || userId == 0) {
            textViewNoHistory.setVisibility(View.VISIBLE);
            textViewNoHistory.setText("Erreur: utilisateur non connecté");
            progressBar.setVisibility(View.GONE);
            return;
        }

        RetrofitClient.getApiService().getUserAnalyses(userId)
                .enqueue(new Callback<List<VoiceAnalysis>>() {
                    @Override
                    public void onResponse(Call<List<VoiceAnalysis>> call, Response<List<VoiceAnalysis>> response) {
                        progressBar.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            analysisList.clear();
                            analysisList.addAll(response.body());
                            historyAdapter.notifyDataSetChanged();
                            checkHistoryAvailability();
                        } else {
                            textViewNoHistory.setVisibility(View.VISIBLE);
                            textViewNoHistory.setText("Erreur lors du chargement de l'historique");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<VoiceAnalysis>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        textViewNoHistory.setVisibility(View.VISIBLE);
                        textViewNoHistory.setText("Erreur réseau: " + t.getMessage());
                    }
                });
    }

    private void checkHistoryAvailability() {
        if (analysisList.isEmpty()) {
            textViewNoHistory.setVisibility(View.VISIBLE);
            recyclerViewHistory.setVisibility(View.GONE);
        } else {
            textViewNoHistory.setVisibility(View.GONE);
            recyclerViewHistory.setVisibility(View.VISIBLE);
        }
    }

    private void onAnalysisClick(VoiceAnalysis analysis) {
        Intent intent = new Intent(requireContext(), AnalysisResultActivity.class);
        intent.putExtra("analysisId", analysis.getId());
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAnalysisHistory();
    }
}