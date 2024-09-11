package com.hfad.cs426_final_project;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CongratulationScreenActivity extends AppCompatActivity {
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congratulation_screen);

        TextView congratulationText = findViewById(R.id.congratulation_text);
        TextView congratulationMessage = findViewById(R.id.congratulation_message);
        Button backButton = findViewById(R.id.back_to_home_button);

        congratulationText.setVisibility(View.INVISIBLE);
        congratulationMessage.setVisibility(View.INVISIBLE);

        // Delay showing the TextViews by 1 second
        handler.postDelayed(() -> {
            AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
            alphaAnimation.setDuration(1000); // 1 second

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(
                    ObjectAnimator.ofFloat(congratulationText, "alpha", 0f, 1f),
                    ObjectAnimator.ofFloat(congratulationMessage, "alpha", 0f, 1f)
            );
            animatorSet.setDuration(1000);

            animatorSet.start();
            congratulationText.setVisibility(View.VISIBLE);
            congratulationMessage.setVisibility(View.VISIBLE);
        }, 2000);
        backButton.setOnClickListener(v -> finish());
    }
}