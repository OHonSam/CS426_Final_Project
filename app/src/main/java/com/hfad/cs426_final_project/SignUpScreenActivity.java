package com.hfad.cs426_final_project;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hfad.cs426_final_project.CustomUIComponent.MyButton;

public class SignUpScreenActivity extends AppCompatActivity {
    private EditText edtFullName;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtConfirmPassword;
    private MyButton btnSignUp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        initUI();
        initListener();
    }

    private void initUI() {
        edtFullName = findViewById(R.id.fullNameEditText_signUpScreen);
        edtEmail = findViewById(R.id.emailEditText_signUpScreen);
        edtPassword = findViewById(R.id.passwordEditText_signUpScreen);
        edtConfirmPassword = findViewById(R.id.confirmEditText_signUpScreen);
        btnSignUp = findViewById(R.id.signUpButton_signUpScreen);
    }

    private void initListener() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}

