package com.hfad.cs426_final_project.StatisticScreen;

import android.view.MenuItem;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Comparator;
import java.util.Map;

import com.hfad.cs426_final_project.DataStorage.Session;
import com.hfad.cs426_final_project.User;

public class StatisticScreenActivity extends BaseScreenActivity {

    // Fields
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
    private List<Session> sessions = new ArrayList<>();
    private SessionMode currentSessionMode = SessionMode.FOCUS_COMPLETED;

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
        initializeComponents();
        setToggleColor(Color.WHITE);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_statistic_screen;
    }

    private void initializeComponents() {
        initializeViews();
        initializeCharts();
        initializePeriodSelection();
        initializeTimeSelection();
        fetchDataForChart(new OnDataFetchedCallback() {
            @Override
            public void onDataFetched() {
                initializeChart();
                setupViewByButton();
                updateTimeSelection();
                sessionModeBtn.setOnClickListener(v -> showSessionModePopup());
            }
        });
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
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        User currentUser = AppContext.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Handle the case where there's no current user logged in (e.g., show an error message)
            return;
        }

        DatabaseReference usersRef = database.getReference("Users");
        DatabaseReference userRef = usersRef.child("User" + currentUser.getId());
        DatabaseReference databaseRef = userRef.child("sessions");

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot sessionSnapshot : dataSnapshot.getChildren()) {
                    Session session = sessionSnapshot.getValue(Session.class);
                    assert session != null;
                    sessions.add(session);
                }

                if (callback != null) {
                    callback.onDataFetched();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
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
        updateCharts(sessions);
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

    // Chart update methods
    private void updateCharts(List<Session> sessions) {
        List<Session> filteredSessions = new ArrayList<>();

        for (Session session : sessions) {
            switch (currentSessionMode) {
                case ALL:
                    filteredSessions.add(session);
                    break;
                case FOCUS_COMPLETED:
                    if (session.isStatus()) {
                        filteredSessions.add(session);
                    }
                    break;
                case FOCUS_INCOMPLETED:
                    if (!session.isStatus()) {
                        filteredSessions.add(session);
                    }
                    break;
            }
        }

        String currentPeriod = timeManager.getCurrentPeriod();
        filteredSessions.sort(Comparator.comparing(Session::getTimestamp));
        updateChartsForPeriod(currentPeriod, filteredSessions);
    }

    private void updateChartsForPeriod(String period, List<Session> sessions) {
        long[] results = focusTimeChart.updateFocusTimeCharts(sessions, period);
        int liveTree = (int)results[0];
        int deadTree = (int)results[1];
        long totalDuration = results[2];

        numLiveTree.setText(String.valueOf(liveTree));
        numDeadTree.setText(String.valueOf(deadTree));
        totalFocusTimeText.setText(String.format("%d hours %d min", totalDuration / 3600, (totalDuration % 3600) / 60));

        updateTagDistributionChart(sessions, period);
    }

    private void updateTagDistributionChart(List<Session> sessions, String period) {
        Map<String, Long> tagDurations = new HashMap<>();
        Map<String, Integer> tagColors = new HashMap<>();
        tagDistributionChart.updateTagDistributionChart(sessions, period, tagDurations, tagColors);

        String mostFocusedTag;
        if (!tagDurations.isEmpty()) {
            mostFocusedTag = Collections.max(tagDurations.entrySet(), Map.Entry.comparingByValue()).getKey();
        } else {
            mostFocusedTag = "Try better";
        }

        int mostFocusedTagColor = 0;

        if (tagColors.containsKey(mostFocusedTag)) {
            mostFocusedTagColor = tagColors.get(mostFocusedTag);
        }

        TextView tagDistributionText = findViewById(R.id.tag_distribution);
        tagDistributionText.setText(mostFocusedTag);
        tagDistributionText.setTextColor(mostFocusedTagColor);
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

        updateCharts(sessions);
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
}