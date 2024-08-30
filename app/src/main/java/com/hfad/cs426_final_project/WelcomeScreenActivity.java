package com.hfad.cs426_final_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hfad.cs426_final_project.CustomUIComponent.MyButton;

public class WelcomeScreenActivity extends AppCompatActivity {
    MyButton btnSignUp;
    MyButton btnLogin;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        initUI();
        initListener();
    }

    private void initUI() {
        btnSignUp = findViewById(R.id.signUpButton_welcomeScreen);
        btnLogin = findViewById(R.id.loginButton_welcomeScreen);
    }

    private void initListener() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SignUpScreenActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LoginScreenActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
