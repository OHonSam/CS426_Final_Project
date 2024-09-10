package com.hfad.cs426_final_project;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.LayoutInflater;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.hfad.cs426_final_project.MainScreen.MainScreenActivity;
import com.hfad.cs426_final_project.RankingScreen.RankingScreenActivity;
import com.hfad.cs426_final_project.StoreScreen.StoreScreenActivity;

import java.util.Objects;

public abstract class BaseScreenActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    protected DrawerLayout drawer;
    protected NavigationView navigationView;
    protected ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private ImageView toggleIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the common layout (which includes the toolbar, navigation, etc.)
        setContentView(R.layout.activity_base_screen);
        getUIReferences();

        loadChildActivityLayout(getLayoutId());

        setupToolbar();
        setupNavigationDrawer();
        setupCustomToggle();
    }

    private void loadChildActivityLayout(int layoutId) {
        // Use FrameLayout to dynamically load the child activity layout
        FrameLayout frameLayout = findViewById(R.id.activity_content);
        frameLayout.removeAllViews(); // optional

        // Inflate the child-specific layout into FrameLayout
        LayoutInflater.from(this).inflate(getLayoutId(), frameLayout);
    }

    private void getUIReferences() {
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout_base_screen);
        navigationView = findViewById(R.id.nav_view_screen_choices);
        toggleIcon = findViewById(R.id.toggle_icon);
    }

    protected abstract int getLayoutId();

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
    }

    private void setupNavigationDrawer() {
//        configureDrawerToggle();
        setNavigationViewSize();
        navigationView.setNavigationItemSelectedListener(this);
        highlightCurrentMenuItem();
    }

    private void highlightCurrentMenuItem() {
        int curLayoutId = getLayoutId();
        if (curLayoutId == R.layout.activity_main_screen) {
            navigationView.getMenu().findItem(R.id.nav_main_focus_screen).setChecked(true);
        } else if (curLayoutId == R.layout.activity_store_screen) {
            navigationView.getMenu().findItem(R.id.nav_store_screen).setChecked(true);
        }
    }

    private void setNavigationViewSize() {
        ViewGroup.LayoutParams params = navigationView.getLayoutParams();
        params.width = (int) (getResources().getDisplayMetrics().widthPixels / 2);
        navigationView.setLayoutParams(params);
    }

    private void configureDrawerToggle() {
        toggle = new ActionBarDrawerToggle(this,
                drawer,
                toolbar,
                R.string.nav_open_drawer,
                R.string.nav_close_drawer);

        // Set the color of the drawer toggle icon
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black));

        // Add the toggle to the DrawerLayout
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // Create an intent based on the selected item
        Intent intent = null;
        if (id == R.id.nav_store_screen) {
            intent = new Intent(this, StoreScreenActivity.class);
        } else if (id == R.id.nav_main_focus_screen) {
            intent = new Intent(this, MainScreenActivity.class);
        } else if (id == R.id.nav_ranking_screen) {
            intent = new Intent(this, RankingScreenActivity.class);
        } else if (id == R.id.nav_sign_out) {
            showSignOutDialog();
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        // If an intent is created, start the new activity
        if (intent != null) {
            drawer.closeDrawer(GravityCompat.START);
            startActivity(intent);
            finish(); // End the current activity
        }

        return true;
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

    private void setupCustomToggle() {
        toggleIcon.setOnClickListener(v -> {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        toggleIcon.bringToFront(); // avoid being overlapped

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                animateToggleIcon(slideOffset);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }

    private void animateToggleIcon(float slideOffset) {
        // Rotate the toggle icon from 0 to 180 degrees based on the slide offset
        float rotationAngle = slideOffset * 180; // Rotate between 0 (closed) and 180 (open)
        toggleIcon.setRotation(rotationAngle);
    }

    protected void setToggleColor(int color) {
        toggleIcon.setColorFilter(color);
    }
}

