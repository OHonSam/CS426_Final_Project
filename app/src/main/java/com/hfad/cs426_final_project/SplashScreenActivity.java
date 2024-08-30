package com.hfad.cs426_final_project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                nextActivity();
            }
        }, 2000);
    }
    private void nextActivity() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUser.getIdToken(true).addOnCompleteListener(task -> {
                Intent intent;
                if (task.isSuccessful()) {
                    // Token is valid, proceed to the Main Screen
                    intent = new Intent(this, MainScreenActivity.class);
                } else {
                    // Token is invalid, user might have been deleted, redirect to Welcome Screen
                    intent = new Intent(this, WelcomeScreenActivity.class);
                }
                startActivity(intent);
                finish(); // Prevent the user from returning to this screen
            });
        } else {
            // No user is signed in, redirect to Welcome Screen
            Intent intent = new Intent(this, WelcomeScreenActivity.class);
            startActivity(intent);
            finish(); // Prevent the user from returning to this screen
        }
    }
}