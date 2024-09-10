package com.hfad.cs426_final_project;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;

import android.view.LayoutInflater;
import android.view.ViewStub;
import android.widget.FrameLayout;

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
import com.hfad.cs426_final_project.StoreScreen.StoreScreenActivity;

import java.util.Objects;

public abstract class BaseScreenActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    protected DrawerLayout drawer;
    protected NavigationView navigationView;
    protected ActionBarDrawerToggle toggle;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the common layout (which includes the toolbar, navigation, etc.)
        setContentView(R.layout.activity_base_screen);
        getUIReferences();

        loadChildActivityLayout(getLayoutId());

        setupToolbar();
        setupNavigationDrawer();
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
    }

    protected abstract int getLayoutId();

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
        toolbar.setBackgroundColor(Color.TRANSPARENT);
    }

    private void setupNavigationDrawer() {
        configureDrawerToggle();
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

    protected void updateDrawerToggle () {
        // bring toggle to front but not the rest of the base screen
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toolbar.setTitle("Ranking");
        toolbar.setBackgroundColor(getResources().getColor(R.color.blue_deep_sea));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setPopupTheme(R.style.AppTheme);

    }
}

