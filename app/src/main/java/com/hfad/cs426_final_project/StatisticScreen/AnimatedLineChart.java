package com.hfad.cs426_final_project.StatisticScreen;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.hfad.cs426_final_project.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class AnimatedLineChart extends FrameLayout {

    private LineChart lineChart;
    private TextView noDataText;

    public AnimatedLineChart(@NonNull Context context) {
        super(context);
        init(context);
    }

    public AnimatedLineChart(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AnimatedLineChart(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.line_chart, this, true);
        lineChart = view.findViewById(R.id.lineChart);
        noDataText = view.findViewById(R.id.noDataText);
        setupChart();
    }

    private void setupChart() {
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setDrawGridBackground(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        lineChart.getAxisLeft().setDrawGridLines(true);
        lineChart.getAxisRight().setEnabled(false);

        lineChart.getLegend().setEnabled(false);

        lineChart.animateX(1500);
    }

    public void updateChart(String period, Calendar startDate) {
        List<Entry> entries = generateSampleData(period, startDate);
        if (entries.isEmpty()) {
            showNoDataMessage();
        } else {
            showChart(entries);
        }
    }

    private List<Entry> generateSampleData(String period, Calendar startDate) {
        List<Entry> entries = new ArrayList<>();
        Random random = new Random(System.currentTimeMillis());

        switch (period) {
            case "Day":
                for (int hour = 0; hour <= 23; hour++) {
                    entries.add(new Entry(hour, random.nextInt(100)));
                }
                break;
            case "Week":
                for (int day = 0; day <= 6; day++) {
                    entries.add(new Entry(day, random.nextInt(100)));
                }
                break;
            case "Month":
                int daysInMonth = startDate.getActualMaximum(Calendar.DAY_OF_MONTH);
                for (int day = 1; day <= daysInMonth; day++) {
                    entries.add(new Entry(day, random.nextInt(100)));
                }
                break;
            case "Year":
                for (int month = 0; month <= 11; month++) {
                    entries.add(new Entry(month, random.nextInt(100)));
                }
                break;
        }

        return entries;
    }

    private void showNoDataMessage() {
        lineChart.setVisibility(GONE);
        noDataText.setVisibility(VISIBLE);
    }

    private void showChart(List<Entry> entries) {
        lineChart.setVisibility(VISIBLE);
        noDataText.setVisibility(GONE);

        LineDataSet dataSet = new LineDataSet(entries, "Sample Data");
        dataSet.setColor(getContext().getColor(R.color.blue_deep_sea));
        dataSet.setCircleColor(getContext().getColor(R.color.blue_deep_sea));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(9f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(getContext().getColor(R.color.primary_20));

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }
}