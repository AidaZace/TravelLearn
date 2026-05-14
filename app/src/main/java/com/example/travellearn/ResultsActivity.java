package com.example.travellearn;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ResultsActivity extends AppCompatActivity {

    TextView txtScore, txtMessage, txtScoreEmoji;
    Button btnPlayAgain, btnLeaderboard, btnShare, btnHome;
    CardView cardScore;
    FrameLayout confettiContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        txtScore        = findViewById(R.id.txtScore);
        txtMessage      = findViewById(R.id.txtMessage);
        txtScoreEmoji   = findViewById(R.id.txtScoreEmoji);
        btnPlayAgain    = findViewById(R.id.btnPlayAgain);
        btnLeaderboard  = findViewById(R.id.btnLeaderboard);
        btnShare        = findViewById(R.id.btnShare);
        btnHome         = findViewById(R.id.btnHome);
        cardScore       = findViewById(R.id.cardScore);
        confettiContainer = findViewById(R.id.confettiContainer);

        int score = getIntent().getIntExtra("score", 0);
        int total = getIntent().getIntExtra("total", 10);

        // Save to leaderboard history
        LeaderboardActivity.saveScore(this, score);

        txtScore.setText(score + " / " + total);

        if (score == total) {
            txtScoreEmoji.setText("🏆");
            txtMessage.setText("Perfect score! You're a world geography champion!");
        } else if (score >= (int)(total * 0.8)) {
            txtScoreEmoji.setText("🌟");
            txtMessage.setText("Excellent! You really know your countries!");
        } else if (score >= (int)(total * 0.6)) {
            txtScoreEmoji.setText("🌍");
            txtMessage.setText("Nice work! Keep exploring the world!");
        } else if (score >= (int)(total * 0.4)) {
            txtScoreEmoji.setText("🗺️");
            txtMessage.setText("Not bad! Every journey starts somewhere.");
        } else {
            txtScoreEmoji.setText("✈️");
            txtMessage.setText("Time to hit the books — or hit the road!");
        }

        // Entrance animation for score card
        cardScore.setScaleX(0f);
        cardScore.setScaleY(0f);
        cardScore.setAlpha(0f);
        cardScore.animate()
                .scaleX(1f).scaleY(1f).alpha(1f)
                .setDuration(500)
                .setInterpolator(new OvershootInterpolator(1.3f))
                .setStartDelay(100)
                .start();

        // Emoji bounce animation
        txtScoreEmoji.setScaleX(0f);
        txtScoreEmoji.setScaleY(0f);
        ObjectAnimator emojiAnim = ObjectAnimator.ofFloat(txtScoreEmoji, "scaleX", 0f, 1.3f, 1f);
        emojiAnim.setDuration(600);
        emojiAnim.setStartDelay(350);
        emojiAnim.start();
        ObjectAnimator emojiAnimY = ObjectAnimator.ofFloat(txtScoreEmoji, "scaleY", 0f, 1.3f, 1f);
        emojiAnimY.setDuration(600);
        emojiAnimY.setStartDelay(350);
        emojiAnimY.start();

        // Launch confetti if score is decent (>= 5)
        if (score >= 5) {
            new Handler().postDelayed(() -> launchConfetti(score, total), 600);
        }

        // Add hover effects to buttons
        addHoverEffect(btnPlayAgain);
        addHoverEffect(btnLeaderboard);
        addHoverEffect(btnShare);
        addHoverEffect(btnHome);

        btnPlayAgain.setOnClickListener(v -> {
            startActivity(new Intent(this, QuizActivity.class));
            finish();
        });

        btnLeaderboard.setOnClickListener(v -> {
            startActivity(new Intent(this, LeaderboardActivity.class));
            finish();
        });

        btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My TravelLearn Score");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "I scored " + score + "/" + total + " on TravelLearn! Can you beat me? 🌍");
            startActivity(Intent.createChooser(shareIntent, "Share your score"));
        });

        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private void addHoverEffect(View v) {
        v.setOnTouchListener((view, event) -> {
            int action = event.getAction();
            if (action == android.view.MotionEvent.ACTION_DOWN) {
                view.animate().scaleX(0.96f).scaleY(0.96f).setDuration(80).start();
            } else if (action == android.view.MotionEvent.ACTION_UP
                    || action == android.view.MotionEvent.ACTION_CANCEL) {
                view.animate().scaleX(1f).scaleY(1f).setDuration(120)
                        .setInterpolator(new OvershootInterpolator(2f)).start();
                if (action == android.view.MotionEvent.ACTION_UP) {
                    view.performClick();
                }
            }
            return true;
        });
    }

    /** Simple confetti shower using a custom View drawn with Canvas */
    private void launchConfetti(int score, int total) {
        ConfettiView confetti = new ConfettiView(this, score, total);
        confettiContainer.addView(confetti);
        confetti.start();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Inner confetti view
    // ──────────────────────────────────────────────────────────────────────────

    static class ConfettiView extends View {

        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final List<Piece> pieces = new ArrayList<>();
        private final Random rng = new Random();
        private long startTime;
        private static final long DURATION = 3000L;

        private final int[] COLORS = {
                Color.parseColor("#FF6B35"), // orange
                Color.parseColor("#FFD166"), // yellow
                Color.parseColor("#06D6A0"), // green
                Color.parseColor("#2EC4B6"), // teal
                Color.parseColor("#EF476F"), // pink
                Color.parseColor("#FFFFFF")  // white
        };

        ConfettiView(android.content.Context ctx, int score, int total) {
            super(ctx);
            int count = 40 + score * 6; // more confetti for higher scores
            setClickable(false);
        }

        void start() {
            startTime = System.currentTimeMillis();

            // Spawn particles from top
            for (int i = 0; i < 80; i++) {
                pieces.add(new Piece(rng, COLORS));
            }

            ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
            animator.setDuration(DURATION);
            animator.addUpdateListener(a -> invalidate());
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    setVisibility(GONE);
                }
            });
            animator.start();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            long elapsed = System.currentTimeMillis() - startTime;
            float t = Math.min(elapsed / (float) DURATION, 1f);

            for (Piece p : pieces) {
                float x = p.startX * getWidth();
                float y = p.startY * getHeight() + t * p.speed * getHeight();
                float alpha = t < 0.7f ? 255 : (int)(255 * (1f - (t - 0.7f) / 0.3f));

                paint.setColor(p.color);
                paint.setAlpha((int) Math.max(0, alpha));

                canvas.save();
                canvas.rotate(p.rotation + t * p.rotSpeed * 360f, x, y);
                canvas.drawRect(x - p.size / 2, y - p.size / 2, x + p.size / 2, y + p.size, paint);
                canvas.restore();
            }
        }

        static class Piece {
            float startX, startY, speed, size, rotation, rotSpeed;
            int color;

            Piece(Random rng, int[] colors) {
                startX    = rng.nextFloat();
                startY    = -0.1f - rng.nextFloat() * 0.4f;
                speed     = 0.4f + rng.nextFloat() * 0.6f;
                size      = 16 + rng.nextFloat() * 14;
                rotation  = rng.nextFloat() * 360f;
                rotSpeed  = (rng.nextBoolean() ? 1 : -1) * (0.5f + rng.nextFloat());
                color     = colors[rng.nextInt(colors.length)];
            }
        }
    }
}