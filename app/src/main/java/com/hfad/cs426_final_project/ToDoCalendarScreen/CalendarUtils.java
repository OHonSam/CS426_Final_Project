package com.hfad.cs426_final_project.ToDoCalendarScreen;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class CalendarUtils {
    public static LocalDate selectedDate;

    public static ArrayList<LocalDate> generateDaysInMonthArray(LocalDate date) {
        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        // Returns the number of days in the month (i.e., 28, 30, or 31).
        int daysInMonthCount = yearMonth.lengthOfMonth();
        // For example, if selectedDate is September 15, 2024, this will return September 1, 2024.
        LocalDate firstDayOfMonth = CalendarUtils.selectedDate.withDayOfMonth(1);
        // Returns the day of the week (i.e., 1 for Monday, 7 for Sunday) for the first day of the month.
        int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue();
        // By determining which day of the week the 1st day of the month falls on (via dayOfWeek),
        // the function knows how many empty cells ("") it needs to insert before the first actual day of the month appears.

        // 6x7 (6 weeks x 7 days/week)
        for (int i = 1; i <= 42; i++) {
            if (i <= dayOfWeek || i > daysInMonthCount + dayOfWeek) {
                // "padding" days from the previous month, which ensures that the 1st day of the current month starts in its correct position.
                // "padding" days from the next month, which ensures that the last day of the current month ends in its correct position.
                daysInMonthArray.add(null);
            } else {
                daysInMonthArray.add(LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), i - dayOfWeek));
            }
        }

        return daysInMonthArray;
    }

    public static ArrayList<LocalDate> generateDaysInWeekArray(LocalDate date) {
        ArrayList<LocalDate> daysInWeekArray = new ArrayList<>();
        LocalDate current = sundayForDate(date);
        LocalDate endDate = current.plusWeeks(1);
        while (current.isBefore(endDate)) {
            daysInWeekArray.add(current);
            current = current.plusDays(1);
        }
        return daysInWeekArray;
    }

    private static LocalDate sundayForDate(LocalDate date) {
        LocalDate oneWeekAgo = date.minusWeeks(1);
        while (date.isAfter(oneWeekAgo)) {
            if (date.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
                return date;
            }
            date = date.minusDays(1);
        }
        return null;
    }

    public static String getMonthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
        return date.format(formatter);
    }
}
