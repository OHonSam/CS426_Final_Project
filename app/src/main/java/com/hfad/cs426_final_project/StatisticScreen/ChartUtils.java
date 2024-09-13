package com.hfad.cs426_final_project.StatisticScreen;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.hfad.cs426_final_project.R;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ChartUtils {

    private TimeManager timeManager;
    private int numHourIntervals = 4;
    private int numDayIntervals = 7;

    public ChartUtils() {
        this.timeManager = TimeManager.getInstance();
    }

    public LocalDateTime getStartOfPeriod(String period) {
       LocalDateTime currentDate = timeManager.getCurrentDateTime();

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

    public LocalDateTime getEndOfPeriod(String period, LocalDateTime start) {
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

    public boolean isWithinPeriod(LocalDateTime dateTime, LocalDateTime start, LocalDateTime end) {
        return (dateTime.isEqual(start) || dateTime.isAfter(start)) && dateTime.isBefore(end);
    }

    public int getIntervalIndex(LocalDateTime dateTime, String period, int intervalDuration) {
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

    public int getIntervalDuration(String period) {
        switch (period) {
            case "Day":
                return 24 / numHourIntervals;
            case "Month":
                return timeManager.getCurrentDateTime().toLocalDate().lengthOfMonth() / numDayIntervals;
            default:
                return 1;
        }
    }

    public int getNumInterval(String period) {
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

    public String formatIntervalLabel(int interval, String period, int intervalDuration) {
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

    public static class FocusTimeMarkerView extends MarkerView {
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