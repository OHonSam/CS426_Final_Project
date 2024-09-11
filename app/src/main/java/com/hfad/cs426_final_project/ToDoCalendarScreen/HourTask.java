package com.hfad.cs426_final_project.ToDoCalendarScreen;

import java.time.LocalTime;
import java.util.ArrayList;

public class HourTask {
    private LocalTime time;

    private ArrayList<Task> hourTasksList;
    public HourTask(LocalTime time, ArrayList<Task> hourTasksList) {
        this.time = time;
        this.hourTasksList = hourTasksList;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public ArrayList<Task> getHourTasksList() {
        return hourTasksList;
    }

    public void setHourTasksList(ArrayList<Task> hourTasksList) {
        this.hourTasksList = hourTasksList;
    }
}
