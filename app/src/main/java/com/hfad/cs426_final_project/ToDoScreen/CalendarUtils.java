package com.hfad.cs426_final_project.ToDoScreen;

import java.util.Calendar;
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
}
