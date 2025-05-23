package com.lachguer.ai_voice.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.lachguer.ai_voice.R;
import com.lachguer.ai_voice.model.VoiceAnalysis;
import java.util.List;

public class AnalysisHistoryAdapter extends RecyclerView.Adapter<AnalysisHistoryAdapter.AnalysisViewHolder> {
    private List<VoiceAnalysis> analysisList;
    private OnAnalysisClickListener listener;

    public interface OnAnalysisClickListener {
        void onAnalysisClick(VoiceAnalysis analysis);
    }

    public AnalysisHistoryAdapter(List<VoiceAnalysis> analysisList, OnAnalysisClickListener listener) {
        this.analysisList = analysisList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AnalysisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_analysis_history, parent, false);
        return new AnalysisViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnalysisViewHolder holder, int position) {
        VoiceAnalysis analysis = analysisList.get(position);
        holder.bind(analysis);
    }

    @Override
    public int getItemCount() {
        return analysisList.size();
    }

    class AnalysisViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewDate;
        private TextView textViewDisorderType;
        private TextView textViewSeverity;

        public AnalysisViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewDisorderType = itemView.findViewById(R.id.textViewDisorderType);
            textViewSeverity = itemView.findViewById(R.id.textViewSeverity);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onAnalysisClick(analysisList.get(position));
                }
            });
        }

        public void bind(VoiceAnalysis analysis) {
            textViewDisorderType.setText(analysis.getTroubleDetecte() != null ?
                    analysis.getTroubleDetecte() : "Non spécifié");
            textViewSeverity.setText("Sévérité: " +
                    (analysis.getSeverityScore() != null ? analysis.getSeverityScore() : "N/A"));
            // Ajouter la date si disponible dans le modèle
            // textViewDate.setText(analysis.getDate());
        }
    }
}