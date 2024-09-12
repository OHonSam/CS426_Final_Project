package com.hfad.cs426_final_project.ToDoCalendarScreen;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class CalendarUtils {
    public static LocalDate selectedDate;

    public static String getFormattedDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH));
    }

    public static String getFormattedTime(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH));
    }

    public static String getMonthYearFromDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH));
    }

    public static String getMonthDayFromDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("MMMM d", Locale.ENGLISH));
    }

    public static String getFormattedShortTime(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public static ArrayList<LocalDate> generateDaysInMonthArray() {
        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();

        LocalDate prevMonth = selectedDate.minusMonths(1);
        LocalDate nextMonth = selectedDate.plusMonths(1);

        YearMonth curYearMonth = YearMonth.from(selectedDate);
        int curNumDaysInMonth = curYearMonth.lengthOfMonth();

        YearMonth prevYearMonth = YearMonth.from(prevMonth);
        int prevNumDaysInMonth = prevYearMonth.lengthOfMonth();

        LocalDate firstDateOfMonth = selectedDate.withDayOfMonth(1);
        int firstDayOfWeek = firstDateOfMonth.getDayOfWeek().getValue(); //  The day of the week (1-7, Monday-Sunday) of the firstDateOfMonth.

        for(int i = 1; i <= 42; i++) {
            if(i <= firstDayOfWeek)
                daysInMonthArray.add(LocalDate.of(prevMonth.getYear(),prevMonth.getMonth(),prevNumDaysInMonth + i - firstDayOfWeek));
            else if(i > curNumDaysInMonth + firstDayOfWeek)
                daysInMonthArray.add(LocalDate.of(nextMonth.getYear(),nextMonth.getMonth(),i - firstDayOfWeek - curNumDaysInMonth));
            else
                daysInMonthArray.add(LocalDate.of(selectedDate.getYear(),selectedDate.getMonth(),i - firstDayOfWeek));
        }
        return  daysInMonthArray;
    }

    public static ArrayList<LocalDate> generateDaysInWeekArray(LocalDate date) {
        ArrayList<LocalDate> daysInWeekArray = new ArrayList<>();
        LocalDate currentDateIterator = sundayForDate(date);
        LocalDate endDate = currentDateIterator.plusWeeks(1);
        while (currentDateIterator.isBefore(endDate)) {
            daysInWeekArray.add(currentDateIterator);
            currentDateIterator = currentDateIterator.plusDays(1);
        }
        return daysInWeekArray;
    }

    private static LocalDate sundayForDate(LocalDate date) {
        while (date.getDayOfWeek() != java.time.DayOfWeek.SUNDAY) {
            date = date.minusDays(1);
        }
        return date;
    }
}
