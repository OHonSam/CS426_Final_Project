package com.hfad.cs426_final_project.ToDoCalendarScreen;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.cs426_final_project.R;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarViewHolder extends RecyclerView.ViewHolder {
    public final TextView dayOfMonth;
    private final CalendarAdapter.OnDayClickListener listener;
    View parentView;

    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnDayClickListener listener) {
        super(itemView);
        this.parentView = itemView.findViewById(R.id.parentView);
        this.dayOfMonth = itemView.findViewById(R.id.cellDayText);
        this.listener = listener;
    }

    public void bind(LocalDate date) {
        // The bind() method already passes the date corresponding to the current item in the list.
        dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));
        setDayAppearance(date);
        setDayOnClickListener(date);
    }

    private void setDayOnClickListener(LocalDate date) {
        // Enable click listener only if the day is not empty
        itemView.setOnClickListener(v -> {
            if (date!=null) {
                listener.onDayClick(getAdapterPosition(), date);
            }
        });
    }

    private void setDayAppearance(LocalDate date) {
        if(date.equals(CalendarUtils.selectedDate))
            parentView.setBackgroundColor(Color.LTGRAY);

        if(date.getMonth().equals(CalendarUtils.selectedDate.getMonth()))
            dayOfMonth.setTextColor(Color.BLACK);
        else
            dayOfMonth.setTextColor(Color.LTGRAY);
    }
}
