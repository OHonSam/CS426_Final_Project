package com.hfad.cs426_final_project.DataStorage;

public class UserTask {
    private int id;
    private String title;
    private String location;
    private long startDate; // Store Date as long (timestamp)
    private long endDate;   // Store Date as long (timestamp)

    private long startTimeInMinutes;

    private long endTimeInMinutes;
    private String description;
//    private Tag tag;

    public UserTask() {}

    public UserTask(int id, String title, long startDate, long endDate, long startTimeInMinutes, long endTimeInMinutes) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTimeInMinutes = startTimeInMinutes;
        this.endTimeInMinutes = endTimeInMinutes;
        this.description = "";
        this.location = "";
    }

    public UserTask(int id, String title, String location, long startDate, long endDate, String description) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getStartTimeInMinutes() {
        return startTimeInMinutes;
    }

    public void setStartTimeInMinutes(long startTimeInMinutes) {
        this.startTimeInMinutes = startTimeInMinutes;
    }

    public long getEndTimeInMinutes() {
        return endTimeInMinutes;
    }

    public void setEndTimeInMinutes(long endTimeInMinutes) {
        this.endTimeInMinutes = endTimeInMinutes;
    }
}
