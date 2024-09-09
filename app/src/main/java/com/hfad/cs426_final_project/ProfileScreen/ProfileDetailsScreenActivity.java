package com.hfad.cs426_final_project.ProfileScreen;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.LoginScreenActivity;
import com.hfad.cs426_final_project.MainScreen.MainScreenActivity;
import com.hfad.cs426_final_project.R;
import com.hfad.cs426_final_project.SmartEditText.EmailEditText;
import com.hfad.cs426_final_project.SmartEditText.PasswordEditText;
import com.hfad.cs426_final_project.User;
import com.hfad.cs426_final_project.WelcomeScreenActivity;

public class ProfileDetailsScreenActivity extends AppCompatActivity {
    public static final int MY_REQUEST_CODE = 10;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            if(o.getResultCode() == RESULT_OK) {
                Intent intent = o.getData();
                if(intent == null) {
                    return;
                }
                mUri = intent.getData();
                setupUserImg(mUri);
            }
        }
    });

    private Uri mUri;
    private MyButton btnSaveChanges;
    private ImageView btnBack, profileImg, editImgButton;
    private EditText edtName, edtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details_screen);

        initUI();
        setupUserInformation();
        initListener();
    }

    private void initUI() {
        btnBack = findViewById(R.id.btnBack);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        profileImg = findViewById(R.id.profileImg);
        editImgButton = findViewById(R.id.editButton);
        edtName = findViewById(R.id.edtName);
        EmailEditText emailEditText = findViewById(R.id.edtEmail);
        edtEmail = emailEditText.getEditText();
    }

    private void setupUserInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
            return;
        }
        edtName.setText(user.getDisplayName());
        edtEmail.setText(user.getEmail());
        setupUserImg(user.getPhotoUrl());
    }

    private void setupUserImg(Uri uri) {
        Glide.with(this)
                .load(uri)
                .error(R.drawable.default_avatar)
                .into(profileImg);
    }

    private void setBitmapImgProfile(Bitmap bitmap) {
        profileImg.setImageBitmap(bitmap);
    }

    private void initListener() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRequestPermission();
            }
        });

        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserEmail();
            }
        });
    }

    private void onClickRequestPermission() {
        if(this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        }
        else {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            this.requestPermissions(permissions, MY_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
            else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private void updateUserProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
            return;
        }

        String name = edtName.getText().toString().trim();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(mUri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileDetailsScreenActivity.this, "Update profile success", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUserEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }

        String oldEmail = user.getEmail();
        String newEmail = edtEmail.getText().toString().trim();

        // Send a verification email before updating the email
        user.verifyBeforeUpdateEmail(newEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            updateUserInDB(oldEmail, newEmail);
                            updateUserProfile();

                            if(!oldEmail.equals(newEmail)){
                                // Notify the user to check their email for verification
                                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileDetailsScreenActivity.this);
                                builder.setTitle("Verification email sent");
                                builder.setMessage("Please verify the new email before updating.");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(ProfileDetailsScreenActivity.this, MainScreenActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                builder.show();
                            }
                        } else {
                            // Handle errors (e.g., re-authentication required or other errors)
                            if (task.getException() instanceof FirebaseAuthRecentLoginRequiredException) {
                                showReAuthenticateDialog();
                            } else {
                                Toast.makeText(ProfileDetailsScreenActivity.this, "Failed to send verification email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }


    private void updateUserInDB(String oldEmail, String newEmail) {
        // Reference to Firebase Realtime Database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");

        // Query the user by the old email
        userRef.orderByChild("email").equalTo(oldEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    // Update the email
                    userSnapshot.getRef().child("email").setValue(newEmail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileDetailsScreenActivity.this, "Database update failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showReAuthenticateDialog() {
        // Inflate the custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_reauthenticate, null);

        // Reference to the email and password EditText
        EmailEditText emailEditText = dialogView.findViewById(R.id.edtEmail);
        EditText inputEmail = emailEditText.getEditText();
        PasswordEditText passwordEditText = dialogView.findViewById(R.id.edtPassword);
        EditText inputPassword = passwordEditText.getEditText();

        // Create a custom dialog using the custom layout
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setTitle("Re-authenticate");

        // Set up the buttons
        builder.setPositiveButton("Authenticate", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                reAuthenticate(email, password);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Customize dialog button colors after it is shown
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.primary_80));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.primary_20));
        });

        dialog.show();
    }


    private void reAuthenticate(String email, String password) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
            return;
        }
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            updateUserEmail();
                        } else {
                            Toast.makeText(ProfileDetailsScreenActivity.this, "Re-authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}