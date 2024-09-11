package com.hfad.cs426_final_project.ToDoCalendarScreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.cs426_final_project.BaseScreenActivity;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.R;

import java.time.LocalDate;
import java.util.ArrayList;

import static com.hfad.cs426_final_project.ToDoCalendarScreen.CalendarUtils.generateDaysInMonthArray;
import static com.hfad.cs426_final_project.ToDoCalendarScreen.CalendarUtils.getMonthYearFromDate;

public class ToDoScreenActivity extends BaseScreenActivity implements CalendarAdapter.OnDayClickListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private ImageButton arrowBackwards, arrowForwards;
    private MyButton btnSwitchWeeklyMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWidgets();
        CalendarUtils.selectedDate = LocalDate.now();
        setMonthView();
        setupCalendarArrowListener();
        setupSwitchWeeklyModeListener();
    }

    private void setupSwitchWeeklyModeListener() {
        btnSwitchWeeklyMode.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                                                       switchWeeklyMode();
                                                   }
                                               });
    }

    private void switchWeeklyMode() {
        Intent intent = new Intent(this, WeekViewActivity.class);
        startActivity(intent);
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
        monthYearText.setText(getMonthYearFromDate(CalendarUtils.selectedDate));

        ArrayList<LocalDate> daysInMonth = generateDaysInMonthArray(CalendarUtils.selectedDate);

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
        btnSwitchWeeklyMode = findViewById(R.id.btnSwitchWeeklyMode);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_to_do_screen;
    }

    public void previousMonthAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
        setMonthView();
    }

    public void nextMonthAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
        setMonthView();
    }

    @Override
    public void onDayClick(int position, LocalDate date) {
        if (date != null) {
            CalendarUtils.selectedDate = date;
            setMonthView();
        }
    }
}