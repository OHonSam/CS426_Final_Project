package com.hfad.cs426_final_project.StatisticScreen;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimeManager {
    private Calendar currentDate;
    private String currentPeriod;

    public String getCurrentPeriod() {
        return currentPeriod;
    }

    public Calendar getCurrentDate() {
        return currentDate;
    }

    public TimeManager() {
        currentDate = Calendar.getInstance();
        currentPeriod = "Day";
    }

    public void handleSelection(String period) {
        currentPeriod = period;
        currentDate = Calendar.getInstance();
    }

    public void navigateTime(int direction) {
        switch (currentPeriod) {
            case "Day":
                currentDate.add(Calendar.DAY_OF_YEAR, direction);
                break;
            case "Week":
                currentDate.add(Calendar.WEEK_OF_YEAR, direction);
                break;
            case "Month":
                currentDate.add(Calendar.MONTH, direction);
                break;
            case "Year":
                currentDate.add(Calendar.YEAR, direction);
                break;
        }
    }

    public String getFormattedTimeSelection() {
        SimpleDateFormat sdf;
        String dateStr;
        Calendar now = Calendar.getInstance();

        switch (currentPeriod) {
            case "Day":
                sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.UK);
                dateStr = sdf.format(currentDate.getTime());
                if (isSameDay(currentDate, now)) {
                    dateStr += " (Today)";
                }
                break;
            case "Week":
                sdf = new SimpleDateFormat("MMM d", Locale.UK);
                Calendar endOfWeek = (Calendar) currentDate.clone();
                endOfWeek.add(Calendar.DAY_OF_WEEK, 6);
                dateStr = sdf.format(currentDate.getTime()) + " - " + sdf.format(endOfWeek.getTime()) + ", " + currentDate.get(Calendar.YEAR);
                break;
            case "Month":
                sdf = new SimpleDateFormat("MMMM yyyy", Locale.UK);
                dateStr = sdf.format(currentDate.getTime());
                break;
            case "Year":
                dateStr = String.valueOf(currentDate.get(Calendar.YEAR));
                break;
            default:
                dateStr = "";
        }

        return dateStr;
    }

    public boolean isLatestTime() {
        Calendar now = Calendar.getInstance();
        switch (currentPeriod) {
            case "Day":
                return isSameDay(currentDate, now);
            case "Week":
                return currentDate.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                        currentDate.get(Calendar.WEEK_OF_YEAR) >= now.get(Calendar.WEEK_OF_YEAR);
            case "Month":
                return currentDate.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                        currentDate.get(Calendar.MONTH) >= now.get(Calendar.MONTH);
            case "Year":
                return currentDate.get(Calendar.YEAR) >= now.get(Calendar.YEAR);
            default:
                return false;
        }
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
}