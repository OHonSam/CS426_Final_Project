package com.hfad.cs426_final_project;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.navigation.NavigationView;
import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;

import java.util.Objects;

public class MainScreenActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TextView timeView;
    MyButton startButton;

    ConstraintLayout popupMusicContainer;

    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        getUIReferences();

        setupToolbar();
        setupNavigationDrawer();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
    }

    private void setupNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer,
                toolbar,
                R.string.nav_open_drawer,
                R.string.nav_close_drawer);

        // Set the color of the drawer toggle icon
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black));

        // Add the toggle to the DrawerLayout
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        ViewGroup.LayoutParams params = navigationView.getLayoutParams();
        params.width = (int) (getResources().getDisplayMetrics().widthPixels / 2);
        navigationView.setLayoutParams(params);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void getUIReferences() {
        timeView = findViewById(R.id.time_view);
        startButton = findViewById(R.id.plant_button);
        popupMusicContainer = findViewById(R.id.main);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view_screen_choices);
        drawer = findViewById(R.id.drawer_layout_main_screen);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        Intent intent = null;

        if (id == R.id.nav_store_screen)
            intent = new Intent(this, StoreScreenActivity.class);
        else
            intent = new Intent(this, MainScreenActivity.class);

        startActivity(intent);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}