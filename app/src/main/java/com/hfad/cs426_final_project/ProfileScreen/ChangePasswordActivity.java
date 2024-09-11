package com.hfad.cs426_final_project.ProfileScreen;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.R;
import com.hfad.cs426_final_project.SmartEditText.EmailEditText;
import com.hfad.cs426_final_project.SmartEditText.PasswordEditText;

public class ChangePasswordActivity extends AppCompatActivity {
    private ImageView btnBack;
    private EditText edtNewPassword, edtConfirmPassword;
    private MyButton btnChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initUI();
        initListener();
    }

    private void initUI() {
        btnBack = findViewById(R.id.btnBack);
        PasswordEditText newPasswordEditText = findViewById(R.id.edtNewPassword);
        edtNewPassword = newPasswordEditText.getEditText();
        PasswordEditText confirmPasswordEditText = findViewById(R.id.edtConfirmPassword);
        edtConfirmPassword = confirmPasswordEditText.getEditText();
        btnChangePassword = findViewById(R.id.btnChangePassword);
    }

    private void initListener() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPass = edtNewPassword.getText().toString().trim();
                String confirmPass = edtConfirmPassword.getText().toString().trim();
                if(newPass.length() >= 6 && confirmPass.length() >= 6 && newPass.equals(confirmPass)) {
                    changePassword();
                }
            }
        });
    }

    private void changePassword() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
            return;
        }
        String newPass = edtNewPassword.getText().toString().trim();

        user.updatePassword(newPass)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ChangePasswordActivity.this, "Change Password success", Toast.LENGTH_SHORT).show();
                        } else {
                            // show dialog to re-authenticate
                            showReAuthenticateDialog();
                        }
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
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorAccent));
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
                            changePassword();
                        } else {
                            Toast.makeText(ChangePasswordActivity.this, "Re-authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}