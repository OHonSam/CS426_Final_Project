package com.hfad.cs426_final_project;

import java.util.Calendar;
import java.util.Date;

public class User {
    private long id;
    private String email;
    private String password;
    private String name;
    private Date lastAccessDate;
    int musicSelectedID;
    int streak;

    public User(){}

    public User(long id, String email, String password, String name) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.lastAccessDate = Calendar.getInstance().getTime();
        this.musicSelectedID = 0;
        this.streak = 1;
    }

    public User(String email, String password, String name, Date lastAccessDate, int musicSelectedID, int streak) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.lastAccessDate = lastAccessDate;
        this.musicSelectedID = musicSelectedID;
        this.streak = streak;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLastAccessDate() {
        return lastAccessDate;
    }

    public void setLastAccessDate(Date lastAccessDate) {
        this.lastAccessDate = lastAccessDate;
    }

    public int getMusicSelectedID() {
        return musicSelectedID;
    }

    public void setMusicSelectedID(int musicSelectedID) {
        this.musicSelectedID = musicSelectedID;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }
}
