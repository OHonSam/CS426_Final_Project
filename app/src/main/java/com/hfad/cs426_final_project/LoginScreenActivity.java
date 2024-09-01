package com.hfad.cs426_final_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.SmartEditText.EmailEditText;
import com.hfad.cs426_final_project.SmartEditText.PasswordEditText;
import com.hfad.cs426_final_project.MainScreen.MainScreenActivity;

import java.util.Calendar;
import java.util.Date;

public class LoginScreenActivity extends AppCompatActivity {
    private AppContext appContext;
    private ClickableImageView btnBack;

    private EditText edtEmail;
    private EditText edtPassword;
    private MyButton btnLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        appContext = AppContext.getInstance();

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

                if (email.isEmpty() || password.isEmpty()) {
                    showToast("Authentication failed.");
                    return;
                }

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginScreenActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    updateUser();
                                    Intent intent = new Intent(LoginScreenActivity.this, MainScreenActivity.class);
                                    startActivity(intent);
                                    finishAffinity();
                                } else {
                                    showToast("Login failed.");
                                }
                            }
                        });
            }
        });
    }

    private void updateUser() {
        // Assuming the user data is stored under a "users" node in the database
        String email = edtEmail.getText().toString().trim();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Query to find the user by email
        databaseReference.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                User user = userSnapshot.getValue(User.class);
                                if (user != null) {
                                    appContext.setCurrentUser(user);
                                }
                            }
                        } else {
                            Toast.makeText(LoginScreenActivity.this, "User not found.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(LoginScreenActivity.this, "Database error: " + databaseError.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(LoginScreenActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
