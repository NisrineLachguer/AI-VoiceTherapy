package com.lachguer.ai_voice.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lachguer.ai_voice.R;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Charger le fragment d'enregistrement vocal par défaut
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RecordVoiceFragment())
                .commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.nav_record) {
                    selectedFragment = new RecordVoiceFragment();
                } else if (itemId == R.id.nav_history) {
                    selectedFragment = new AnalysisHistoryFragment();
                } else if (itemId == R.id.nav_exercises) {
                    selectedFragment = new TherapyExercisesFragment();
                } else if (itemId == R.id.nav_profile) {
                    selectedFragment = new ProfileFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                }
                return true;
            };
}