package com.hfad.cs426_final_project.StatisticScreen;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.BaseScreenActivity;
import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.R;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Comparator;
import java.util.Map;

import com.hfad.cs426_final_project.DataStorage.Session;

public class StatisticScreenActivity extends BaseScreenActivity {

    private MaterialButtonToggleGroup toggleGroup;
    private ClickableImageView shareBtn, backBtn, forwardBtn, cancelBtn;
    private TextView timeSelectionText, totalFocusTimeText, numLiveTree, numDeadTree;
    private TimeManager timeManager;
    private LineChart focusTimeLineChart;
    private BarChart focusTimeBarChart;
    private PieChart tagDistributionPieChart;
    private LinearLayout tagListLayout;
    private MyButton viewByButton, sessionModeBtn;
    private boolean isLineChartVisible = true;
    private TagDistributionChart tagDistributionChart;
    private FocusTimeChart focusTimeChart;
    private List<Session> allSessions = new ArrayList<>();
    private List<Session> filteredSessions = new ArrayList<>();
    private SessionMode currentSessionMode = SessionMode.FOCUS_COMPLETED;
    private MyButton itemTypeBtn;
    private ImageView itemTypeImage;
    private boolean isTreeMode = true;
    private ListView favoriteListView;
    private FavoriteItemListAdapter favoriteAdapter;
    private ChartUtils chartUtils;

    private enum SessionMode {
        ALL("All"),
        FOCUS_COMPLETED("Focus completed"),
        FOCUS_INCOMPLETED("Focus incompleted");

        private final String displayName;

        SessionMode(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chartUtils = new ChartUtils();
        initializeComponents();
        setToggleColor(Color.WHITE);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_statistic_screen;
    }

    private void initializeComponents() {
        initializeViews();
        initializePeriodSelection();
        initializeTimeSelection();
        initializeCharts();
        initializeFavoriteItemList();
        fetchDataForChart(new OnDataFetchedCallback() {
            @Override
            public void onDataFetched() {
                initializeChart();
                setupViewByButton();
                sessionModeBtn.setOnClickListener(v -> showSessionModePopup());
                shareBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareStatistics();
                    }
                });
                setupItemTypeButton();
                updateTimeSelection();
                updateItemTypeImage();
            }
        });

    }

    private void setupItemTypeButton() {
        itemTypeBtn = findViewById(R.id.item_type_btn);
        itemTypeImage = findViewById(R.id.item_type_image);
        itemTypeBtn.setOnClickListener(v -> toggleItemType());
        shareBtn = findViewById(R.id.share_btn);
    }

    private void toggleItemType() {
        isTreeMode = !isTreeMode;
        updateItemTypeButtonAndImage();
        updateFavoriteList();
        updateItemTypeImage();
    }

    private void updateItemTypeButtonAndImage() {
        if (isTreeMode) {
            itemTypeBtn.setText("Tree");
            itemTypeBtn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.secondary_40));
        } else {
            itemTypeBtn.setText("Block");
            itemTypeBtn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.secondary_90));
        }
    }

    private void updateItemTypeImage() {
        if (favoriteAdapter == null || favoriteAdapter.getCount() == 0) {
            // Set default image if no data
            itemTypeImage.setImageResource(isTreeMode ? R.drawable.favorite_tree : R.drawable.piece);
            return;
        }

        String topItemImageUri = favoriteAdapter.getTopItemImageUri();
        if (topItemImageUri != null) {
            Glide.with(this)
                    .load(topItemImageUri)
                    .into(itemTypeImage);
        } else {
            itemTypeImage.setImageResource(isTreeMode ? R.drawable.favorite_tree : R.drawable.piece);
        }
    }

    private void updateFavoriteList() {
        if (favoriteAdapter != null) {
            favoriteAdapter.updateData(filteredSessions, isTreeMode);
        }
    }

    private void initializeViews() {
        sessionModeBtn = findViewById(R.id.all_session_mode);
        shareBtn = findViewById(R.id.share_btn);
        shareBtn.setOnClickListener(v -> {
        });
        backBtn = findViewById(R.id.back_btn);
        forwardBtn = findViewById(R.id.forward_btn);
        timeSelectionText = findViewById(R.id.time_selection_text);
        totalFocusTimeText = findViewById(R.id.total_focused_time);
        numLiveTree = findViewById(R.id.num_live_tree);
        numDeadTree = findViewById(R.id.num_dead_tree);
        viewByButton = findViewById(R.id.view_by_button);

        favoriteListView = findViewById(R.id.statistic_list);
    }

    private void initializeCharts() {
        focusTimeLineChart = findViewById(R.id.focus_time_chart);
        focusTimeLineChart.setNoDataText("");
        focusTimeBarChart = findViewById(R.id.focus_time_bar_chart);
        tagDistributionPieChart = findViewById(R.id.tag_distribution_chart);
        tagDistributionPieChart.setNoDataText("");
        tagListLayout = findViewById(R.id.tag_list);
    }

    private void initializeChart() {
        focusTimeChart = new FocusTimeChart(this, focusTimeLineChart, focusTimeBarChart);
        tagDistributionChart = new TagDistributionChart(this, tagDistributionPieChart, tagListLayout);
    }

    private void fetchDataForChart(OnDataFetchedCallback callback) {
        allSessions = AppContext.getInstance().getCurrentUser().getSessions();
        if(callback != null) {
            callback.onDataFetched();
        }
    }

    private void setupViewByButton() {
        viewByButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(StatisticScreenActivity.this, viewByButton);
            popup.getMenuInflater().inflate(R.menu.statistic_screen_view_by, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_line_chart) {
                    showLineChart();
                    return true;
                } else if (item.getItemId() == R.id.menu_bar_chart) {
                    showBarChart();
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    private void showSessionModePopup() {
        PopupMenu popup = new PopupMenu(this, sessionModeBtn);
        popup.getMenuInflater().inflate(R.menu.popup_session_mode, popup.getMenu());
        popup.setOnMenuItemClickListener(this::onSessionModeItemClick);
        popup.show();
    }

    private boolean onSessionModeItemClick(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_all_sessions) {
            updateSessionMode(SessionMode.ALL);
            return true;
        } else if (itemId == R.id.menu_focus_completed) {
            updateSessionMode(SessionMode.FOCUS_COMPLETED);
            return true;
        } else if (itemId == R.id.menu_focus_incompleted) {
            updateSessionMode(SessionMode.FOCUS_INCOMPLETED);
            return true;
        }
        return false;
    }

    private void updateSessionMode(SessionMode newMode) {
        currentSessionMode = newMode;
        sessionModeBtn.setText(currentSessionMode.getDisplayName());
        updateSessionModeButtonAppearance();
        updateCharts();
    }

    private void updateSessionModeButtonAppearance() {
        int colorResId;
        switch (currentSessionMode) {
            case FOCUS_INCOMPLETED:
                colorResId = R.color.secondary_30;
                break;
            case ALL:
                colorResId = R.color.secondary_40;
                break;
            default:
                colorResId = R.color.secondary_50;
        }
        sessionModeBtn.setBackgroundTintList(ContextCompat.getColorStateList(this, colorResId));
    }

    private void updateCharts() {
        filteredSessions = filterSessions(allSessions);
        updateFocusTimeChart();
        updateTagDistributionChart();
        updateFavoriteItemList();
        updateItemTypeImage();
    }

    private List<Session> filterSessions(List<Session> sessions) {
        List<Session> filtered = new ArrayList<>();
        String currentPeriod = timeManager.getCurrentPeriod();
        LocalDateTime startTime = chartUtils.getStartOfPeriod(currentPeriod);
        LocalDateTime endTime = chartUtils.getEndOfPeriod(currentPeriod, startTime);

        for (Session session : sessions) {
            LocalDateTime sessionDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(session.getTimestamp()), ZoneId.systemDefault());
            if (chartUtils.isWithinPeriod(sessionDateTime, startTime, endTime)) {
                switch (currentSessionMode) {
                    case ALL:
                        filtered.add(session);
                        break;
                    case FOCUS_COMPLETED:
                        if (session.isStatus()) {
                            filtered.add(session);
                        }
                        break;
                    case FOCUS_INCOMPLETED:
                        if (!session.isStatus()) {
                            filtered.add(session);
                        }
                        break;
                }
            }
        }

        filtered.sort(Comparator.comparing(Session::getTimestamp));
        return filtered;
    }

    private void initializeFavoriteItemList() {
        filteredSessions = new ArrayList<>();
        favoriteAdapter = new FavoriteItemListAdapter(this, filteredSessions, isTreeMode);
        favoriteListView.setAdapter(favoriteAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            favoriteListView.setNestedScrollingEnabled(true);
        }

        favoriteListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    private void updateFocusTimeChart() {
        String currentPeriod = timeManager.getCurrentPeriod();
        long[] results = focusTimeChart.updateFocusTimeCharts(filteredSessions, currentPeriod);
        int liveTree = (int)results[0];
        int deadTree = (int)results[1];
        long totalDuration = results[2];

        numLiveTree.setText(String.valueOf(liveTree));
        numDeadTree.setText(String.valueOf(deadTree));
        totalFocusTimeText.setText(String.format("%d hours %d min", totalDuration / 3600, (totalDuration % 3600) / 60));
    }

    private void updateTagDistributionChart() {
        Map<String, Long> tagDurations = new HashMap<>();
        Map<String, Integer> tagColors = new HashMap<>();
        tagDistributionChart.updateTagDistributionChart(filteredSessions, tagDurations, tagColors);

        String mostFocusedTag = tagDurations.isEmpty() ? "Let's try to focus!!!" :
                Collections.max(tagDurations.entrySet(), Map.Entry.comparingByValue()).getKey();

        int mostFocusedTagColor = tagColors.getOrDefault(mostFocusedTag, 0);

        TextView tagDistributionText = findViewById(R.id.tag_distribution);
        tagDistributionText.setText(mostFocusedTag);
        tagDistributionText.setTextColor(mostFocusedTagColor);
    }

    private void updateFavoriteItemList() {
        if (favoriteAdapter != null) {
            favoriteAdapter.updateData(filteredSessions, isTreeMode);
        }
    }


    // Time selection methods
    private void initializePeriodSelection() {
        toggleGroup = findViewById(R.id.time_picker);
        toggleGroup.check(R.id.day_btn);
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.day_btn) {
                    updatePeriodSelection("Day");
                } else if (checkedId == R.id.week_btn) {
                    updatePeriodSelection("Week");
                } else if (checkedId == R.id.month_btn) {
                    updatePeriodSelection("Month");
                } else if (checkedId == R.id.year_btn) {
                    updatePeriodSelection("Year");
                }
            }
        });
    }

    private void initializeTimeSelection() {
        timeManager = TimeManager.getInstance();
        backBtn.setOnClickListener(v -> navigateTime(-1));
        forwardBtn.setOnClickListener(v -> navigateTime(1));
        timeSelectionText.setOnClickListener(v -> {
            if (!timeManager.isLatestTime()) {
                timeManager.setCurrentDate(Calendar.getInstance());
                updateTimeSelection();
            }
        });
    }

    private void updatePeriodSelection(String period) {
        timeManager.updatePeriodSelection(period);
        updateTimeSelection();
        updateCharts();
    }

    private void navigateTime(int direction) {
        timeManager.navigateTime(direction);
        updateTimeSelection();
    }

    private void updateTimeSelection() {
        String formattedTimeSelection = timeManager.getFormattedTimeSelection();
        timeSelectionText.setText(formattedTimeSelection);
        forwardBtn.setVisibility(timeManager.isLatestTime() ? View.INVISIBLE : View.VISIBLE);

        if (timeManager.isLatestTime()) {
            timeSelectionText.setCompoundDrawables(null, null, null, null);
        } else {
            timeSelectionText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.return_btn, 0);
        }
        updateCharts();
    }

    // Chart view methods
    private void showLineChart() {
        focusTimeLineChart.setVisibility(View.VISIBLE);
        focusTimeBarChart.setVisibility(View.GONE);
        viewByButton.setText("Line chart");
        viewByButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.primary_10));
        focusTimeLineChart.animateX(1500, Easing.EaseInCubic);
        isLineChartVisible = true;
    }

    private void showBarChart() {
        focusTimeLineChart.setVisibility(View.GONE);
        focusTimeBarChart.setVisibility(View.VISIBLE);
        viewByButton.setText("Bar chart");
        viewByButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.primary_30));
        focusTimeBarChart.animateY(1500, Easing.EaseInCubic);
        focusTimeBarChart.invalidate();
        isLineChartVisible = false;
    }

    // Interface
    public interface OnDataFetchedCallback {
        void onDataFetched();
    }

    private void shareStatistics() {
        LinearLayout contentToShare = findViewById(R.id.content_to_share);
        Bitmap screenshot = takeScreenshot(contentToShare);
        Uri screenshotUri = saveScreenshotToMediaStore(screenshot);
        shareScreenshot(screenshotUri);
    }

    private Bitmap takeScreenshot(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    private Uri saveScreenshotToMediaStore(Bitmap bitmap) {
        ContentResolver resolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "statistics_" + System.currentTimeMillis() + ".png");
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        try {
            OutputStream outputStream = resolver.openOutputStream(imageUri);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageUri;
    }

    private void shareScreenshot(Uri uri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(shareIntent, "Share Statistics"));
    }
}