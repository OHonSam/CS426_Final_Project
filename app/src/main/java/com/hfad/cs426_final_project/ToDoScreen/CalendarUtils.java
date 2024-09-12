package com.hfad.cs426_final_project.ToDoScreen;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarUtils {
    public static long convertDateToMillis(String date) {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(date));
            return calendar.getTimeInMillis();
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Invalid date format
        }
    }

    // Helper method to convert time (HH:mm) to minutes
    public static long convertTimeToMinutes(String time) {
        try {
            String[] timeParts = time.split(":");
            long hours = Integer.parseInt(timeParts[0]);
            long minutes = Integer.parseInt(timeParts[1]);
            return hours * 60 + minutes; // Total minutes since midnight
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Invalid time format
        }
    }

    public static String convertMillisToDate(long dateInMillis) {
        // Format the long value to a human-readable date string
        // e.g., "12/09/2024"
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date(dateInMillis));
    }

    public static String convertMinutesToTimeFormat(long timeInMinutes) {
        // Convert time from minutes to "HH:mm" format
        long hours = timeInMinutes / 60;
        long minutes = timeInMinutes % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
    }
}
