package com.hfad.cs426_final_project.MainScreen;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.BaseScreenActivity;
import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.FailScreenActivity;
import com.hfad.cs426_final_project.MainScreen.Clock.Clock;
import com.hfad.cs426_final_project.DataStorage.Tag;
import com.hfad.cs426_final_project.MainScreen.BottomSheet.BottomSheetMainScreen;
import com.hfad.cs426_final_project.MainScreen.Clock.OnClockListener;
import com.hfad.cs426_final_project.MainScreen.Music.MusicAdapter;
import com.hfad.cs426_final_project.MainScreen.Music.MusicItem;
import com.hfad.cs426_final_project.MainScreen.Music.MusicManager;
import com.hfad.cs426_final_project.MainScreen.Clock.ModePickerDialog;
import com.hfad.cs426_final_project.R;

import java.util.List;

import me.tankery.lib.circularseekbar.CircularSeekBar;

public class MainScreenActivity extends BaseScreenActivity implements OnClockListener {
    private AppContext appContext;
    private ImageView imgTree;
    private TextView timeView;
    private MyButton startButton, todoButton, musicButton, newTagButton, btnClockModePicker;
    private ClickableImageView todoImage, musicImage, newTagImage;
    private LinearLayout todoContainer, musicContainer, newTagContainer;
    private DrawerLayout popupMusicContainer;

    private Clock clock;
    private ModePickerDialog modePickerDialog;
    private MusicManager musicManager;
    private Spinner searchTagSpinner;

    BottomSheetMainScreen bottomSheet;

    private CircularSeekBar progressBar;
    private ActivityResultLauncher<Intent> failScreenLauncher;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_screen;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUIReferences();

        appContext = AppContext.getInstance();
        musicManager = new MusicManager(this, musicImage);

        setupSearchTag();
        setupMusicListener();
        setupTodoListener();
        setupNewTagListener();

        setupClockModePickerDialog();
        setupClock();

        setupTree();
        setupBottomSheet();

        failScreenLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Start the clock for a new session
                        clock.start();
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Re-enable clock interaction when the user returns to this activity
        clock.getClockSetting().setModePickerDialogEnabled(true);

        // Disable deep mode count (if applicable)
        clock.disableDeepModeCount();
        updateTagDisplay();
    }


    @Override
    protected void onPause() {
        super.onPause();
        clock.enableDeepModeCount();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)  {
        super.onSaveInstanceState(outState);
        appContext.saveUserInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void getUIReferences() {
        imgTree = findViewById(R.id.tree);
        imgTree.setScaleType(ImageView.ScaleType.FIT_CENTER);
        timeView = findViewById(R.id.time_view);
        startButton = findViewById(R.id.plant_button);

        todoImage = findViewById(R.id.todo_image);
        todoButton = findViewById(R.id.todo_button);
        todoContainer = findViewById(R.id.todo_container);
        musicContainer = findViewById(R.id.music_container);
        musicImage =findViewById(R.id.music_image);
        musicButton=findViewById(R.id.music_button);
        newTagImage = findViewById(R.id.new_tag_image);
        newTagButton = findViewById(R.id.new_tag_button);
        newTagContainer = findViewById(R.id.new_tag_container);

        popupMusicContainer = findViewById(R.id.drawer_layout_base_screen);
        progressBar = findViewById(R.id.progress_bar);
        btnClockModePicker = findViewById(R.id.clockMode);
        modePickerDialog = new ModePickerDialog();
        searchTagSpinner = findViewById(R.id.search_tag_spinner);
    }

    private void setupBottomSheet() {
        imgTree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet = new BottomSheetMainScreen();
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            }
        });
    }

    private void setupClock() {
        clock = new Clock(this, timeView, startButton, appContext.getCurrentUser().getClockSetting(), progressBar, toggleIcon, this);
        appContext.setCurrentClock(clock);
    }

    private void setupClockModePickerDialog() {
        btnClockModePicker.setOnClickListener(v -> {
            showModePickerDialog();
        });
    }

    private void showModePickerDialog() {
        if (getSupportFragmentManager().findFragmentByTag(ModePickerDialog.TAG) == null)
            modePickerDialog.show(getSupportFragmentManager(), ModePickerDialog.TAG);
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
        TagAdapterSpinner tagAdapterSpinner = new TagAdapterSpinner(this, R.layout.item_tag_selected, tagList);
        searchTagSpinner.setAdapter(tagAdapterSpinner);
        searchTagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                appContext.getCurrentUser().setFocusTag(tagList.get(position));
                updateTagDisplay();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                Intent intent = new Intent(MainScreenActivity.this, AddNewTagActivity.class);
                startActivity(intent);
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

    public void setupTree() {
        Uri treeURI = Uri.parse(appContext.getCurrentUser().getUserSetting().getSelectedTree().getImgUri());
        Glide.with(this)
                .load(treeURI)
                .override(Target.SIZE_ORIGINAL) // or specify your desired dimensions
                .fitCenter() // Or use centerCrop() if you want to crop the image to fit
                .into(imgTree);
    }

    public void updateTagDisplay() {
        TagAdapterSpinner tagAdapterSpinner = (TagAdapterSpinner) searchTagSpinner.getAdapter();
        int position = tagAdapterSpinner.getPosition(appContext.getCurrentUser().getFocusTag());
        searchTagSpinner.setSelection(position);
    }

    public void updateBottomSheetSelection() {
        bottomSheet.updateSelectionArea();
    }

    public void navigateBottomSheetSelectionFragment() {
        bottomSheet.navigateSelectionFragment();
    }

    public void startPlanting() {
        startButton.performClick();
        bottomSheet.dismiss();
    }

    @Override
    public void redirectToFailScreenActivity(String message) {
        Intent intent = new Intent(this, FailScreenActivity.class);
        intent.putExtra(FailScreenActivity.TAG_WHY_TREE_WITHERED, message);  // Add the extra message
        failScreenLauncher.launch(intent);
    }

}