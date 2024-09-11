package com.hfad.cs426_final_project.ToDoCalendarScreen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.cs426_final_project.R;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;

class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder>
{
    private final ArrayList<LocalDate> daysList;
    private final OnDayClickListener onDayClickListener;

    public interface OnDayClickListener
    {
        void onDayClick(int position, LocalDate date);
    }

    public CalendarAdapter(ArrayList<LocalDate> daysList, OnDayClickListener onDayClickListener)
    {
        this.daysList = daysList;
        this.onDayClickListener = onDayClickListener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

        if (daysList.size() > 15) // month view
            layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        else // week view
            layoutParams.height = (int) parent.getHeight();

        return new CalendarViewHolder(view, onDayClickListener, daysList);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position)
    {
        final LocalDate date = daysList.get(position);
        holder.bind(date);
    }

    @Override
    public int getItemCount()
    {
        return daysList.size();
    }
}