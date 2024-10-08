package com.hfad.cs426_final_project.MainScreen;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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
import com.hfad.cs426_final_project.MainScreen.DoneSessionActivities.CongratulationScreenActivity;
import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.MainScreen.DoneSessionActivities.FailScreenActivity;
import com.hfad.cs426_final_project.MainScreen.Clock.Clock;
import com.hfad.cs426_final_project.DataStorage.Tag;
import com.hfad.cs426_final_project.MainScreen.BottomSheet.BottomSheetMainScreen;
import com.hfad.cs426_final_project.MainScreen.Clock.OnClockListener;
import com.hfad.cs426_final_project.MainScreen.Music.MusicAdapter;
import com.hfad.cs426_final_project.MainScreen.Music.MusicItem;
import com.hfad.cs426_final_project.MainScreen.Music.MusicManager;
import com.hfad.cs426_final_project.MainScreen.Clock.ModePickerDialog;
import com.hfad.cs426_final_project.MainScreen.Tag.AddNewTagActivity;
import com.hfad.cs426_final_project.MainScreen.Tag.TagAdapterSpinner;
import com.hfad.cs426_final_project.NotificationReceiver;
import com.hfad.cs426_final_project.R;
import com.hfad.cs426_final_project.ToDoScreen.ToDoScreenActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.tankery.lib.circularseekbar.CircularSeekBar;

public class MainScreenActivity extends BaseScreenActivity implements OnClockListener {
    private AppContext appContext;
    private ImageView imgTree, fireImg;
    private TextView timeView, tvStreakDisplay, tvSunDisplay;
    private MyButton startButton, todoButton, musicButton, newTagButton, btnClockModePicker;
    private ClickableImageView todoImage, musicImage, newTagImage;
    private LinearLayout todoContainer, musicContainer, newTagContainer;
    private DrawerLayout popupMusicContainer;

    private Clock clock;
    private ModePickerDialog modePickerDialog;
    private MusicManager musicManager;
    private Spinner searchTagSpinner;

    private BottomSheetMainScreen bottomSheet;
    private CircularSeekBar progressBar;

    private ActivityResultLauncher<Intent> failScreenLauncher;
    private ActivityResultLauncher<Intent> congratulationScreenLauncher;

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

        setupStreakAndSunInfo();
        setupTree();
        setupBottomSheet();

        registerActivityResultLaunchers();
        myAlarm();
    }

    private void registerActivityResultLaunchers() {
        failScreenLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        clock.start();
                    }
                }
        );

        congratulationScreenLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        clock.start();
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        clock.setTimeView(timeView);
        clock.disableDeepModeCount();
        setupStreakAndSunInfo();
    }

    @Override
    protected void onPause() {
        super.onPause();
        clock.enableDeepModeCount();
    }

    private void getUIReferences() {
        imgTree = findViewById(R.id.tree);
        imgTree.setScaleType(ImageView.ScaleType.FIT_CENTER);
        fireImg = findViewById(R.id.fireIcon);
        timeView = findViewById(R.id.time_view);
        tvStreakDisplay = findViewById(R.id.streakDisplay);
        tvSunDisplay = findViewById(R.id.sunTotalText);
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
        PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

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
        if (tagList.isEmpty()) {
            tagList.add(new Tag());
        }
        TagAdapterSpinner tagAdapterSpinner = new TagAdapterSpinner(this, R.layout.item_tag_selected, tagList);
        searchTagSpinner.setAdapter(tagAdapterSpinner);
        searchTagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                appContext.getCurrentUser().setFocusTag(tagList.get(position));
                updateTagDisplay();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
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

        todoContainer.setOnTouchListener(todoTouchListener);
        todoButton.setOnTouchListener(todoTouchListener);
        todoImage.setOnTouchListener(todoTouchListener);

        View.OnClickListener todoClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainScreenActivity.this, ToDoScreenActivity.class);
                startActivity(intent);
                finish();
            }
        };

        todoContainer.setOnClickListener(todoClickListener);
        todoButton.setOnClickListener(todoClickListener);
        todoImage.setOnClickListener(todoClickListener);
    }

    @SuppressLint("ClickableViewAccessibility")
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

    private void setupStreakAndSunInfo() {
        int streak = appContext.getCurrentUser().getStreakManager().getStreakDays();
        tvStreakDisplay.setText(String.valueOf(streak) + " " + "streak(s)");
        if(streak < 1) {
            fireImg.setVisibility(View.GONE);
            tvStreakDisplay.setText("& Get a streak!");
        }
        else {
            fireImg.setVisibility(View.VISIBLE);
        }

        int sun = appContext.getCurrentUser().getSun();
        tvSunDisplay.setText(String.valueOf(sun));
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
    public void redirectToFailScreenActivity(String message, int rewards) {
        Intent intent = new Intent(this, FailScreenActivity.class);
        intent.putExtra(FailScreenActivity.TAG_WHY_TREE_WITHERED, message);  // Add the extra message
        failScreenLauncher.launch(intent);
    }

    @Override
    public void redirectToCongratulationScreenActivity(int rewards) {
        Intent intent = new Intent(this, CongratulationScreenActivity.class);
        intent.putExtra(CongratulationScreenActivity.REWARDS,rewards);
        congratulationScreenLauncher.launch(intent);
    }

    public void myAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Check for exact alarm scheduling permission on Android 12+ (API level 31+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            // If the permission is not granted, request the user to allow exact alarms
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            startActivity(intent);
            return;  // Exit the method until the permission is granted
        }
        setupAlarm(alarmManager, 22, 0, 0, "NonFocusNotification", 0);
        setupAlarm(alarmManager, 0, 0, 0, "MidnightReset", 1);
    }

    private void setupAlarm(AlarmManager alarmManager, int hour, int minute, int second, String notificationType, int requestCode) {
        // Set the alarm for the specified time
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        if (calendar.getTime().before(new Date())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);  // Set for the next day if the time has passed today
        }

        // Intent for the notification
        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        intent.putExtra("notificationType", notificationType);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), requestCode, intent, PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            // Use setExactAndAllowWhileIdle to ensure the alarm fires even in Doze mode
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    public void disableWhenFocus() {
        imgTree.setOnClickListener(null);

        newTagContainer.setOnClickListener(null);
        newTagImage.setOnClickListener(null);
        newTagButton.setOnClickListener(null);

        todoContainer.setOnClickListener(null);
        todoButton.setOnClickListener(null);
        todoImage.setOnClickListener(null);

        searchTagSpinner.setEnabled(false);
    }

    public void enableOnResume() {
        setupMusicListener();
        setupTodoListener();
        setupNewTagListener();
        searchTagSpinner.setEnabled(true);

        setupTree();
        setupBottomSheet();
    }
}