package com.example.travellearn;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnCountryDetails, btnQuiz, btnLeaderboard, btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCountryDetails = findViewById(R.id.btnCountryDetails);
        btnQuiz = findViewById(R.id.btnQuiz);
        btnLeaderboard = findViewById(R.id.btnLeaderboard);
        btnSettings = findViewById(R.id.btnSettings);

        btnCountryDetails.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CountryDetailActivity.class);
            startActivity(intent);
        });

        btnQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, QuizActivity.class);
            startActivity(intent);
        });

        btnLeaderboard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LeaderboardActivity.class);
            startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }
}