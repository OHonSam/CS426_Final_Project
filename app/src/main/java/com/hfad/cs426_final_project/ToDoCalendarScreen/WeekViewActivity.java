package com.hfad.cs426_final_project.ToDoCalendarScreen;

import static com.hfad.cs426_final_project.ToDoCalendarScreen.CalendarUtils.generateDaysInMonthArray;
import static com.hfad.cs426_final_project.ToDoCalendarScreen.CalendarUtils.generateDaysInWeekArray;
import static com.hfad.cs426_final_project.ToDoCalendarScreen.CalendarUtils.getMonthYearFromDate;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.cs426_final_project.R;

import java.time.LocalDate;
import java.util.ArrayList;

public class WeekViewActivity extends AppCompatActivity implements CalendarAdapter.OnDayClickListener {
    private ImageButton arrowBackwards, arrowForwards;
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view);
        initWidgets();
        
        setupCalendarArrowListener();
        setWeekView();
    }

    private void setWeekView() {
        monthYearText.setText(getMonthYearFromDate(CalendarUtils.selectedDate));

        ArrayList<LocalDate> daysInMonth = generateDaysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private void setupCalendarArrowListener() {
        arrowBackwards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousWeekAction(v);
            }
        });
        arrowForwards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextWeekAction(v);
            }
        });
    }

    private void nextWeekAction(View v) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setWeekView();
    }

    private void previousWeekAction(View v) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setWeekView();
    }

    private void initWidgets() {
        monthYearText = findViewById(R.id.tvMonthYear_ToDoScreen);
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        arrowBackwards = findViewById(R.id.arrowBackwards_WeekView);
        arrowForwards = findViewById(R.id.arrowForwards_WeekView);

    }

    @Override
    public void onDayClick(int position, LocalDate date) {
        if (date != null) {
            CalendarUtils.selectedDate = date;
            setWeekView();
        }
    }
}