package com.example.travellearn;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {

    TextView txtProgress, txtQuestion, txtFeedback;
    ImageView imgFlag;
    ImageButton btnBack;
    CardView cardFeedback;
    ProgressBar progressBar;
    Button btnOption1, btnOption2, btnOption3, btnOption4;
    Button[] optionButtons;

    // ── 15 countries matching CountryDetailActivity exactly ──────────────────
    private static final String[] COUNTRIES = {
            "Turkey", "France", "Japan", "Germany", "Albania",
            "Italy", "Spain", "Greece", "South Korea", "Netherlands",
            "Poland", "Sweden", "Norway", "Brazil", "China"
    };

    private static final String[] CAPITALS = {
            "Ankara", "Paris", "Tokyo", "Berlin", "Tirana",
            "Rome", "Madrid", "Athens", "Seoul", "Amsterdam",
            "Warsaw", "Stockholm", "Oslo", "Brasília", "Beijing"
    };

    private static final String[][] WRONG_CITIES = {
            {"Istanbul", "Izmir", "Bursa", "Antalya"},
            {"Marseille", "Lyon", "Toulouse", "Nice"},
            {"Osaka", "Kyoto", "Nagoya", "Yokohama"},
            {"Hamburg", "Munich", "Cologne", "Frankfurt"},
            {"Durrës", "Vlorë", "Shkodër", "Fier"},
            {"Milan", "Naples", "Turin", "Palermo"},
            {"Barcelona", "Valencia", "Seville", "Bilbao"},
            {"Thessaloniki", "Patras", "Heraklion", "Larissa"},
            {"Busan", "Incheon", "Daegu", "Gwangju"},
            {"Rotterdam", "The Hague", "Utrecht", "Eindhoven"},
            {"Kraków", "Łódź", "Wrocław", "Poznań"},
            {"Gothenburg", "Malmö", "Uppsala", "Västerås"},
            {"Bergen", "Trondheim", "Stavanger", "Drammen"},
            {"São Paulo", "Rio de Janeiro", "Salvador", "Fortaleza"},
            {"Shanghai", "Guangzhou", "Shenzhen", "Chengdu"}
    };

    private static final int[] FLAGS = {
            R.drawable.turkey,
            R.drawable.france,
            R.drawable.japan,
            R.drawable.germany,
            R.drawable.albania,
            R.drawable.italy,
            R.drawable.spain,
            R.drawable.greece,
            R.drawable.korea,        // South Korea placeholder
            R.drawable.netherlands,
            R.drawable.poland,
            R.drawable.sweden,
            R.drawable.norway,
            R.drawable.brazil,
            R.drawable.china         // China placeholder
    };

    int questionNumber = 1;
    final int TOTAL_QUESTIONS = 15;
    int score = 0;
    String correctAnswer = "";

    ArrayList<Integer> questionOrder = new ArrayList<>();
    Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        txtProgress  = findViewById(R.id.txtProgress);
        txtQuestion  = findViewById(R.id.txtQuestion);
        txtFeedback  = findViewById(R.id.txtFeedback);
        imgFlag      = findViewById(R.id.imgFlag);
        cardFeedback = findViewById(R.id.cardFeedback);
        progressBar  = findViewById(R.id.progressBar);
        btnBack      = findViewById(R.id.btnBack);
        btnOption1   = findViewById(R.id.btnOption1);
        btnOption2   = findViewById(R.id.btnOption2);
        btnOption3   = findViewById(R.id.btnOption3);
        btnOption4   = findViewById(R.id.btnOption4);

        optionButtons = new Button[]{btnOption1, btnOption2, btnOption3, btnOption4};

        // Shuffled order so each country appears once
        for (int i = 0; i < COUNTRIES.length; i++) questionOrder.add(i);
        Collections.shuffle(questionOrder);

        btnBack.setOnClickListener(v -> finish());

        for (Button btn : optionButtons) {
            btn.setOnClickListener(v -> checkAnswer((Button) v));
            addHoverEffect(btn);
        }

        loadQuestion();
    }

    private void addHoverEffect(Button btn) {
        btn.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80).start();
            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                v.animate().scaleX(1f).scaleY(1f).setDuration(120)
                        .setInterpolator(new OvershootInterpolator(2f)).start();
                if (action == MotionEvent.ACTION_UP) v.performClick();
            }
            return true;
        });
    }

    private void updateProgressBar() {
        int progress = (int) ((questionNumber - 1) / (float) TOTAL_QUESTIONS * 100);
        ObjectAnimator anim = ObjectAnimator.ofInt(progressBar, "progress",
                progressBar.getProgress(), progress);
        anim.setDuration(300);
        anim.start();
    }

    private void loadQuestion() {
        cardFeedback.setVisibility(View.GONE);
        resetButtonColors();
        for (Button btn : optionButtons) btn.setEnabled(true);

        txtProgress.setText(questionNumber + " / " + TOTAL_QUESTIONS);
        updateProgressBar();

        int countryIndex = questionOrder.get(questionNumber - 1);
        boolean isFlagQuestion = (questionNumber % 2 == 1);

        if (isFlagQuestion) {
            imgFlag.setVisibility(View.VISIBLE);
            imgFlag.setImageResource(FLAGS[countryIndex]);
            txtQuestion.setText("🏳️  Which country does this flag belong to?");
            correctAnswer = COUNTRIES[countryIndex];
            setFlagOptions(countryIndex);
        } else {
            imgFlag.setVisibility(View.GONE);
            txtQuestion.setText("🏛️  What is the capital of " + COUNTRIES[countryIndex] + "?");
            correctAnswer = CAPITALS[countryIndex];
            setCapitalOptions(countryIndex);
        }

        txtQuestion.setAlpha(0f);
        txtQuestion.setTranslationY(24f);
        txtQuestion.animate().alpha(1f).translationY(0f).setDuration(280).start();

        for (int i = 0; i < optionButtons.length; i++) {
            Button btn = optionButtons[i];
            btn.setAlpha(0f);
            btn.setTranslationY(30f);
            btn.animate().alpha(1f).translationY(0f)
                    .setDuration(250).setStartDelay(i * 60L).start();
        }
    }

    private void setFlagOptions(int correctIndex) {
        ArrayList<String> options = new ArrayList<>();
        options.add(COUNTRIES[correctIndex]);
        ArrayList<Integer> pool = new ArrayList<>();
        for (int i = 0; i < COUNTRIES.length; i++) {
            if (i != correctIndex) pool.add(i);
        }
        Collections.shuffle(pool);
        for (int i = 0; i < 3; i++) options.add(COUNTRIES[pool.get(i)]);
        Collections.shuffle(options);
        setButtonTexts(options);
    }

    private void setCapitalOptions(int correctIndex) {
        ArrayList<String> options = new ArrayList<>();
        options.add(CAPITALS[correctIndex]);
        ArrayList<String> wrongCities = new ArrayList<>(Arrays.asList(WRONG_CITIES[correctIndex]));
        Collections.shuffle(wrongCities);
        for (int i = 0; i < 3 && i < wrongCities.size(); i++) {
            options.add(wrongCities.get(i));
        }
        Collections.shuffle(options);
        setButtonTexts(options);
    }

    private void setButtonTexts(ArrayList<String> options) {
        btnOption1.setText(options.get(0));
        btnOption2.setText(options.get(1));
        btnOption3.setText(options.get(2));
        btnOption4.setText(options.get(3));
    }

    private void checkAnswer(Button selectedButton) {
        String selected = selectedButton.getText().toString();
        for (Button btn : optionButtons) btn.setEnabled(false);

        int correctColor = getResources().getColor(R.color.correct_green);
        int wrongColor   = getResources().getColor(R.color.wrong_red);

        cardFeedback.setVisibility(View.VISIBLE);
        cardFeedback.setAlpha(0f);
        cardFeedback.setScaleX(0.85f);
        cardFeedback.setScaleY(0.85f);
        cardFeedback.animate().alpha(1f).scaleX(1f).scaleY(1f)
                .setDuration(220).setInterpolator(new OvershootInterpolator(1.5f)).start();

        if (selected.equals(correctAnswer)) {
            score++;
            txtFeedback.setText("✓  Correct!  🎉");
            txtFeedback.setTextColor(Color.WHITE);
            cardFeedback.setCardBackgroundColor(correctColor);
            selectedButton.setBackgroundColor(correctColor);
            selectedButton.setTextColor(Color.WHITE);
        } else {
            txtFeedback.setText("✗  Wrong!  It was: " + correctAnswer);
            txtFeedback.setTextColor(Color.WHITE);
            cardFeedback.setCardBackgroundColor(wrongColor);
            selectedButton.setBackgroundColor(wrongColor);
            selectedButton.setTextColor(Color.WHITE);
            for (Button btn : optionButtons) {
                if (btn.getText().toString().equals(correctAnswer)) {
                    btn.setBackgroundColor(correctColor);
                    btn.setTextColor(Color.WHITE);
                }
            }
        }

        new Handler().postDelayed(() -> {
            if (questionNumber < TOTAL_QUESTIONS) {
                questionNumber++;
                loadQuestion();
            } else {
                ObjectAnimator anim = ObjectAnimator.ofInt(progressBar, "progress",
                        progressBar.getProgress(), 100);
                anim.setDuration(300);
                anim.start();
                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(QuizActivity.this, ResultsActivity.class);
                    intent.putExtra("score", score);
                    intent.putExtra("total", TOTAL_QUESTIONS);
                    startActivity(intent);
                    finish();
                }, 450);
            }
        }, 1600);
    }

    private void resetButtonColors() {
        int teal  = getResources().getColor(R.color.primary_main);
        int white = getResources().getColor(R.color.text_white);
        for (Button btn : optionButtons) {
            btn.setBackgroundColor(teal);
            btn.setTextColor(white);
        }
    }
}