package com.lachguer.ai_voice.api;

import com.lachguer.ai_voice.model.AuthResponse;
import com.lachguer.ai_voice.model.LoginRequest;
import com.lachguer.ai_voice.model.RegisterRequest;
import com.lachguer.ai_voice.model.TherapyExercise;
import com.lachguer.ai_voice.model.User;
import com.lachguer.ai_voice.model.VoiceAnalysis;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    // Inscription
    @POST("api/auth/register")
    Call<AuthResponse> registerUser(@Body RegisterRequest registerRequest);


    // Connexion
    @POST("api/auth/login")
    Call<AuthResponse> loginUser(@Body LoginRequest loginRequest);

    // Récupérer les informations de l'utilisateur
    @GET("api/users/{userId}")
    Call<User> getUserById(@Path("userId") Long userId, @Header("Authorization") String token);

    // Récupérer les analyses d'un utilisateur
    @GET("api/users/{userId}/analyses")
    Call<List<VoiceAnalysis>> getUserAnalyses(@Path("userId") Long userId);

    // Récupérer une analyse par son ID
    @GET("api/analyses/{analysisId}")
    Call<VoiceAnalysis> getAnalysisById(@Path("analysisId") Long analysisId);

    // Récupérer un exercice par son ID
    @GET("api/exercises/{exerciseId}")
    Call<TherapyExercise> getExerciseById(@Path("exerciseId") Long exerciseId);


    @GET("api/exercises/user/{userId}")
    Call<List<TherapyExercise>> getUserExercises(@Path("userId") Long userId);
    // Marquer un exercice comme complété
    @PUT("api/exercises/{exerciseId}/complete")
    Call<TherapyExercise> completeExercise(@Path("exerciseId") Long exerciseId);

    // Récupérer les exercices recommandés pour une analyse
    @GET("api/analyses/{analysisId}/exercises")
    Call<List<TherapyExercise>> getRecommendedExercises(@Path("analysisId") Long analysisId);
    
    // Récupérer tous les exercices pour un utilisateur
    @GET("api/users/{userId}/exercises")
    Call<List<TherapyExercise>> getUserExercises(@Path("userId") Long userId, @Header("Authorization") String token);

    // Analyser un enregistrement vocal
    // Modify this method in ApiService.java
    @Multipart
    @POST("api/analyses")
    Call<VoiceAnalysis> analyzeVoice(
            @Header("Authorization") String token,
            @Part("userId") RequestBody userId,
            @Part MultipartBody.Part audioFile);


}