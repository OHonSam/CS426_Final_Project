    package com.hfad.cs426_final_project.StatisticScreen;

    import android.animation.ValueAnimator;
    import android.content.Context;
    import  android.graphics.Color;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.view.animation.Animation;
    import android.widget.LinearLayout;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.content.ContextCompat;


    import com.github.mikephil.charting.animation.ChartAnimator;
    import com.github.mikephil.charting.animation.Easing;
    import com.github.mikephil.charting.charts.LineChart;
    import com.github.mikephil.charting.charts.PieChart;
    import com.github.mikephil.charting.components.MarkerView;
    import com.github.mikephil.charting.components.XAxis;
    import com.github.mikephil.charting.components.YAxis;
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
    import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
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

    public class StatisticScreenActivity extends AppCompatActivity {

        private MaterialButtonToggleGroup toggleGroup;
        private ClickableImageView escapeBtn, shareBtn, backBtn, forwardBtn;
        private TextView timeSelectionText, totalFocusTimeText, numLiveTree, numDeadTree;
        private TimeManager timeManager;
        private LineChart focusTimeLineChart;
        private PieChart tagDistributionChart;
        private List<Session> sessions = new ArrayList<>();
        int numHourIntervals = 4;
        int numDayIntervals = 7;
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
            numLiveTree = findViewById(R.id.num_live_tree);
            numDeadTree = findViewById(R.id.num_dead_tree);
            backBtn = findViewById(R.id.back_btn);
            forwardBtn = findViewById(R.id.forward_btn);

            timeManager = new TimeManager();
            initializePeriodSelection();
            initializeTimeSelection();

            focusTimeLineChart = findViewById(R.id.focus_time_chart);
            focusTimeLineChart.setNoDataText("");

            tagDistributionChart = findViewById(R.id.tag_distribution_chart);
            tagDistributionChart.setNoDataText("");

            fetchDataForChart(new OnDataFetchedCallback() {
                @Override
                public void onDataFetched() {
                    initializeChart();
                    updateTimeSelection();
                }
            });
        }

        private void initializeChart() {
            initializeFocusTimeLineChart();
            initializeTagDistributionChart();
        }

        private void fetchDataForChart(OnDataFetchedCallback callback) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference usersRef = database.getReference("Users_bao");
            DatabaseReference userRef = usersRef.child("User0");
            DatabaseReference databaseRef = userRef.child("sessions");

            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot sessionSnapshot : dataSnapshot.getChildren()) {
                        Session session = sessionSnapshot.getValue(Session.class);
                        assert session != null;
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

        private void initializeFocusTimeLineChart() {
            focusTimeLineChart.getDescription().setEnabled(false);
            focusTimeLineChart.setTouchEnabled(true);
            focusTimeLineChart.setDragEnabled(true);
            focusTimeLineChart.setScaleEnabled(true);
            focusTimeLineChart.setPinchZoom(false);
            focusTimeLineChart.setDrawGridBackground(false);
            focusTimeLineChart.getLegend().setEnabled(false);

            // Customize X-axis
            XAxis xAxis = focusTimeLineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1f);
            xAxis.setTextColor(ContextCompat.getColor(this, R.color.primary_50));
            xAxis.setTextSize(8f);
            xAxis.setLabelRotationAngle(45f);

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

        private void initializeTagDistributionChart() {
            tagDistributionChart.getDescription().setEnabled(false);
            tagDistributionChart.setDrawHoleEnabled(false);
            tagDistributionChart.setUsePercentValues(true);
            tagDistributionChart.setEntryLabelColor(Color.WHITE);
            tagDistributionChart.setEntryLabelTextSize(12f);
            tagDistributionChart.getLegend().setEnabled(false);
        }

        private void updateCharts(List<Session> sessions) {
            String currentSelection = timeManager.getCurrentPeriod();
            List<Entry> entries = new ArrayList<>();
            List<String> xAxisLabels = new ArrayList<>();

            // Sort sessions by timestamp
            sessions.sort(Comparator.comparing(Session::getTimestamp));

            // Use the common method to update charts for the selected period
            updateChartsForPeriod(currentSelection, sessions, entries, xAxisLabels);
        }

        private void updateChartsForPeriod(String period, List<Session> sessions, List<Entry> entries, List<String> xAxisLabels) {
            // Update focus time data
            calculateFocusTimeData(period, sessions, entries, xAxisLabels);
            updateFocusTimeChart(entries, xAxisLabels);

            // Update tag distribution chart
            List<Session> filteredSessions = calculateTagDistributionData(period, sessions);
            updateTagDistributionChart(filteredSessions);
        }


        private void updateFocusTimeChart(List<Entry> entries, List<String> xAxisLabels) {
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

        private void calculateFocusTimeData(String period, List<Session> sessions, List<Entry> entries, List<String> xAxisLabels) {
            HashMap<Integer, Float> intervalFocusTime = new HashMap<>();
            LocalDateTime startTime = getStartOfPeriod(period);
            LocalDateTime endTime = getEndOfPeriod(period);

            long totalDuration = 0;
            int intervalDuration = getIntervalDuration(period);
            int deadTree = 0;
            int liveTree = 0;

            for (Session session : sessions) {
                LocalDateTime sessionDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(session.getTimestamp()), ZoneId.systemDefault());
                if (isWithinPeriod(sessionDateTime, startTime, endTime)) {
                    int intervalIndex = getIntervalIndex(sessionDateTime, period, intervalDuration);
                    float focusTimeMinutes = session.getDuration() / 60f;
                    intervalFocusTime.put(intervalIndex, intervalFocusTime.getOrDefault(intervalIndex, 0f) + focusTimeMinutes);
                    totalDuration += session.getDuration();

                    if (session.isStatus()) {
                        ++liveTree;
                    } else {
                        ++deadTree;
                    }
                }
            }

            numLiveTree.setText(String.valueOf(liveTree));
            numDeadTree.setText(String.valueOf(deadTree));
            totalFocusTimeText.setText(String.format("%d hours %d min", totalDuration / 3600, (totalDuration % 3600) / 60));

            for (int i = 0; i < getNumInterval(period); i++) {
                float focusTime = intervalFocusTime.getOrDefault(i, 0f);
                entries.add(new Entry(i, focusTime));
                xAxisLabels.add(formatIntervalLabel(i, period, intervalDuration));
            }
        }



        private List<Session> calculateTagDistributionData(String period, List<Session> sessions) {
            List<Session> filteredSessions = new ArrayList<>();
            LocalDateTime startTime = getStartOfPeriod(period);
            LocalDateTime endTime = getEndOfPeriod(period);

            for (Session session : sessions) {
                if (!session.isStatus()) {
                    continue;
                }

                LocalDateTime sessionDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(session.getTimestamp()), ZoneId.systemDefault());
                if (isWithinPeriod(sessionDateTime, startTime, endTime)) {
                    filteredSessions.add(session);  // Add the session to the filtered list
                }
            }
            return filteredSessions;  // Return the filtered sessions
        }

        private void updateTagDistributionChart(List<Session> sessions) {
            Map<String, Long> tagDurations = new HashMap<>();
            Map<String, Integer> tagColors = new HashMap<>();
            long totalDuration = 0;

            // Calculate durations for each tag and assign colors
            for (Session session : sessions) {
                    String tagName = session.getTag().getName();
                    long duration = session.getDuration();
                    tagDurations.put(tagName, tagDurations.getOrDefault(tagName, 0L) + duration);
                    totalDuration += duration;

                    // Assign color to each tag if not already assigned
                    if (!tagColors.containsKey(tagName)) {
                        tagColors.put(tagName, session.getTag().getColor());
                }
            }

            // Prepare data for the pie chart
            List<PieEntry> entries = new ArrayList<>();
            List<Integer> colors = new ArrayList<>();
            LinearLayout tagListLayout = findViewById(R.id.tag_list);
            tagListLayout.removeAllViews();

            for (Map.Entry<String, Long> entry : tagDurations.entrySet()) {
                float percentage = (float) entry.getValue() / totalDuration * 100;
                entries.add(new PieEntry(percentage, entry.getKey()));
                int color = tagColors.get(entry.getKey());  // Retrieve color from the map
                colors.add(color);

                addTagItem(tagListLayout, entry.getKey(), color, entry.getValue(), totalDuration);
            }

            PieDataSet dataSet = new PieDataSet(entries, "Tag Distribution");
            dataSet.setColors(colors);
            dataSet.setValueTextSize(12f);
            dataSet.setValueTextColor(Color.WHITE);
            dataSet.setValueFormatter(new PercentFormatter());

            tagDistributionChart.setData(new PieData(dataSet));
            tagDistributionChart.setRotationAngle(270f);
            tagDistributionChart.setRotationEnabled(false);
            tagDistributionChart.animateY(1500, Easing.EaseInOutCubic);
            tagDistributionChart.invalidate();

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

            tagDistributionChart.invalidate(); // refresh the chart
        }

        private void addTagItem(LinearLayout tagListLayout, String tagName, int color, long duration, long totalDuration) {
            View tagItem = getLayoutInflater().inflate(R.layout.statistic_tag_item, tagListLayout, false);
            View colorView = tagItem.findViewById(R.id.tag_color);
            TextView nameView = tagItem.findViewById(R.id.tag_name);
            TextView valueView = tagItem.findViewById(R.id.tag_value);

            colorView.setBackgroundColor(color);
            nameView.setText(tagName);
            valueView.setText(String.format("%d%% (%d mins)",
                    Math.round((float) duration / totalDuration * 100),
                    duration / 60));

            tagListLayout.addView(tagItem);
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

        private LocalDateTime getStartOfPeriod(String period) {
            Calendar calendar = timeManager.getCurrentDate();
            LocalDateTime currentDate = LocalDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId());

            switch (period) {
                case "Day":
                    return currentDate.withHour(0).withMinute(0).withSecond(0).withNano(0);
                case "Week":
                    LocalDateTime startOfWeek = currentDate.with(DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0).withNano(0);
                    return startOfWeek;
                case "Month":
                    return currentDate.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                case "Year":
                    return currentDate.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                default:
                    return currentDate;
            }
        }


        private LocalDateTime getEndOfPeriod(String period) {
            LocalDateTime start = getStartOfPeriod(period);
            switch (period) {
                case "Day":
                    return start.plusDays(1);
                case "Week":
                    return start.plusWeeks(1);
                case "Month":
                    return start.plusMonths(1);
                case "Year":
                    return start.plusYears(1);
                default:
                    return start;
            }
        }

        private boolean isWithinPeriod(LocalDateTime dateTime, LocalDateTime start, LocalDateTime end) {
            return (dateTime.isEqual(start) || dateTime.isAfter(start)) && dateTime.isBefore(end);
        }

        private int getIntervalIndex(LocalDateTime dateTime, String period, int intervalDuration) {
            switch (period) {
                case "Day":
                    return dateTime.getHour() / intervalDuration;
                case "Week":
                    return dateTime.getDayOfWeek().getValue() - 1;
                case "Month":
                    return (dateTime.getDayOfMonth() - 1) / intervalDuration >= numDayIntervals ? numDayIntervals - 1 : (dateTime.getDayOfMonth() - 1) / intervalDuration;
                case "Year":
                    return dateTime.getMonthValue() - 1;
                default:
                    return 0;
            }
        }

        private int getIntervalDuration(String period) {
            switch (period) {
                case "Day":
                    return 24 / numHourIntervals;
                case "Month":
                    return timeManager.getCurrentDateTime().toLocalDate().lengthOfMonth() / numDayIntervals;
                default:
                    return 1;
            }
        }

        private int getNumInterval(String period) {
            switch (period) {
                case "Day":
                    return numHourIntervals;
                case "Week":
                    return 7;
                case "Month":
                    return numDayIntervals;
                case "Year":
                    return 12;
                default:
                    return 0;
            }
        }

        private String formatIntervalLabel(int interval, String period, int intervalDuration) {
            switch (period) {
                case "Day":
                    int startHour = interval * intervalDuration;
                    int endHour = (interval + 1) * intervalDuration;
                    return String.format("%dh-%dh", startHour, endHour < 24 ? endHour : endHour - 24);
                case "Week":
                    int dayOfWeekIndex = (interval % 7) + 1;
                    return DayOfWeek.of(dayOfWeekIndex).toString().substring(0, 3);
                case "Month":
                    int startDay = interval * intervalDuration;
                    int endDay = (interval + 1) * intervalDuration;
                    if (interval == numDayIntervals - 1) {
                        endDay = timeManager.getCurrentDateTime().toLocalDate().lengthOfMonth();
                    }
                    return String.format("Day %d-%d", startDay + 1, Math.min(endDay,timeManager.getCurrentDateTime().toLocalDate().lengthOfMonth()));
                case "Year":
                    int monthIndex = Math.max(0, Math.min(interval, 11));

                    LocalDate currentDate = timeManager.getCurrentDateTime().toLocalDate();
                    DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH);

                    return monthFormatter.format(LocalDate.of(currentDate.getYear(), monthIndex + 1, 1));
                default:
                    return "";
            }
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