package com.hfad.cs426_final_project.ProfileScreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hfad.cs426_final_project.BaseScreenActivity;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.R;
import com.hfad.cs426_final_project.WelcomeScreenActivity;

public class ProfileScreenActivity extends BaseScreenActivity {
    private ImageView ivProfileImg;
    private TextView tvName, tvEmail;
    private MyButton btnProfileDetails, btnChangePassword, btnLogout;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_profile_screen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUI();
        setupUserInformation();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupUserInformation();
    }

    private void initUI() {
        ivProfileImg = findViewById(R.id.profileImg);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvMail);
        btnProfileDetails = findViewById(R.id.btnProfileDetails);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogOut);
    }

    private void setupUserInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
            return;
        }
        tvName.setText(user.getDisplayName());
        tvEmail.setText(user.getEmail());
        Glide.with(this)
                .load(user.getPhotoUrl())
                .error(R.drawable.default_avatar)
                .into(ivProfileImg);
    }

    private void initListener() {
        btnProfileDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileScreenActivity.this, ProfileDetailsScreenActivity.class);
                startActivity(intent);
            }
        });
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileScreenActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignOutDialog();
            }
        });
    }

    private void showSignOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sign Out");
        builder.setMessage("Are you sure you want to sign out?");
        builder.setPositiveButton("OK", (dialog, which) -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, WelcomeScreenActivity.class);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}