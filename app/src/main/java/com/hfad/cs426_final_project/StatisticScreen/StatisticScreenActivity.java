    package com.hfad.cs426_final_project.StatisticScreen;

    import android.animation.ValueAnimator;
    import android.content.Context;
    import  android.graphics.Color;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.content.ContextCompat;


    import com.github.mikephil.charting.animation.Easing;
    import com.github.mikephil.charting.charts.LineChart;
    import com.github.mikephil.charting.components.MarkerView;
    import com.github.mikephil.charting.components.XAxis;
    import com.github.mikephil.charting.components.YAxis;
    import com.github.mikephil.charting.data.Entry;
    import com.github.mikephil.charting.data.LineData;
    import com.github.mikephil.charting.data.LineDataSet;
    import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
    import com.github.mikephil.charting.highlight.Highlight;
    import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
    import com.github.mikephil.charting.utils.MPPointF;
    import com.google.android.material.button.MaterialButtonToggleGroup;
    import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
    import com.hfad.cs426_final_project.R;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;
    import java.time.Instant;
    import java.time.LocalDateTime;
    import java.time.ZoneId;
    import java.util.ArrayList;
    import java.util.Calendar;
    import java.util.Collections;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Comparator;
    import com.hfad.cs426_final_project.DataStorage.Session;

    public class StatisticScreenActivity extends AppCompatActivity {

        private MaterialButtonToggleGroup toggleGroup;
        private ClickableImageView escapeBtn, shareBtn, backBtn, forwardBtn;
        private TextView timeSelectionText, totalFocusTimeText;
        private TimeManager timeManager;
        private LineChart focusTimeLineChart;
        private List<Session> sessions = new ArrayList<>();
        private int timeIntervals = 4;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_statistic_screen);
            initializeComponents();
        }

        private void initializeComponents() {
            escapeBtn = findViewById(R.id.escape_btn);
            escapeBtn.setOnClickListener(v -> {
            });

            shareBtn = findViewById(R.id.share_btn);
            shareBtn.setOnClickListener(v -> {
            });

            timeSelectionText = findViewById(R.id.time_selection_text);
            totalFocusTimeText = findViewById(R.id.total_focused_time);
            backBtn = findViewById(R.id.back_btn);
            forwardBtn = findViewById(R.id.forward_btn);

            timeManager = new TimeManager();
            initializePeriodSelection();
            initializeTimeSelection();

            focusTimeLineChart = findViewById(R.id.focus_time_chart);
            focusTimeLineChart.setNoDataText("");
            fetchDataForChart(new OnDataFetchedCallback() {
                @Override
                public void onDataFetched() {
                    initializeChart();
                    updateTimeSelection();
                }
            });
        }

        private void initializeChart() {
            focusTimeLineChart.getDescription().setEnabled(false);
            focusTimeLineChart.setTouchEnabled(true);
            focusTimeLineChart.setDragEnabled(true);
            focusTimeLineChart.setScaleEnabled(false);
            focusTimeLineChart.setPinchZoom(false);
            focusTimeLineChart.setDrawGridBackground(false);
            focusTimeLineChart.getLegend().setEnabled(false);

            // Customize X-axis
            XAxis xAxis = focusTimeLineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1f);
            xAxis.setTextColor(ContextCompat.getColor(this, R.color.primary_50));
            xAxis.setTextSize(10f);

            // Disable Y-axis
            focusTimeLineChart.getAxisLeft().setEnabled(false);
            focusTimeLineChart.getAxisRight().setEnabled(false);

            // Set custom marker view
            FocusTimeMarkerView mv = new FocusTimeMarkerView(this, R.layout.custom_marker_view);
            mv.setChartView(focusTimeLineChart);
            focusTimeLineChart.setMarker(mv);

            focusTimeLineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {
                    focusTimeLineChart.highlightValue(h);
                }

                @Override
                public void onNothingSelected() {
                    focusTimeLineChart.highlightValue(null);
                }
            });
        }

        private void fetchDataForChart(OnDataFetchedCallback callback) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference usersRef = database.getReference("Users_bao");
            DatabaseReference userRef = usersRef.child("User0");
            DatabaseReference databaseRef = userRef.child("sessions");

            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot sessionSnapshot : dataSnapshot.getChildren()) {
                        Session session = sessionSnapshot.getValue(Session.class);
                        if (session != null) {
                            sessions.add(session);
                        }
                    }

                    // After data is fetched, call the callback
                    if (callback != null) {
                        callback.onDataFetched();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle possible errors.
                }
            });
        }

        private void updateCharts(List<Session> sessions) {
            String currentSelection = timeManager.getCurrentPeriod();
            List<Entry> entries = new ArrayList<>();
            List<String> xAxisLabels = new ArrayList<>();

            // Sort sessions by timestamp
            Collections.sort(sessions, Comparator.comparing(Session::getTimestamp));

            switch (currentSelection) {
                case "Day":
                    updateChartsForDay(sessions, entries, xAxisLabels);
                    break;
                case "Week":
                    // updateChartsForWeek(sessions, entries, xAxisLabels);
                    break;
                case "Month":
                    // updateChartsForMonth(sessions, entries, xAxisLabels);
                    break;
                case "Year":
                    // updateChartsForYear(sessions, entries, xAxisLabels);
                    break;
            }

            boolean allZero = entries.stream().allMatch(entry -> entry.getY() == 0f);

            if (allZero) {
                focusTimeLineChart.clear();
                focusTimeLineChart.setNoDataText("No data available");
                focusTimeLineChart.setNoDataTextColor(ContextCompat.getColor(this, R.color.primary_50));
                focusTimeLineChart.invalidate();
            } else {
                LineDataSet dataSet = new LineDataSet(entries, "Focus Time");
                dataSet.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
                dataSet.setCircleColor(ContextCompat.getColor(this, R.color.secondary_90));
                dataSet.setLineWidth(1.5f);
                dataSet.setCircleRadius(6f);
                dataSet.setDrawValues(false);
                dataSet.setHighlightEnabled(true);
                dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

                LineData lineData = new LineData(dataSet);

                focusTimeLineChart.setData(lineData);
                focusTimeLineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));

                // Animate chart drawing
                focusTimeLineChart.animateX(1000, Easing.Linear);

                // Animate data points one by one
                ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
                animator.setDuration(1000);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        dataSet.setDrawCircles(false);
                        lineData.notifyDataChanged();
                        focusTimeLineChart.notifyDataSetChanged();
                        focusTimeLineChart.invalidate();

                        float percent = (float) animation.getAnimatedValue();
                        int count = (int) (dataSet.getEntryCount() * percent);

                        for (int i = 0; i < count; i++) {
                            dataSet.setDrawCircles(true);
                        }
                    }
                });
                animator.start();
            }
        }

        private void updateChartsForDay(List<Session> sessions, List<Entry> entries, List<String> xAxisLabels) {
            HashMap<Integer, Float> intervalFocusTime = new HashMap<>();
            LocalDateTime currentDay = timeManager.getCurrentDateTime();
            long totalDuration = 0;

            int intervalDuration = 24 / timeIntervals;

            for (Session session : sessions) {
                LocalDateTime sessionDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(session.getTimestamp()), ZoneId.systemDefault());
                if (sessionDateTime.toLocalDate().equals(currentDay.toLocalDate())) {
                    int hour = sessionDateTime.getHour();
                    int interval = hour / intervalDuration;
                    float focusTimeMinutes = session.getDuration() / 60f;
                    intervalFocusTime.put(interval, intervalFocusTime.getOrDefault(interval, 0f) + focusTimeMinutes);
                    totalDuration += session.getDuration();
                }
            }

            totalFocusTimeText.setText(String.format("%d hours %d min", totalDuration / 3600, (totalDuration % 3600) / 60));

            for (int i = 0; i < timeIntervals; i++) {
                float focusTime = intervalFocusTime.getOrDefault(i, 0f);
                entries.add(new Entry(i, focusTime));
                xAxisLabels.add(String.format("%dh-%dh", i * intervalDuration, (i + 1) * intervalDuration));
            }
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

        private class FocusTimeMarkerView extends MarkerView {
            private TextView tvContent;

            public FocusTimeMarkerView(Context context, int layoutResource) {
                super(context, layoutResource);
                tvContent = findViewById(R.id.tvContent);
            }

            @Override
            public void refreshContent(Entry e, Highlight highlight) {
                int hours = (int) e.getY() / 60;
                int minutes = (int) e.getY() % 60;
                tvContent.setText(String.format("%d hours %d min", hours, minutes));
                super.refreshContent(e, highlight);
            }

            @Override
            public MPPointF getOffset() {
                return new MPPointF(-((float) getWidth() / 2), -getHeight());
            }
        }
    }