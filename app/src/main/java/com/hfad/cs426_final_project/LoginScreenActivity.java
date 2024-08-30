package com.hfad.cs426_final_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.SmartEditText.EmailEditText;
import com.hfad.cs426_final_project.SmartEditText.PasswordEditText;
import com.hfad.cs426_final_project.MainScreen.MainScreenActivity;

public class LoginScreenActivity extends AppCompatActivity {
    private ClickableImageView btnBack;

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
        btnBack = findViewById(R.id.backButton_loginScreen);
        EmailEditText emailEditText = findViewById(R.id.emailEditText_loginScreen);
        edtEmail = emailEditText.getEditText();
        PasswordEditText passwordEditText = findViewById(R.id.passwordEditText_loginScreen);
        edtPassword = passwordEditText.getEditText();
        btnLogin = findViewById(R.id.loginButton_loginScreen);
    }

    private void initListener() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginScreenActivity.this, WelcomeScreenActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                if(email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginScreenActivity.this, "Login failed.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginScreenActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(LoginScreenActivity.this, MainScreenActivity.class);
                                    startActivity(intent);
                                    finishAffinity();
                                } else {
                                    Toast.makeText(LoginScreenActivity.this, "Login failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
