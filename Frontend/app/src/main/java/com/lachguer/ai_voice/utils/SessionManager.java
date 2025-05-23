package com.lachguer.ai_voice.utils;


import android.content.Context;
import android.content.SharedPreferences;

import com.lachguer.ai_voice.model.User;

public class SessionManager {
    private static final String PREF_NAME = "AIVoiceSession";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NOM = "nom";
    private static final String KEY_PRENOM = "prenom";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /**
     * Crée une session après connexion
     */
    public void createLoginSession(String token, Long userId, String nom, String prenom, String email) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_TOKEN, token);
        editor.putLong(KEY_USER_ID, userId);
        editor.putString(KEY_NOM, nom);
        editor.putString(KEY_PRENOM, prenom);
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    /**
     * Vérifie si l'utilisateur est connecté
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Récupère le token d'authentification
     */
    public String getToken() {
        return pref.getString(KEY_TOKEN, null); // Retourne juste le token sans "Bearer "
    }

    /**
     * Récupère l'ID de l'utilisateur
     */
    public Long getUserId() {
        return pref.getLong(KEY_USER_ID, 0);
    }

    /**
     * Récupère les informations de l'utilisateur
     */
    public User getUserDetails() {
        User user = new User();
        user.setId(pref.getLong(KEY_USER_ID, 0));
        user.setNom(pref.getString(KEY_NOM, null));
        user.setPrenom(pref.getString(KEY_PRENOM, null));
        user.setEmail(pref.getString(KEY_EMAIL, null));
        return user;
    }

    /**
     * Alias pour getToken() pour compatibilité avec le code existant
     */
    public String getAuthToken() {
        return getToken(); // Appel la méthode getToken pour maintenir la cohérence
    }
    /**
     * Déconnecte l'utilisateur en effaçant la session
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }
}