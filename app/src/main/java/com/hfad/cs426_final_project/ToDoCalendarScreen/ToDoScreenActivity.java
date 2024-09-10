package com.hfad.cs426_final_project.ToDoCalendarScreen;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.cs426_final_project.BaseScreenActivity;
import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.R;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class ToDoScreenActivity extends BaseScreenActivity implements CalendarAdapter.OnDayClickListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;
    private ImageButton arrowBackwards, arrowForwards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWidgets();
        selectedDate = LocalDate.now();
        setMonthView();

        setupCalendarArrowListener();
    }

    private void setupCalendarArrowListener() {
        arrowBackwards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousMonthAction(v);
            }
        });
        arrowForwards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMonthAction(v);
            }
        });
    }

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDate));

        ArrayList<String> daysInMonth = generateDaysInMonthArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private void initWidgets() {
        monthYearText = findViewById(R.id.tvMonthYear_ToDoScreen);
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        arrowBackwards = findViewById(R.id.arrowBackwards_ToDoScreen);
        arrowForwards = findViewById(R.id.arrowForwards_ToDoScreen);
    }

    private ArrayList<String> generateDaysInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        // Returns the number of days in the month (i.e., 28, 30, or 31).
        int daysInMonthCount = yearMonth.lengthOfMonth();
        // For example, if selectedDate is September 15, 2024, this will return September 1, 2024.
        LocalDate firstDayOfMonth = selectedDate.withDayOfMonth(1);
        // Returns the day of the week (i.e., 1 for Monday, 7 for Sunday) for the first day of the month.
        int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue();
        // By determining which day of the week the 1st day of the month falls on (via dayOfWeek),
        // the function knows how many empty cells ("") it needs to insert before the first actual day of the month appears.

        // 6x7 (6 weeks x 7 days/week)
        for (int i = 1; i <= 42; i++) {
            if (i <= dayOfWeek || i > daysInMonthCount + dayOfWeek) {
                // "padding" days from the previous month, which ensures that the 1st day of the current month starts in its correct position.
                // "padding" days from the next month, which ensures that the last day of the current month ends in its correct position.
                daysInMonthArray.add("");
            } else {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }
        }

        return daysInMonthArray;
    }

    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
        return date.format(formatter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_to_do_screen;
    }

    public void previousMonthAction(View view) {
        selectedDate = selectedDate.minusMonths(1);
        setMonthView();

    }

    public void nextMonthAction(View view) {
        selectedDate = selectedDate.plusMonths(1);
        setMonthView();

    }

    @Override
    public void onDayClick(int position, String dayText) {
        if (!dayText.isEmpty()) {
            String message = "Selected Date " + dayText + " " + monthYearFromDate(selectedDate);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
}