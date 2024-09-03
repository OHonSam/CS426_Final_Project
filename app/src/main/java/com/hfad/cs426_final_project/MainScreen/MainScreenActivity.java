package com.hfad.cs426_final_project.MainScreen;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.CongratulationScreenActivity;
import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.MainScreen.Clock.Clock;
import com.hfad.cs426_final_project.DataStorage.Tag;
import com.hfad.cs426_final_project.MainScreen.Music.MusicAdapter;
import com.hfad.cs426_final_project.MainScreen.Music.MusicItem;
import com.hfad.cs426_final_project.MainScreen.Music.MusicManager;
import com.hfad.cs426_final_project.MainScreen.Clock.ModePickerDialog;
import com.hfad.cs426_final_project.R;
import com.hfad.cs426_final_project.StoreScreen.StoreScreenActivity;
import com.hfad.cs426_final_project.WelcomeScreenActivity;

import java.util.List;
import java.util.Objects;

public class MainScreenActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private AppContext appContext;
    private ImageView imgTree;
    TextView timeView;
    MyButton startButton, todoButton, musicButton, newTagButton, clockMode;
    ClickableImageView todoImage, musicImage, newTagImage;
    LinearLayout todoContainer, musicContainer, newTagContainer;
    Clock clock;

    ModePickerDialog modePickerDialog;

    ConstraintLayout popupMusicContainer;

    private MusicManager musicManager;

    Spinner searchTagSpinner;

    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawer;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        getUIReferences();

        appContext = AppContext.getInstance();

        setupToolbar();
        setupNavigationDrawer();

        musicManager = new MusicManager(this, musicImage);


        setupSearchTag();
        setupMusicListener();
        setupTodoListener();
        setupNewTagListener();

        setupClockMode();
        setupClock();

        setupStartButton();
        setupTree();
    }

    private void setupClockMode() {
        clockMode = findViewById(R.id.clockMode);
        clockMode.setOnClickListener(v -> {
            showModePickerDialog();
        });
    }

    private void showModePickerDialog() {
        if (getSupportFragmentManager().findFragmentByTag(ModePickerDialog.TAG) == null)
            modePickerDialog.show(getSupportFragmentManager(), ModePickerDialog.TAG);
//        modePickerDialog.setOnDismissListener(new ModePickerDialog.onDismissListener() {
//            @Override
//            public void onDismiss(ModePickerDialog modePickerDialog) {
//                //clock.updateSetting(modePickerDialog.);
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        clock.disableDeepModeCount();
    }

    @Override
    protected void onPause() {
        super.onPause();
        clock.enableDeepModeCount();
        Log.d("ClockOutside", "OnPause invoked");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        appContext.saveUserInfo();
    }

    private void getUIReferences() {
        imgTree = findViewById(R.id.tree);
        timeView = findViewById(R.id.time_view);
        startButton = findViewById(R.id.plant_button);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view_screen_choices);
        drawer = findViewById(R.id.drawer_layout_main_screen);

        todoImage = findViewById(R.id.todo_image);
        todoButton = findViewById(R.id.todo_button);
        todoContainer = findViewById(R.id.todo_container);
        musicContainer = findViewById(R.id.music_container);
        musicImage =findViewById(R.id.music_image);
        musicButton=findViewById(R.id.music_button);
        newTagImage = findViewById(R.id.new_tag_image);
        newTagButton = findViewById(R.id.new_tag_button);
        newTagContainer = findViewById(R.id.new_tag_container);

        popupMusicContainer = findViewById(R.id.main);
        modePickerDialog = new ModePickerDialog();
        searchTagSpinner = findViewById(R.id.search_tag_spinner);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        Intent intent = new Intent(this, MainScreenActivity.class);

        if (id == R.id.nav_store_screen)
            intent = new Intent(this, StoreScreenActivity.class);
        else if (id == R.id.nav_sign_out) {
            showSignOutDialog();
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        startActivity(intent);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showSignOutDialog() {
        // Create an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the dialog title and message
        builder.setTitle("Sign Out");
        builder.setMessage("Are you sure you want to sign out?");

        // Set the "OK" button with a click listener
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Sign out logic here
                Intent intent = new Intent(MainScreenActivity.this, WelcomeScreenActivity.class);
                startActivity(intent);
                finish();

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
            }
        });

        // Set the "Cancel" button with a click listener
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

        View.OnLongClickListener musicLongClickListener = v -> {
            showMusicSelectionPopup(); // Call your method to show the popup
            return true; // Return true to indicate that the long click event was handled
        };

        View.OnClickListener musicClickListener = v -> musicManager.toggleOnOff();

        // Set the listener to the views
        musicContainer.setOnTouchListener(musicTouchListener);
        musicButton.setOnTouchListener(musicTouchListener);
        musicImage.setOnTouchListener(musicTouchListener);

        // Set the long click listener to open the music selection popup
        musicContainer.setOnLongClickListener(musicLongClickListener);
        musicButton.setOnLongClickListener(musicLongClickListener);
        musicImage.setOnLongClickListener(musicLongClickListener);

        // Set the click listener to toggle the music on/off
        musicContainer.setOnClickListener(musicClickListener);
        musicButton.setOnClickListener(musicClickListener);
        musicImage.setOnClickListener(musicClickListener);
    }

    private void showMusicSelectionPopup() {
        // Inflate the popup layout
        View popupView = getLayoutInflater().inflate(R.layout.popup_music_selection, null);

        // RecyclerView from the inflated popupView
        RecyclerView recyclerView = popupView.findViewById(R.id.recycler_view_music);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<MusicItem> musicList = MusicItem.getMusicList();
        MusicAdapter adapter = new MusicAdapter(this, musicList, musicManager.loadSavedMusicSelection(), musicItem -> {
            // Handle music item click (e.g., switch music)
            musicManager.switchMusic(musicItem.getFileResourceId());
        });
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
        recyclerView.setClickable(true);
        recyclerView.scrollToPosition(adapter.getPositionByMusicResId(musicManager.loadSavedMusicSelection()));

        // Calculate portion of the screen to show the popup
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels / 1.5);
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
                musicManager.saveMusicSelection();
            }
        });

        // Show the popup at the center of the container view
        popupWindow.showAtLocation(musicContainer, Gravity.CENTER, 0, 0);
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

    private void setupSearchTag() {
        List<Tag> tagList = appContext.getCurrentUser().getOwnTags();
        String[] tagNames = new String[tagList.size()];

        for (int i = 0; i < tagList.size(); i++) {
            tagNames[i] = tagList.get(i).getName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item, // Layout for each item in the dropdown
                tagNames // The array of tag names
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchTagSpinner.setAdapter(adapter);

        searchTagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected item
                String selectedItem = parentView.getItemAtPosition(position).toString();
                // Do something with the selected item

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do something if nothing is selected
            }
        });

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

    private void setupClock() {
        clock = new Clock(this, timeView, startButton, appContext.getCurrentUser().getClockSetting(), 0, 5);
        appContext.setCurrentClock(clock);
        appContext.getCurrentClock().run();
    }

    private void setupStartButton() {
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the clock is not running then start it
                if (clock.getClockSetting().getIsCountExceedTime() && clock.getIsEndSession()) {
                    clock.setIsEndSession(false);
                    redirectToCongratulationScreen();
                    clock.reset();
                }

                if (!clock.isRunning()) {
                    clock.start();
                } else {
                    clock.stop();
                }

            }
        });
    }

    public void redirectToCongratulationScreen() {
        Intent intent = new Intent(this, CongratulationScreenActivity.class);
        startActivity(intent);
    }

    private void setupNewTagListener() {
        View.OnTouchListener newTagTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean isPressed = (event.getAction() == MotionEvent.ACTION_DOWN);

                // Handle pressed state for musicImage if it's the musicButton or musicImage
                newTagImage.setPressed(isPressed);
                newTagButton.onTouchEvent(event); // Pass the event to musicButton

                return true; // Indicate the touch was handled
            }
        };
        View.OnClickListener tagClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTagDialog();
            }
        };
        // Set the listener to the views
        newTagContainer.setOnTouchListener(newTagTouchListener);
        newTagImage.setOnTouchListener(newTagTouchListener);
        newTagButton.setOnTouchListener(newTagTouchListener);

        newTagContainer.setOnClickListener(tagClickListener);
        newTagImage.setOnClickListener(tagClickListener);
        newTagButton.setOnClickListener(tagClickListener);
    }

    private void showAddTagDialog() {
        // Inflate the dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_tag, null);

        // Initialize the dialog components
        EditText etTagName = dialogView.findViewById(R.id.etTagName);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnAdd = dialogView.findViewById(R.id.btnAdd);

        // Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();

        // Set click listeners for the buttons
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnAdd.setOnClickListener(v -> {
            String tagName = etTagName.getText().toString().trim();
            if (!tagName.isEmpty()) {
                if(!appContext.getCurrentUser().hasTag(tagName)) {
                    // Add the tag logic
                    Tag newTag = new Tag(appContext.getCurrentUser().getOwnTags().size(), tagName);
                    appContext.getCurrentUser().getOwnTags().add(newTag);
                    setupSearchTag();
                    // For example: addTag(tagName);
                    Toast.makeText(this, "Tag added: " + tagName, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                else {
                    Toast.makeText(this, "This tag has existed", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter a tag name", Toast.LENGTH_SHORT).show();
            }
        });

        // Show the dialog
        dialog.show();
    }

    private void setupTree() {
        Uri treeURI = Uri.parse(appContext.getCurrentUser().getUserSetting().getSelectedTree().getImgUri());
        Glide.with(this)
                .load(treeURI)
                .override(Target.SIZE_ORIGINAL) // or specify your desired dimensions
                .fitCenter() // Or use centerCrop() if you want to crop the image to fit
                .into(imgTree);
    }

}