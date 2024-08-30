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
    //Number of seconds displayed on the stopwatch.
    private int seconds = 0;
    //Is the stopwatch running?
    private boolean running;

    TextView timeView;
    MyButton startButton, todoButton, musicButton;
    ClickableImageView todoImage, musicImage;
    LinearLayout todoContainer, musicContainer;

    ConstraintLayout popupMusicContainer;

    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        getUIReferences();

        setupMusicListener();
        setupTodoListener();

        setupToolbar();
        setupNavigationDrawer();
    }
    
    private void showMusicSelectionPopup() {
        // Inflate the popup layout
        View popupView = getLayoutInflater().inflate(R.layout.popup_music_selection, null);

        // Calculate portion of the screen to show the popup
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels / 2);
        int height = (int) (displayMetrics.heightPixels / 2);

        // Create the PopupWindow
        PopupWindow popupWindow = new PopupWindow(popupView,
                width,
                height,
                true);

        // Dim the background for the rest except the popup window
        applyDim(popupMusicContainer, 0.5f);

        // Return to the normal background for the rest if the popup window is dismissed
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                clearDim(popupMusicContainer);
            }
        });

        // Show the popup at the center of the container view
        popupWindow.showAtLocation(musicContainer, Gravity.CENTER, 0, 0);

        // Selecting music and play it
    }

    public static void applyDim(@NonNull ViewGroup parent, float dimAmount) {
        Drawable dim = new ColorDrawable(Color.BLACK);
        dim.setBounds(0, 0, parent.getWidth(), parent.getHeight());
        dim.setAlpha((int) (255 * dimAmount));

        ViewGroupOverlay overlay = parent.getOverlay();
        overlay.add(dim);
    }

    public static void clearDim(@NonNull ViewGroup parent) {
        ViewGroupOverlay overlay = parent.getOverlay();
        overlay.clear();
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

    @SuppressLint("ClickableViewAccessibility")
    private void setupMusicListener() {
        View.OnTouchListener musicTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean isPressed = (event.getAction() == MotionEvent.ACTION_DOWN);

                // Handle pressed state for musicImage if it's the musicButton or musicImage
                musicImage.setPressed(isPressed);
                musicButton.onTouchEvent(event); // Pass the event to musicButton

                return true; // Indicate the touch was handled
            }
        };

        View.OnClickListener musicClickListener = v -> showMusicSelectionPopup();

        // Set the listener to the views
        musicContainer.setOnTouchListener(musicTouchListener);
        musicButton.setOnTouchListener(musicTouchListener);
        musicImage.setOnTouchListener(musicTouchListener);

        // Set the click listener to open the music selection popup
        musicContainer.setOnClickListener(musicClickListener);
        musicButton.setOnClickListener(musicClickListener);
        musicImage.setOnClickListener(musicClickListener);

    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupTodoListener() {
        View.OnTouchListener todoTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean isPressed = (event.getAction() == MotionEvent.ACTION_DOWN);

                // Handle pressed state for musicImage if it's the musicButton or musicImage
                todoImage.setPressed(isPressed);
                todoButton.onTouchEvent(event); // Pass the event to musicButton

                return true; // Indicate the touch was handled
            }
        };

        // Set the listener to the views
        todoContainer.setOnTouchListener(todoTouchListener);
        todoButton.setOnTouchListener(todoTouchListener);
        todoImage.setOnTouchListener(todoTouchListener);
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