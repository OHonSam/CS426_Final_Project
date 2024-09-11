    package com.hfad.cs426_final_project.StatisticScreen;

    import android.animation.ValueAnimator;
    import android.content.Context;
    import  android.graphics.Color;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.view.animation.Animation;
    import android.widget.LinearLayout;
    import android.widget.PopupMenu;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.content.ContextCompat;


    import com.github.mikephil.charting.animation.ChartAnimator;
    import com.github.mikephil.charting.animation.Easing;
    import com.github.mikephil.charting.charts.BarChart;
    import com.github.mikephil.charting.charts.LineChart;
    import com.github.mikephil.charting.charts.PieChart;
    import com.github.mikephil.charting.components.MarkerView;
    import com.github.mikephil.charting.components.XAxis;
    import com.github.mikephil.charting.components.YAxis;
    import com.github.mikephil.charting.data.BarData;
    import com.github.mikephil.charting.data.BarDataSet;
    import com.github.mikephil.charting.data.BarEntry;
    import com.github.mikephil.charting.data.Entry;
    import com.github.mikephil.charting.data.LineData;
    import com.github.mikephil.charting.data.LineDataSet;
    import com.github.mikephil.charting.data.PieData;
    import com.github.mikephil.charting.data.PieDataSet;
    import com.github.mikephil.charting.data.PieEntry;
    import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
    import com.github.mikephil.charting.formatter.PercentFormatter;
    import com.github.mikephil.charting.highlight.Highlight;
    import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
    import com.github.mikephil.charting.utils.MPPointF;
    import com.github.mikephil.charting.utils.Utils;
    import com.google.android.material.button.MaterialButtonToggleGroup;
    import com.hfad.cs426_final_project.AppContext;
    import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
    import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
    import com.hfad.cs426_final_project.DataStorage.Tag;
    import com.hfad.cs426_final_project.R;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;

    import java.text.SimpleDateFormat;
    import java.time.DayOfWeek;
    import java.time.Instant;
    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.time.ZoneId;
    import java.time.format.DateTimeFormatter;
    import java.util.ArrayList;
    import java.util.Calendar;
    import java.util.Collections;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Comparator;
    import java.util.Locale;
    import java.util.Map;

    import com.hfad.cs426_final_project.DataStorage.Session;
    import com.hfad.cs426_final_project.User;

    public class StatisticScreenActivity extends AppCompatActivity {

        private MaterialButtonToggleGroup toggleGroup;
        private ClickableImageView escapeBtn, shareBtn, backBtn, forwardBtn, cancelBtn;
        private TextView timeSelectionText, totalFocusTimeText, numLiveTree, numDeadTree;
        private TimeManager timeManager;
        private LineChart focusTimeLineChart;
        private BarChart focusTimeBarChart;
        private PieChart tagDistributionPieChart;
        private LinearLayout tagListLayout;
        private MyButton viewByButton;
        private boolean isLineChartVisible = true;
        private TagDistributionChart tagDistributionChart;
        private FocusTimeChart focusTimeChart;
        private List<Session> sessions = new ArrayList<>();

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_statistic_screen);
             initializeComponents();
        }

        private void initializeComponents() {
            escapeBtn = findViewById(R.id.escape_btn);
            escapeBtn.setOnClickListener(v -> {
                finish();
            });

            shareBtn = findViewById(R.id.share_btn);
            shareBtn.setOnClickListener(v -> {
            });

            backBtn = findViewById(R.id.back_btn);
            forwardBtn = findViewById(R.id.forward_btn);
            timeSelectionText = findViewById(R.id.time_selection_text);
            totalFocusTimeText = findViewById(R.id.total_focused_time);
            numLiveTree = findViewById(R.id.num_live_tree);
            numDeadTree = findViewById(R.id.num_dead_tree);

            timeManager = TimeManager.getInstance();
            initializePeriodSelection();
            initializeTimeSelection();

            focusTimeLineChart = findViewById(R.id.focus_time_chart);
            focusTimeLineChart.setNoDataText("");

            focusTimeBarChart = findViewById(R.id.focus_time_bar_chart);
            focusTimeBarChart = findViewById(R.id.focus_time_bar_chart);

            tagDistributionPieChart = findViewById(R.id.tag_distribution_chart);
            tagDistributionPieChart.setNoDataText("");
            tagListLayout = findViewById(R.id.tag_list);
            viewByButton = findViewById(R.id.view_by_button);

            fetchDataForChart(new OnDataFetchedCallback() {
                @Override
                public void onDataFetched() {
                    initializeChart();
                    setupViewByButton();
                    updateTimeSelection();
                }
            });
        }

        private void initializeChart() {
            focusTimeChart = new FocusTimeChart(this, focusTimeLineChart, focusTimeBarChart);
            tagDistributionChart = new TagDistributionChart(this, tagDistributionPieChart, tagListLayout);
        }

        private void fetchDataForChart(OnDataFetchedCallback callback) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            User currentUser = AppContext.getInstance().getCurrentUser();
            Log.d("Current user", currentUser.getId() + "");
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
                        Log.d("Sessionn", session.getTimestamp() + "");
                        Log.d("Timestamp", session.getDateTimeFromTimestamp() + "");
                        sessions.add(session);
                    }

                    // After data is fetched, call the callback
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

        private void updateCharts(List<Session> sessions) {
            Log.d("Hello", "Hello");
            String currentPeriod = timeManager.getCurrentPeriod();
            sessions.sort(Comparator.comparing(Session::getTimestamp));
            updateChartsForPeriod(currentPeriod, sessions);
        }

        private void updateChartsForPeriod(String period, List<Session> sessions) {
            // Update focus time data
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
            backBtn.setOnClickListener(v -> {
                navigateTime(-1);
            });

            forwardBtn.setOnClickListener(v -> {
                navigateTime(1);
            });

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

        public interface OnDataFetchedCallback {
            void onDataFetched();
        }
    }