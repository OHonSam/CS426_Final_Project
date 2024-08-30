package com.hfad.cs426_final_project;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.SmartEditText.EmailEditText;
import com.hfad.cs426_final_project.SmartEditText.PasswordEditText;

public class SignUpScreenActivity extends AppCompatActivity {
    private ClickableImageView btnBack;

    private EditText edtFullName;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtConfirmPassword;
    private MyButton btnSignUp;
    private TextView tvSignIn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        initUI();
        initListener();
    }

    private void initUI() {
        btnBack = findViewById(R.id.backButton_signUpScreen);

        edtFullName = findViewById(R.id.fullNameEditText_signUpScreen);

        EmailEditText emailEditText = findViewById(R.id.emailEditText_signUpScreen);
        edtEmail = emailEditText.getEditText();

        PasswordEditText passwordEditText = findViewById(R.id.passwordEditText_signUpScreen);
        edtPassword = passwordEditText.getEditText();

        PasswordEditText confirmPasswordEditText = findViewById(R.id.confirmEditText_signUpScreen);
        edtConfirmPassword = confirmPasswordEditText.getEditText();

        btnSignUp = findViewById(R.id.signUpButton_signUpScreen);
        tvSignIn = findViewById(R.id.signInTextView_signUpScreen);
    }

    private void initListener() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpScreenActivity.this, WelcomeScreenActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtFullName.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String confirmPassword = edtConfirmPassword.getText().toString().trim();
                if(email.isEmpty() || password.isEmpty() || name.isEmpty() || confirmPassword.isEmpty() || !password.equals(confirmPassword)) {
                    Toast.makeText(SignUpScreenActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpScreenActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(SignUpScreenActivity.this, MainScreenActivity.class);
                                    startActivity(intent);
                                    finishAffinity();
                                } else {
                                    Toast.makeText(SignUpScreenActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpScreenActivity.this, LoginScreenActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}

