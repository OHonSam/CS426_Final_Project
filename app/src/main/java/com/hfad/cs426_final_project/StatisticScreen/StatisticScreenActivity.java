    package com.hfad.cs426_final_project.StatisticScreen;

    import android.graphics.Color;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.widget.TextView;

    import androidx.appcompat.app.AppCompatActivity;


    import com.github.mikephil.charting.charts.LineChart;
    import com.github.mikephil.charting.components.XAxis;
    import com.github.mikephil.charting.data.Entry;
    import com.github.mikephil.charting.data.LineData;
    import com.github.mikephil.charting.data.LineDataSet;
    import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
    import com.google.android.material.button.MaterialButtonToggleGroup;
    import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
    import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
    import com.hfad.cs426_final_project.R;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;
    import java.time.Instant;
    import java.time.LocalDateTime;
    import java.time.ZoneId;
    import java.time.ZoneOffset;
    import java.util.ArrayList;
    import java.util.Calendar;
    import java.util.Collections;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Comparator;
    import com.hfad.cs426_final_project.DataStorage.Session;

    public class StatisticScreenActivity extends AppCompatActivity {

        private MaterialButtonToggleGroup toggleGroup;
        private HexagonalLandView landView;
        private ClickableImageView escapeBtn, shareBtn, backBtn, forwardBtn;
        private TextView timeSelectionText;
        private TimeManager timeManager;
        private LineChart focusTimeChart;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_statistic_screen);
            initializeComponents();
            fetchDataAndUpdateChart();
        }

        private void initializeComponents() {
            escapeBtn = findViewById(R.id.escape_btn);
            escapeBtn.setOnClickListener(v -> {
            });

            shareBtn = findViewById(R.id.share_btn);
            shareBtn.setOnClickListener(v -> {
            });

            timeSelectionText = findViewById(R.id.time_selection_text);
            backBtn = findViewById(R.id.back_btn);
            forwardBtn = findViewById(R.id.forward_btn);

            timeManager = new TimeManager();
            initializeToggleGroup();
            initializeTimeNavigation();

            updateTimeSelection();

            focusTimeChart = findViewById(R.id.focus_time_chart);
            initializeChart();
        }

        private void initializeChart() {
            focusTimeChart.getDescription().setEnabled(false);
            focusTimeChart.setTouchEnabled(true);
            focusTimeChart.setDragEnabled(true);
            focusTimeChart.setScaleEnabled(true);
            focusTimeChart.setPinchZoom(true);

            XAxis xAxis = focusTimeChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
        }

        private void fetchDataAndUpdateChart() {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference usersRef = database.getReference("Users_bao");
            DatabaseReference userRef = usersRef.child("User0");
            DatabaseReference databaseRef = userRef.child("sessions");

            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("GO here", "go here");
                    List<Session> sessions = new ArrayList<>();
                    for (DataSnapshot sessionSnapshot : dataSnapshot.getChildren()) {
                        Session session = sessionSnapshot.getValue(Session.class);
                        if (session != null) {
                            sessions.add(session);
                        }
                    }
                    updateChart(sessions);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle possible errors.
                }
            });
        }

        private void updateChart(List<Session> sessions) {
            String currentSelection = timeManager.getCurrentPeriod();
            List<Entry> entries = new ArrayList<>();
            List<String> xAxisLabels = new ArrayList<>();

            // Sort sessions by timestamp
            Collections.sort(sessions, Comparator.comparing(Session::getTimestamp));

            switch (currentSelection) {
                case "Day":
                    updateChartForDay(sessions, entries, xAxisLabels);
                    break;
                case "Week":
                    // updateChartForWeek(sessions, entries, xAxisLabels);
                    break;
                case "Month":
                    // updateChartForMonth(sessions, entries, xAxisLabels);
                    break;
                case "Year":
                    // updateChartForYear(sessions, entries, xAxisLabels);
                    break;
            }

            LineDataSet dataSet = new LineDataSet(entries, "Focus Time (minutes)");
            dataSet.setColor(Color.BLUE);
            dataSet.setCircleColor(Color.BLUE);
            LineData lineData = new LineData(dataSet);

            focusTimeChart.setData(lineData);
            focusTimeChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
            focusTimeChart.invalidate(); // refresh
        }

        private void updateChartForDay(List<Session> sessions, List<Entry> entries, List<String> xAxisLabels) {
            HashMap<Integer, Float> hourlyFocusTime = new HashMap<>();
            LocalDateTime currentDay = timeManager.getCurrentDateTime();

            for (Session session : sessions) {
                LocalDateTime sessionDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(session.getTimestamp()), ZoneId.systemDefault());
                Log.d("Session", sessionDateTime.toString());
                if (sessionDateTime.toLocalDate().equals(currentDay.toLocalDate())) {
                    int hour = sessionDateTime.getHour();
                    float focusTimeMinutes = session.getDuration() / 60f;
                    hourlyFocusTime.put(hour, hourlyFocusTime.getOrDefault(hour, 0f) + focusTimeMinutes);
                }
            }

            for (int hour = 0; hour < 24; hour++) {
                entries.add(new Entry(hour, hourlyFocusTime.getOrDefault(hour, 0f)));
                xAxisLabels.add(String.format("%02d:00", hour));
            }
        }

        private void initializeToggleGroup() {
            toggleGroup = findViewById(R.id.time_picker);

            toggleGroup.check(R.id.day_btn);

            toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
                if (isChecked) {
                    if (checkedId == R.id.day_btn) {
                        handleSelection("Day");
                    } else if (checkedId == R.id.week_btn) {
                        handleSelection("Week");
                    } else if (checkedId == R.id.month_btn) {
                        handleSelection("Month");
                    } else if (checkedId == R.id.year_btn) {
                        handleSelection("Year");
                    }
                }
            });
        }

        private void initializeTimeNavigation() {
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

        private void handleSelection(String period) {
            timeManager.handleSelection(period);
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
            fetchDataAndUpdateChart();
        }


//        private void initializeIsometricLandView() {
//            landView.post(() -> {
//                for (int i = 0; i < 6; i++) {
//                    for (int j = 0; j < 6; j++) {
//                        if ((i + j) % 2 == 0) {
//                            landView.addTree(i, j);
//                        }
//                    }
//                }
//            });
//        }
    }