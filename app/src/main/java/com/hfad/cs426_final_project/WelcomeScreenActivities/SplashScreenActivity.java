package com.hfad.cs426_final_project.WelcomeScreenActivities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.MainScreen.MainScreenActivity;
import com.hfad.cs426_final_project.R;
import com.hfad.cs426_final_project.DataStorage.User;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {
    private AppContext appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        appContext = AppContext.getInstance();
        appContext.loadData().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    checkCurrentUser();
                }
            }
        });
    }

    private void checkCurrentUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            currentUser.getIdToken(true).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Token is valid, proceed to load the user data and move to MainAppScreen
                    loadUserDataAndGoToMainApp(currentUser.getEmail());
                } else {
                    // Token is invalid, user might have been deleted, redirect to Welcome Screen
                    redirectToWelcomeScreen();
                }
            });
        } else {
            // No user is signed in, redirect to Welcome Screen
            redirectToWelcomeScreen();
        }
    }

    private void loadUserDataAndGoToMainApp(String email) {
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
                                    redirectToMainScreen();  // Start MainScreenActivity after user data is set
                                    return;
                                }
                            }
                        } else {
                            Toast.makeText(SplashScreenActivity.this, "User not found.",
                                    Toast.LENGTH_SHORT).show();
                            redirectToWelcomeScreen();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(SplashScreenActivity.this, "Database error: " + databaseError.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        redirectToWelcomeScreen();
                    }
                });
    }

    private void redirectToMainScreen() {
        Intent intent = new Intent(this, MainScreenActivity.class);
        startActivity(intent);
        finish(); // Prevent the user from returning to this screen
    }

    private void redirectToWelcomeScreen() {
        Intent intent = new Intent(this, WelcomeScreenActivity.class);
        startActivity(intent);
        finish(); // Prevent the user from returning to this screen
    }
}
