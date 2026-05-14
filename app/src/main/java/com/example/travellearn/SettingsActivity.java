package com.example.travellearn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    EditText editUsername;
    CheckBox checkSound;
    Button btnSaveSettings, btnBackHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        editUsername = findViewById(R.id.editUsername);
        checkSound = findViewById(R.id.checkSound);
        btnSaveSettings = findViewById(R.id.btnSaveSettings);
        btnBackHome = findViewById(R.id.btnBackHome);

        SharedPreferences prefs = getSharedPreferences("TravelLearnPrefs", MODE_PRIVATE);

        String savedName = prefs.getString("username", "");
        boolean soundEnabled = prefs.getBoolean("soundEnabled", true);

        editUsername.setText(savedName);
        checkSound.setChecked(soundEnabled);

        btnSaveSettings.setOnClickListener(v -> {
            String username = editUsername.getText().toString().trim();
            boolean sound = checkSound.isChecked();

            prefs.edit()
                    .putString("username", username)
                    .putBoolean("soundEnabled", sound)
                    .apply();

            Toast.makeText(SettingsActivity.this, "Settings saved!", Toast.LENGTH_SHORT).show();
        });

        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}