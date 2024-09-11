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
    private final ArrayList<LocalDate> daysList;
    public final TextView dayOfMonth;
    private final CalendarAdapter.OnDayClickListener listener;
    private View parentView;

    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnDayClickListener listener, ArrayList<LocalDate> daysList) {
        super(itemView);
        this.parentView = itemView.findViewById(R.id.parentView);
        this.dayOfMonth = itemView.findViewById(R.id.cellDayText);
        this.daysList = daysList;
        this.listener = listener;
    }

    public void bind(LocalDate date) {
        if (date == null) {
            dayOfMonth.setText("");
        } else {
            dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));
            if (date.equals(CalendarUtils.selectedDate)) {
                // Use ContextCompat to get the color value from the resource
                parentView.setBackgroundColor(ContextCompat.getColor(parentView.getContext(), R.color.secondary_50));
            } else {
                // Optionally reset the background color for non-selected dates
                parentView.setBackgroundColor(Color.TRANSPARENT);  // Or another default color
            }
        }

        // Enable click listener only if the day is not empty
        itemView.setOnClickListener(v -> {
            if (date!=null) {
                listener.onDayClick(getAdapterPosition(), daysList.get(getAdapterPosition()));
            }
        });

    }
}
