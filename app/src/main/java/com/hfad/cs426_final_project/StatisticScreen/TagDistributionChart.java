package com.hfad.cs426_final_project.StatisticScreen;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.hfad.cs426_final_project.DataStorage.Session;
import com.hfad.cs426_final_project.R;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagDistributionChart {
    private Context context;
    private PieChart pieChart;
    private LinearLayout tagListLayout;
    private ChartUtils chartUtils;

    public TagDistributionChart(Context context, PieChart pieChart, LinearLayout tagListLayout) {
        this.context = context;
        this.pieChart = pieChart;
        this.tagListLayout = tagListLayout;
        this.chartUtils = new ChartUtils();
        initializeChart();
    }

    private void initializeChart() {
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.getLegend().setEnabled(false);
    }

    public void updateTagDistributionChart(List<Session> sessions, String period, Map<String, Long> tagDurations, Map<String, Integer> tagColors) {
        List<Session> filteredSessions = calculateTagDistributionData(period, sessions);
        updateChart(filteredSessions, tagDurations, tagColors);
    }

    private List<Session> calculateTagDistributionData(String period, List<Session> sessions) {
        List<Session> filteredSessions = new ArrayList<>();
        LocalDateTime startTime = chartUtils.getStartOfPeriod(period);
        LocalDateTime endTime = chartUtils.getEndOfPeriod(period, startTime);

        for (Session session : sessions) {
            if (!session.isStatus()) {
                continue;
            }

            LocalDateTime sessionDateTime = LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(session.getTimestamp()), java.time.ZoneId.systemDefault());
            if (chartUtils.isWithinPeriod(sessionDateTime, startTime, endTime)) {
                filteredSessions.add(session);
            }
        }
        return filteredSessions;
    }

    private void updateChart(List<Session> sessions, Map<String, Long> tagDurations, Map<String, Integer> tagColors) {
        long totalDuration = 0;

        for (Session session : sessions) {
            String tagName = session.getTag().getName();
            long duration = session.getDuration();
            tagDurations.put(tagName, tagDurations.getOrDefault(tagName, 0L) + duration);
            totalDuration += duration;

            if (!tagColors.containsKey(tagName)) {
                tagColors.put(tagName, session.getTag().getColor());
            }
        }

        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        tagListLayout.removeAllViews();

        for (Map.Entry<String, Long> entry : tagDurations.entrySet()) {
            float percentage = (float) entry.getValue() / totalDuration * 100;
            entries.add(new PieEntry(percentage, entry.getKey()));
            int color = tagColors.get(entry.getKey());
            colors.add(color);

            addTagItem(tagListLayout, entry.getKey(), color, entry.getValue(), totalDuration);
        }

        PieDataSet dataSet = new PieDataSet(entries, "Tag Distribution");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueFormatter(new PercentFormatter());

        pieChart.setData(new PieData(dataSet));
        pieChart.setRotationAngle(270f);
        pieChart.setRotationEnabled(false);
        pieChart.animateY(1500, Easing.EaseInOutCubic);
        pieChart.invalidate();
    }

    private void addTagItem(LinearLayout tagListLayout, String tagName, int color, long duration, long totalDuration) {
        View tagItem = LayoutInflater.from(context).inflate(R.layout.statistic_tag_item, tagListLayout, false);
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
}