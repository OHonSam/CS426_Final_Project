package com.hfad.cs426_final_project.StatisticScreen;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.hfad.cs426_final_project.R;
import com.hfad.cs426_final_project.DataStorage.Session;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.time.Instant;
import java.time.ZoneId;

public class FocusTimeChart {

    private Context context;
    private LineChart lineChart;
    private BarChart barChart;
    private TextView numLiveTree;
    private TextView numDeadTree;
    private TextView totalFocusTimeText;
    private ChartUtils chartUtils;
    private long liveTree = 0;
    private long deadTree = 0;
    private long totalDuration = 0;

    public FocusTimeChart(Context context, LineChart lineChart, BarChart barChart) {
        this.context = context;
        this.lineChart = lineChart;
        this.barChart = barChart;
        this.chartUtils = new ChartUtils();
        initializeComponents();
    }

    private void initializeComponents() {
        View textView = LayoutInflater.from(context).inflate(R.layout.activity_statistic_screen, null);
        numLiveTree = textView.findViewById(R.id.num_live_tree);
        numDeadTree = textView.findViewById(R.id.num_dead_tree);
        totalFocusTimeText = textView.findViewById(R.id.total_focused_time);
        initializeLineChart();
        initializeBarChart();
    }

    private void initializeLineChart() {
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(false);
        lineChart.setDrawGridBackground(false);
        lineChart.getLegend().setEnabled(false);


        // Customize X-axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(ContextCompat.getColor(context, R.color.primary_50));
        xAxis.setTextSize(8f);
        lineChart.setExtraOffsets(20, 0, 20, 0);

        // Disable Y-axis
        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        // Set custom marker view
        ChartUtils.FocusTimeMarkerView mv = new ChartUtils.FocusTimeMarkerView(context, R.layout.custom_marker_view);
        mv.setChartView(lineChart);
        lineChart.setMarker(mv);

        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                lineChart.highlightValue(h);
            }

            @Override
            public void onNothingSelected() {
                lineChart.highlightValue(null);
            }
        });
    }

    private void initializeBarChart() {
        barChart.getDescription().setEnabled(false);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);
        barChart.getLegend().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(ContextCompat.getColor(context, R.color.primary_50));
        xAxis.setTextSize(8f);


        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);

        ChartUtils.FocusTimeMarkerView mv = new ChartUtils.FocusTimeMarkerView(context, R.layout.custom_marker_view);
        mv.setChartView(barChart);
        barChart.setMarker(mv);

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                barChart.highlightValue(h);
            }

            @Override
            public void onNothingSelected() {
                barChart.highlightValue(null);
            }
        });
    }

    public long[] updateFocusTimeCharts(List<Session> sessions, String currentPeriod) {
        List<Entry> lineEntries = new ArrayList<>();
        List<BarEntry> barEntries = new ArrayList<>();
        List<String> xAxisLabels = new ArrayList<>();

        liveTree = 0;
        deadTree = 0;
        totalDuration = 0;

        calculateFocusTimeData(currentPeriod, sessions, lineEntries, barEntries, xAxisLabels);
        updateCharts(lineEntries, barEntries, xAxisLabels);

        return new long[]{liveTree, deadTree, totalDuration};
    }

    public void calculateFocusTimeData(String period, List<Session> sessions, List<Entry> lineEntries, List<BarEntry> barEntries, List<String> xAxisLabels) {
        HashMap<Integer, Float> intervalFocusTime = new HashMap<>();
        LocalDateTime startTime = chartUtils.getStartOfPeriod(period);
        LocalDateTime endTime = chartUtils.getEndOfPeriod(period, startTime);
        int intervalDuration = chartUtils.getIntervalDuration(period);

        for (Session session : sessions) {
            LocalDateTime sessionDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(session.getTimestamp()), ZoneId.systemDefault());
            if (chartUtils.isWithinPeriod(sessionDateTime, startTime, endTime)) {
                int intervalIndex = chartUtils.getIntervalIndex(sessionDateTime, period, intervalDuration);
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

        // Update the statistic screen
        numLiveTree.setText(String.valueOf(liveTree));
        numDeadTree.setText(String.valueOf(deadTree));
        totalFocusTimeText.setText(String.format("%d hours %d min", totalDuration / 3600, (totalDuration % 3600) / 60));

        for (int i = 0; i < chartUtils.getNumInterval(period); i++) {
            float focusTime = intervalFocusTime.getOrDefault(i, 0f);
            lineEntries.add(new Entry(i, focusTime));
            barEntries.add(new BarEntry(i, focusTime));
            xAxisLabels.add(chartUtils.formatIntervalLabel(i, period, intervalDuration));
        }
    }

    private void updateCharts(List<Entry> lineEntries, List<BarEntry> barEntries, List<String> xAxisLabels) {
        boolean allZero = lineEntries.stream().allMatch(entry -> entry.getY() == 0f);

        if (allZero) {
            lineChart.clear();
            barChart.clear();
            lineChart.setNoDataText("No data available");
            barChart.setNoDataText("No data available");
            lineChart.setNoDataTextColor(ContextCompat.getColor(context, R.color.primary_50));
            barChart.setNoDataTextColor(ContextCompat.getColor(context, R.color.primary_50));
            lineChart.invalidate();
            barChart.invalidate();
        } else {
            updateLineChart(lineEntries, xAxisLabels);
            updateBarChart(barEntries, xAxisLabels);
        }
    }

    private void updateLineChart(List<Entry> entries, List<String> xAxisLabels) {
        LineDataSet dataSet = new LineDataSet(entries, "Focus Time");
        dataSet.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
        dataSet.setCircleColor(ContextCompat.getColor(context, R.color.secondary_90));

        dataSet.setDrawValues(false);
        dataSet.setHighlightEnabled(true);
        dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));

        // Animate chart drawing
        lineChart.animateX(2000, Easing.EaseInCubic);
        lineChart.invalidate();
    }

    private void updateBarChart(List<BarEntry> entries, List<String> xAxisLabels) {
        BarDataSet dataSet = new BarDataSet(entries, "Focus Time");
        dataSet.setColor(ContextCompat.getColor(context, R.color.primary_30));
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(10f);
        dataSet.setDrawValues(false);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));

        barChart.animateY(1500, Easing.EaseInCubic);
        barChart.invalidate();
    }
}