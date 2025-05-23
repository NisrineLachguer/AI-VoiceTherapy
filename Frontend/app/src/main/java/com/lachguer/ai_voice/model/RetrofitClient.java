package com.lachguer.ai_voice.model;

import android.content.Context;
import android.util.Log;

import com.lachguer.ai_voice.api.ApiService;
import com.lachguer.ai_voice.utils.SessionManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String TAG = "RetrofitClient";
    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static Retrofit retrofit;
    private static SessionManager sessionManager;

    // Initialize with context (call this once in your Application class)
    public static void initialize(Context context) {
        if (context != null) {
            sessionManager = new SessionManager(context);
        } else {
            Log.e(TAG, "Context is null in RetrofitClient initialization");
        }
    }

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> Log.d(TAG, message));
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(new AuthInterceptor())
                    .addInterceptor(new ErrorInterceptor())
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();

            // Créer un Gson personnalisé avec une tolérance aux erreurs
            com.google.gson.Gson gson = new com.google.gson.GsonBuilder()
                    .setLenient() // Permet une analyse plus souple du JSON
                    .create();
            
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getRetrofitInstance().create(ApiService.class);
    }

    // Auth interceptor to add token to requests
    private static class AuthInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();

            try {
                // Skip auth for login/register endpoints
                if (original.url().toString().contains("/api/auth/")) {
                    return chain.proceed(original);
                }

                // Get token from SessionManager (with "Bearer " prefix)
                String token = sessionManager != null ? sessionManager.getAuthToken() : null;
                if (token == null || token.isEmpty()) {
                    return chain.proceed(original);
                }

                // Add Authorization header
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", token) // Don't modify the token
                        .method(original.method(), original.body());

                return chain.proceed(requestBuilder.build());
            } catch (Exception e) {
                Log.e(TAG, "Error in AuthInterceptor: " + e.getMessage());
                return chain.proceed(original);
            }
        }
    }
    // Error interceptor to handle error responses
    private static class ErrorInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);

            if (!response.isSuccessful()) {
                String errorMsg = "HTTP " + response.code();
                try {
                    String errorBody = response.peekBody(2048).string();
                    errorMsg += ": " + errorBody;
                    Log.e(TAG, "API Error - " + errorMsg);
                } catch (IOException e) {
                    Log.e(TAG, "Error reading error response body", e);
                }

                // You could throw a custom exception here if needed
                // throw new ApiException(response.code(), errorMsg);
            }

            return response;
        }
    }
}