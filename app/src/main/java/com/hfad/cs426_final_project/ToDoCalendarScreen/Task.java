package com.hfad.cs426_final_project.ToDoCalendarScreen;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Task {
    private LocalDate date;
    private LocalTime time;
    private String title;

    public static ArrayList<Task>  tasksList = new ArrayList<>();

    public static ArrayList<Task> getTasksForDate(LocalDate date) {
        ArrayList<Task> tasks = new ArrayList<>();

        for (Task task : tasksList) {
            if (task.getDate().equals(date)) {
                tasks.add(task);
            }
        }

        return tasks;
    }

    public static ArrayList<Task> getTasksForDateAndTime(LocalDate date, LocalTime time) {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Task task : tasksList) {
            if (task.getDate().equals(date) && task.getTime().getHour() == time.getHour()) {
                tasks.add(task);
            }
        }
        return tasks;
    }

    public Task(LocalDate date, LocalTime time, String title) {
        this.date = date;
        this.time = time;
        this.title = title;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
