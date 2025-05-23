package com.lachguer.ai_voice.ui;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.lachguer.ai_voice.R;
import com.lachguer.ai_voice.model.RetrofitClient;
import com.lachguer.ai_voice.model.VoiceAnalysis;
import com.lachguer.ai_voice.utils.AudioConverter;
import com.lachguer.ai_voice.utils.SessionManager;
import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ws.schild.jave.EncoderException;

public class RecordVoiceFragment extends Fragment {
    private static final String TAG = "RecordVoiceFragment";

    private Button buttonRecord;
    private Button buttonStopRecord;
    private Button buttonAnalyze;
    private TextView textViewStatus;
    private ProgressBar progressBarRecording;
    private ProgressBar progressBarAnalyzing;

    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private boolean isRecording = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_voice, container, false);

        buttonRecord = view.findViewById(R.id.buttonRecord);
        buttonStopRecord = view.findViewById(R.id.buttonStopRecord);
        buttonAnalyze = view.findViewById(R.id.buttonAnalyze);
        textViewStatus = view.findViewById(R.id.textViewStatus);
        progressBarRecording = view.findViewById(R.id.progressBarRecording);
        progressBarAnalyzing = view.findViewById(R.id.progressBarAnalyzing);

        buttonStopRecord.setEnabled(false);
        buttonAnalyze.setEnabled(false);

        buttonRecord.setOnClickListener(v -> startRecording());
        buttonStopRecord.setOnClickListener(v -> stopRecording());
        buttonAnalyze.setOnClickListener(v -> analyzeRecording());

        return view;
    }

    private void startRecording() {
        try {
            if (getContext() == null) {
                Log.e(TAG, "Context est null lors du démarrage de l'enregistrement");
                return;
            }

            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO}, 1);
                return;
            }

            File outputDir = requireContext().getCacheDir();
            File outputFile = File.createTempFile("audio_record_", ".mp3", outputDir);
            audioFilePath = outputFile.getAbsolutePath();

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.prepare();
            mediaRecorder.start();

            isRecording = true;
            textViewStatus.setText("Enregistrement en cours...");
            progressBarRecording.setVisibility(View.VISIBLE);

            buttonRecord.setEnabled(false);
            buttonStopRecord.setEnabled(true);
            buttonAnalyze.setEnabled(false);

        } catch (IOException e) {
            Log.e(TAG, "Erreur IO lors de l'enregistrement: " + e.getMessage());
            Toast.makeText(requireContext(), "Erreur lors de l'enregistrement: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            resetRecordingState();
        } catch (Exception e) {
            Log.e(TAG, "Erreur inattendue lors de l'enregistrement: " + e.getMessage());
            Toast.makeText(requireContext(), "Erreur inattendue: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            resetRecordingState();
        }
    }

    private void stopRecording() {
        if (isRecording && mediaRecorder != null) {
            try {
                mediaRecorder.stop();
            } catch (IllegalStateException e) {
                Log.e(TAG, "MediaRecorder stop failed - wasn't recording", e);
                // Continue with cleanup even if stop failed
            } catch (RuntimeException e) {
                Log.e(TAG, "MediaRecorder stop failed unexpectedly", e);
                // Continue with cleanup
            } finally {
                // Always release resources
                mediaRecorder.release();
                mediaRecorder = null;
                isRecording = false;

                textViewStatus.setText("Enregistrement terminé");
                progressBarRecording.setVisibility(View.GONE);

                buttonRecord.setEnabled(true);
                buttonStopRecord.setEnabled(false);
                buttonAnalyze.setEnabled(true);
            }
        } else {
            resetRecordingState();
        }
    }
    private void resetRecordingState() {
        isRecording = false;
        if (textViewStatus != null) {
            textViewStatus.setText("Prêt à enregistrer");
        }
        if (progressBarRecording != null) {
            progressBarRecording.setVisibility(View.GONE);
        }
        if (buttonRecord != null) {
            buttonRecord.setEnabled(true);
        }
        if (buttonStopRecord != null) {
            buttonStopRecord.setEnabled(false);
        }
        if (buttonAnalyze != null) {
            buttonAnalyze.setEnabled(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaRecorder != null) {
            try {
                if (isRecording) {
                    mediaRecorder.stop();
                }
                mediaRecorder.release();
                mediaRecorder = null;
            } catch (Exception e) {
                Log.e(TAG, "Erreur lors du nettoyage du MediaRecorder: " + e.getMessage());
            }
        }
    }

    private void analyzeRecording() {
        if (audioFilePath == null || getContext() == null) {
            Toast.makeText(requireContext(), "Aucun enregistrement disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        // Créer l'objet File pour le fichier audio original
        File originalAudioFile = new File(audioFilePath);

        // Vérifier si le fichier existe
        if (!originalAudioFile.exists()) {
            Toast.makeText(requireContext(), "Le fichier audio n'existe pas", Toast.LENGTH_SHORT).show();
            return;
        }

        // Vérifier la taille du fichier
        long fileSize = originalAudioFile.length();
        if (fileSize > 10 * 1024 * 1024) { // 10MB
            Toast.makeText(requireContext(),
                    "Fichier trop volumineux (max 10MB)",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        textViewStatus.setText("Préparation de l'audio...");
        progressBarAnalyzing.setVisibility(View.VISIBLE);
        buttonAnalyze.setEnabled(false);

        // Utiliser un thread séparé pour la conversion audio
        new Thread(() -> {
            try {
                File audioFileToSend;

                // Déterminer si une conversion est nécessaire en fonction de l'extension
                String extension = AudioConverter.getFileExtension(audioFilePath);
                if ("wav".equals(extension)) {
                    // Utiliser directement le fichier s'il est déjà en WAV
                    audioFileToSend = originalAudioFile;
                } else {
                    // Convertir le fichier en WAV pour les autres formats
                    requireActivity().runOnUiThread(() ->
                            textViewStatus.setText("Conversion du format audio..."));

                    audioFileToSend = AudioConverter.convertToWav(originalAudioFile, requireContext());
                }

                // Créer les parties de la requête
                RequestBody requestFile = RequestBody.create(
                        MediaType.parse("audio/" + AudioConverter.getFileExtension(audioFileToSend.getName())),
                        audioFileToSend);

                MultipartBody.Part body = MultipartBody.Part.createFormData(
                        "audioFile",
                        audioFileToSend.getName(),
                        requestFile);

                SessionManager sessionManager = new SessionManager(requireContext());
                Long userId = sessionManager.getUserId();
                RequestBody userIdPart = RequestBody.create(MediaType.parse("text/plain"), userId.toString());
                String token = sessionManager.getToken();

                Log.d(TAG, "Taille du fichier à envoyer: " + audioFileToSend.length() + " bytes");

                // Mettre à jour l'UI dans le thread principal
                requireActivity().runOnUiThread(() ->
                        textViewStatus.setText("Analyse en cours..."));

                // Créer et envoyer la requête
                Call<VoiceAnalysis> call = RetrofitClient.getApiService().analyzeVoice(
                        "Bearer " + token,
                        userIdPart,
                        body
                );

                Log.d(TAG, "Requête envoyée: " + call.request().toString());

                // Exécuter la requête dans le thread réseau
                call.enqueue(new Callback<VoiceAnalysis>() {
                    @Override
                    public void onResponse(Call<VoiceAnalysis> call, Response<VoiceAnalysis> response) {
                        requireActivity().runOnUiThread(() -> {
                            progressBarAnalyzing.setVisibility(View.GONE);

                            if (response.isSuccessful() && response.body() != null) {
                                navigateToAnalysisResult(response.body().getId());
                            } else {
                                handleApiError(response);
                                buttonAnalyze.setEnabled(true);
                            }

                            // Nettoyer le fichier WAV temporaire si différent de l'original
                            if (!audioFileToSend.equals(originalAudioFile) && audioFileToSend.exists()) {
                                audioFileToSend.delete();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<VoiceAnalysis> call, Throwable t) {
                        requireActivity().runOnUiThread(() -> {
                            progressBarAnalyzing.setVisibility(View.GONE);
                            buttonAnalyze.setEnabled(true);
                            handleNetworkError(t);

                            // Nettoyer le fichier WAV temporaire si différent de l'original
                            if (!audioFileToSend.equals(originalAudioFile) && audioFileToSend.exists()) {
                                audioFileToSend.delete();
                            }
                        });
                    }
                });

            } catch (IOException e) {
                Log.e(TAG, "Erreur IO lors de la préparation du fichier audio", e);
                requireActivity().runOnUiThread(() -> {
                    progressBarAnalyzing.setVisibility(View.GONE);
                    buttonAnalyze.setEnabled(true);
                    textViewStatus.setText("Prêt à enregistrer");
                    Toast.makeText(requireContext(),
                            "Erreur lors de la préparation du fichier audio: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            } catch (Exception e) {
                Log.e(TAG, "Erreur inattendue", e);
                requireActivity().runOnUiThread(() -> {
                    progressBarAnalyzing.setVisibility(View.GONE);
                    buttonAnalyze.setEnabled(true);
                    textViewStatus.setText("Prêt à enregistrer");
                    Toast.makeText(requireContext(),
                            "Erreur inattendue: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void handleApiError(Response<VoiceAnalysis> response) {
        try {
            String errorBody = response.errorBody() != null ?
                    response.errorBody().string() : "No error details";

            Log.e("API_ERROR", "Error " + response.code() + ": " + errorBody);

            // Afficher une erreur plus détaillée à l'utilisateur
            String userMessage = "Erreur lors de l'analyse";
            if (errorBody.contains("Invalid user ID")) {
                userMessage = "Problème d'identification";
            } else if (errorBody.contains("Error processing audio")) {
                userMessage = "Format audio non supporté";
            }

            Toast.makeText(requireContext(),
                    userMessage + " (Code: " + response.code() + ")",
                    Toast.LENGTH_LONG).show();

            textViewStatus.setText("Analyse échouée");

        } catch (IOException e) {
            Log.e("API_ERROR", "Error parsing error response", e);
            Toast.makeText(requireContext(),
                    "Erreur inconnue", Toast.LENGTH_SHORT).show();
            textViewStatus.setText("Erreur inconnue");
        }
    }

    private void handleNetworkError(Throwable t) {
        String errorMsg = "Erreur réseau";
        if (t instanceof IOException) {
            errorMsg = "Problème de connexion";
        } else if (t instanceof com.google.gson.JsonSyntaxException ||
                t instanceof com.google.gson.stream.MalformedJsonException) {
            // Gestion spécifique des erreurs de parsing JSON
            errorMsg = "Erreur de format de données";
            Log.e("JSON_ERROR", "Erreur de parsing JSON", t);
        } else if (t.getMessage() != null) {
            errorMsg = t.getMessage();
        }

        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show();
        Log.e("NETWORK_ERROR", "API call failed", t);
        textViewStatus.setText("Échec: " + errorMsg);
    }

    private void navigateToAnalysisResult(Long analysisId) {
        Intent intent = new Intent(requireContext(), AnalysisResultActivity.class);
        intent.putExtra("analysisId", analysisId);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording();
            } else {
                Toast.makeText(requireContext(), "Permission d'enregistrement audio refusée",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isRecording) {
            stopRecording();
        }
    }
}
