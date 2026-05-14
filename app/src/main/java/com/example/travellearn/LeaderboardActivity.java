package com.example.travellearn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Locale;

public class LeaderboardActivity extends AppCompatActivity {

    TextView txtBestScore, txtMotivation, txtTotalGames, txtAvgScore;
    LinearLayout layoutRecentScores;
    Button btnPlayQuiz, btnBackHome;
    ImageButton btnBack;

    static final String PREFS_NAME  = "TravelLearnPrefs";
    static final String KEY_BEST    = "bestScore";
    static final String KEY_HISTORY = "scoreHistory";
    static final String KEY_GAMES   = "totalGames";

    // Quiz now has 15 questions
    static final int TOTAL_QUESTIONS = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        txtBestScore       = findViewById(R.id.txtBestScore);
        txtMotivation      = findViewById(R.id.txtMotivation);
        txtTotalGames      = findViewById(R.id.txtTotalGames);
        txtAvgScore        = findViewById(R.id.txtAvgScore);
        layoutRecentScores = findViewById(R.id.layoutRecentScores);
        btnPlayQuiz        = findViewById(R.id.btnPlayQuiz);
        btnBackHome        = findViewById(R.id.btnBackHome);
        btnBack            = findViewById(R.id.btnBack);

        loadAndDisplay();

        btnBack.setOnClickListener(v -> finish());

        btnPlayQuiz.setOnClickListener(v ->
                startActivity(new Intent(this, QuizActivity.class))
        );

        btnBackHome.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private void loadAndDisplay() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int bestScore   = prefs.getInt(KEY_BEST, 0);
        int totalGames  = prefs.getInt(KEY_GAMES, 0);
        String histJson = prefs.getString(KEY_HISTORY, "[]");

        txtBestScore.setText(bestScore + " / " + TOTAL_QUESTIONS);

        // Thresholds scaled to 15 questions
        if (totalGames == 0) {
            txtMotivation.setText("Play your first quiz to start the adventure!");
        } else if (bestScore >= 14) {
            txtMotivation.setText("🌟 Outstanding! You're a geography master!");
        } else if (bestScore >= 11) {
            txtMotivation.setText("🎉 Great job! Keep going for a perfect score!");
        } else if (bestScore >= 8) {
            txtMotivation.setText("💪 Good effort! Practice makes perfect!");
        } else {
            txtMotivation.setText("🗺️ Keep exploring — every quiz makes you better!");
        }

        txtTotalGames.setText(String.valueOf(totalGames));

        JSONArray history;
        try {
            history = new JSONArray(histJson);
        } catch (JSONException e) {
            history = new JSONArray();
        }

        if (history.length() > 0) {
            double sum = 0;
            for (int i = 0; i < history.length(); i++) {
                try { sum += history.getInt(i); } catch (JSONException ignored) {}
            }
            txtAvgScore.setText(String.format(Locale.getDefault(), "%.1f", sum / history.length()));
        } else {
            txtAvgScore.setText("—");
        }

        layoutRecentScores.removeAllViews();
        int start = Math.max(0, history.length() - 5);
        for (int i = history.length() - 1; i >= start; i--) {
            int gameScore = 0;
            try { gameScore = history.getInt(i); } catch (JSONException ignored) {}
            addRecentScoreRow(i + 1, gameScore);
        }

        if (history.length() == 0) {
            TextView empty = new TextView(this);
            empty.setText("No games played yet.");
            empty.setTextColor(getResources().getColor(R.color.text_secondary));
            empty.setTextSize(14);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(0, 16, 0, 16);
            layoutRecentScores.addView(empty);
        }
    }

    private void addRecentScoreRow(int gameNumber, int gameScore) {
        CardView card = new CardView(this);
        CardView.LayoutParams cardParams = new CardView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, dpToPx(10));
        card.setLayoutParams(cardParams);
        card.setRadius(dpToPx(14));
        card.setCardElevation(dpToPx(2));
        card.setCardBackgroundColor(getResources().getColor(R.color.white));

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dpToPx(16), dpToPx(14), dpToPx(16), dpToPx(14));

        TextView tvGame = new TextView(this);
        tvGame.setText("Game #" + gameNumber);
        tvGame.setTextSize(14);
        tvGame.setTextColor(getResources().getColor(R.color.text_secondary));
        LinearLayout.LayoutParams gameParams =
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        tvGame.setLayoutParams(gameParams);
        row.addView(tvGame);

        TextView tvScore = new TextView(this);
        tvScore.setText(gameScore + " / " + TOTAL_QUESTIONS);
        tvScore.setTextSize(14);
        tvScore.setTypeface(null, Typeface.BOLD);
        tvScore.setGravity(Gravity.CENTER);
        tvScore.setPadding(dpToPx(14), dpToPx(5), dpToPx(14), dpToPx(5));
        tvScore.setTextColor(Color.WHITE);

        // Color thresholds scaled to 15 questions
        int bgColor;
        if (gameScore >= 13) {
            bgColor = getResources().getColor(R.color.accent_yellow);   // gold: 13-15
        } else if (gameScore >= 10) {
            bgColor = getResources().getColor(R.color.correct_green);   // green: 10-12
        } else if (gameScore >= 7) {
            bgColor = getResources().getColor(R.color.primary_light);   // teal: 7-9
        } else {
            bgColor = getResources().getColor(R.color.accent_pink);     // pink: 0-6
        }

        GradientDrawable badge = new GradientDrawable();
        badge.setCornerRadius(dpToPx(20));
        badge.setColor(bgColor);
        tvScore.setBackground(badge);

        row.addView(tvScore);
        card.addView(row);
        layoutRecentScores.addView(card);
    }

    public static void saveScore(android.content.Context ctx, int score) {
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        int currentBest = prefs.getInt(KEY_BEST, 0);
        if (score > currentBest) editor.putInt(KEY_BEST, score);

        int games = prefs.getInt(KEY_GAMES, 0);
        editor.putInt(KEY_GAMES, games + 1);

        String historyJson = prefs.getString(KEY_HISTORY, "[]");
        JSONArray history;
        try {
            history = new JSONArray(historyJson);
        } catch (JSONException e) {
            history = new JSONArray();
        }
        history.put(score);
        while (history.length() > 50) history.remove(0);
        editor.putString(KEY_HISTORY, history.toString());
        editor.apply();
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}