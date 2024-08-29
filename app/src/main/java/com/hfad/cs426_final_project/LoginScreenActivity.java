package com.hfad.cs426_final_project;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hfad.cs426_final_project.CustomUIComponent.MyButton;

public class LoginScreenActivity extends AppCompatActivity {
    private EditText edtEmail;
    private EditText edtPassword;
    private MyButton btnLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        initUI();
        initListener();
    }

    private void initUI() {
        edtEmail = findViewById(R.id.emailEditText_loginScreen);
        edtPassword = findViewById(R.id.passwordEditText_loginScreen);
        btnLogin = findViewById(R.id.loginButton_loginScreen);
    }

    private void initListener() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
