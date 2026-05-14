package com.example.travellearn;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.Locale;

public class CountryDetailActivity extends AppCompatActivity {

    CardView cardFront, cardBack;
    ImageView imgWorldCard, flagImage;
    TextView txtCounter, txtFrontLabel, countryName, capital, greeting, funFact;
    Button btnSpeak, btnNext, btnPrev;
    ImageButton btnBack;

    TextToSpeech tts;
    int currentIndex = 0;
    boolean isFlipped = false;

    // ── Data — must match QuizActivity exactly ────────────────────────────────
    String[] countries = {
            "Turkey", "France", "Japan", "Germany", "Albania",
            "Italy", "Spain", "Greece", "South Korea", "Netherlands",
            "Poland", "Sweden", "Norway", "Brazil", "China"
    };

    String[] capitals = {
            "Ankara", "Paris", "Tokyo", "Berlin", "Tirana",
            "Rome", "Madrid", "Athens", "Seoul", "Amsterdam",
            "Warsaw", "Stockholm", "Oslo", "Brasília", "Beijing"
    };

    String[] greetings = {
            "Merhaba", "Bonjour", "Konnichiwa", "Hallo", "Përshëndetje",
            "Ciao", "Hola", "Yia sas", "Annyeonghaseyo", "Hallo",
            "Cześć", "Hej", "Hei", "Olá", "Nǐ hǎo"
    };

    String[] funFacts = {
            "Home to the only city spanning two continents: Istanbul!",
            "The Eiffel Tower grows ~15 cm taller in summer due to heat.",
            "Japan has over 6,800 islands!",
            "Germany has over 1,500 different types of beer.",
            "Albania has more bunkers per km² than any other country.",
            "Italy invented pizza, pasta, and the espresso machine.",
            "Spain has 47 UNESCO World Heritage Sites.",
            "Greece has over 2,000 islands, only 170 are inhabited.",
            "South Korea has one of the world's highest internet speeds!",
            "The Netherlands has more bicycles than people.",
            "Poland has the world's largest medieval castle at Malbork.",
            "Sweden invented the zipper, dynamite, and the pacemaker.",
            "Norway has the world's longest road tunnel at 24.5 km.",
            "Brazil is home to the Amazon, the world's largest rainforest.",
            "China has the world's longest high-speed rail network!"
    };

    int[] flags = {
            R.drawable.turkey,
            R.drawable.france,
            R.drawable.japan,
            R.drawable.germany,
            R.drawable.albania,
            R.drawable.italy,
            R.drawable.spain,
            R.drawable.greece,
            R.drawable.korea,
            R.drawable.netherlands,
            R.drawable.poland,
            R.drawable.sweden,
            R.drawable.norway,
            R.drawable.brazil,
            R.drawable.china
    };

    Locale[] locales = {
            new Locale("tr", "TR"),
            Locale.FRENCH,
            Locale.JAPANESE,
            Locale.GERMAN,
            new Locale("sq", "AL"),
            Locale.ITALIAN,
            new Locale("es", "ES"),
            new Locale("el", "GR"),
            new Locale("ko", "KR"),
            new Locale("nl", "NL"),
            new Locale("pl", "PL"),
            new Locale("sv", "SE"),
            new Locale("nb", "NO"),
            new Locale("pt", "BR"),
            Locale.CHINESE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_detail);

        cardFront     = findViewById(R.id.cardFront);
        cardBack      = findViewById(R.id.cardBack);
        imgWorldCard  = findViewById(R.id.imgWorldCard);
        flagImage     = findViewById(R.id.flagImage);
        txtCounter    = findViewById(R.id.txtCounter);
        txtFrontLabel = findViewById(R.id.txtFrontLabel);
        countryName   = findViewById(R.id.countryName);
        capital       = findViewById(R.id.capital);
        greeting      = findViewById(R.id.greeting);
        funFact       = findViewById(R.id.funFact);
        btnSpeak      = findViewById(R.id.btnSpeak);
        btnNext       = findViewById(R.id.btnNext);
        btnPrev       = findViewById(R.id.btnPrev);
        btnBack       = findViewById(R.id.btnBack);

        // Programmatic teal outline on the back card so it's visible on white bg
        applyCardOutline(cardBack, 0xFF2EC4B6, 3f);

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setSpeechRate(0.85f);
                tts.setPitch(1.05f);
            }
        });

        showCountry();

        btnBack.setOnClickListener(v -> finish());
        cardFront.setOnClickListener(v -> flipCard());
        cardBack.setOnClickListener(v -> flipCard());

        // ── Read soundEnabled from SharedPreferences before speaking ──────────
        btnSpeak.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("TravelLearnPrefs", MODE_PRIVATE);
            boolean soundEnabled = prefs.getBoolean("soundEnabled", true);
            if (soundEnabled) {
                speakGreeting();
            } else {
                Toast.makeText(this, "Sound is disabled in Settings", Toast.LENGTH_SHORT).show();
            }
        });

        btnNext.setOnClickListener(v -> {
            currentIndex = (currentIndex + 1) % countries.length;
            if (isFlipped) resetToFront();
            else showCountryAnimated();
        });

        btnPrev.setOnClickListener(v -> {
            currentIndex = (currentIndex - 1 + countries.length) % countries.length;
            if (isFlipped) resetToFront();
            else showCountryAnimated();
        });
    }

    /** Draws a rounded stroke outline on a CardView using setForeground */
    private void applyCardOutline(CardView card, int color, float strokeDpWidth) {
        float density = getResources().getDisplayMetrics().density;
        GradientDrawable outline = new GradientDrawable();
        outline.setShape(GradientDrawable.RECTANGLE);
        outline.setCornerRadius(24 * density);
        outline.setColor(0x00000000);
        outline.setStroke((int)(strokeDpWidth * density), color);
        card.setForeground(outline);
    }

    private void showCountry() {
        txtCounter.setText((currentIndex + 1) + " / " + countries.length);
        flagImage.setImageResource(flags[currentIndex]);
        countryName.setText(countries[currentIndex]);
        capital.setText("🏛️  Capital: " + capitals[currentIndex]);
        greeting.setText("👋  Greeting: " + greetings[currentIndex]);
        funFact.setText("💡  " + funFacts[currentIndex]);
    }

    private void showCountryAnimated() {
        cardFront.setAlpha(0f);
        cardFront.setTranslationX(60f);
        showCountry();
        cardFront.animate().alpha(1f).translationX(0f).setDuration(250)
                .setInterpolator(new OvershootInterpolator(1f)).start();
    }

    private void flipCard() {
        final View fromView = isFlipped ? cardBack  : cardFront;
        final View toView   = isFlipped ? cardFront : cardBack;

        ObjectAnimator foldOut = ObjectAnimator.ofFloat(fromView, "scaleX", 1f, 0f);
        foldOut.setDuration(180);
        foldOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                fromView.setVisibility(View.GONE);
                toView.setScaleX(0f);
                toView.setVisibility(View.VISIBLE);
                ObjectAnimator unfoldIn = ObjectAnimator.ofFloat(toView, "scaleX", 0f, 1f);
                unfoldIn.setDuration(200);
                unfoldIn.setInterpolator(new OvershootInterpolator(1.2f));
                unfoldIn.start();
            }
        });
        foldOut.start();
        isFlipped = !isFlipped;
    }

    private void resetToFront() {
        cardBack.setVisibility(View.GONE);
        cardFront.setVisibility(View.VISIBLE);
        cardFront.setScaleX(1f);
        isFlipped = false;
        showCountryAnimated();
    }

    private void speakGreeting() {
        if (tts == null) return;
        int result = tts.setLanguage(locales[currentIndex]);
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Toast.makeText(this, "Voice not available for this language", Toast.LENGTH_SHORT).show();
            tts.setLanguage(Locale.ENGLISH);
        }
        tts.speak(greetings[currentIndex], TextToSpeech.QUEUE_FLUSH, null, "greeting");
    }

    @Override
    protected void onDestroy() {
        if (tts != null) { tts.stop(); tts.shutdown(); }
        super.onDestroy();
    }
}