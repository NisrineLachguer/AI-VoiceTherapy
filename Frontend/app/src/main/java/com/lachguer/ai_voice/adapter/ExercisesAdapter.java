package com.lachguer.ai_voice.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.lachguer.ai_voice.R;
import com.lachguer.ai_voice.model.TherapyExercise;
import java.util.List;

public class ExercisesAdapter extends RecyclerView.Adapter<ExercisesAdapter.ExerciseViewHolder> {
    private List<TherapyExercise> exercisesList;
    private OnExerciseClickListener listener;

    public interface OnExerciseClickListener {
        void onExerciseClick(TherapyExercise exercise);
    }

    public ExercisesAdapter(List<TherapyExercise> exercisesList, OnExerciseClickListener listener) {
        this.exercisesList = exercisesList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        TherapyExercise exercise = exercisesList.get(position);
        holder.bind(exercise);
    }

    @Override
    public int getItemCount() {
        return exercisesList.size();
    }


    class ExerciseViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewDescription;
        private TextView textViewDuration;
        private TextView textViewStatus;
        private TextView textViewTargetDisorder;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewDuration = itemView.findViewById(R.id.textViewDuration);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            textViewTargetDisorder = itemView.findViewById(R.id.textViewTargetDisorder);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onExerciseClick(exercisesList.get(position));
                }
            });
        }

        public void bind(TherapyExercise exercise) {
            textViewTitle.setText(exercise.getTitre() != null ? exercise.getTitre() : "Exercice");
            textViewDescription.setText(exercise.getDescription() != null ?
                    exercise.getDescription() : "Aucune description disponible");
            textViewDuration.setText("Durée: " +
                    (exercise.getDureeRecommandee() != null ? exercise.getDureeRecommandee() : "N/A"));
            textViewStatus.setText("Statut: " + (exercise.getCompleted() ? "Complété" : "En cours"));
            textViewTargetDisorder.setText("Trouble cible: " +
                    (exercise.getTroubleCible() != null ? exercise.getTroubleCible() : "N/A"));

            // Remove the troubleCible reference since it doesn't exist in the model
            textViewTargetDisorder.setText("Recommended frequency: " +
                    (exercise.getFrequenceRecommandee() != null ? exercise.getFrequenceRecommandee() : "N/A"));

        }
    }}