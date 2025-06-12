package com.example.greenplate.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.greenplate.R;

public class Splash extends AppCompatActivity {
    private static final int SPLASH_DELAY = 2000; // 2 detik

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("user_profile", MODE_PRIVATE);
            boolean hasProfile = prefs.contains("name"); // Atau cek field lain

            if (hasProfile) {
                startActivity(new Intent(this, MainActivity.class));
            } else {
                startActivity(new Intent(this, Profile.class));
            }
            finish(); // Tutup splash
        }, SPLASH_DELAY);
    }
}