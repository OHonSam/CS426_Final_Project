package com.hfad.cs426_final_project;

import com.hfad.cs426_final_project.DataStorage.Tag;
import com.hfad.cs426_final_project.DataStorage.Task;
import com.hfad.cs426_final_project.DataStorage.Tree;
import com.hfad.cs426_final_project.DataStorage.UserSetting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class User {
    private long id;
    private String email;
    private String password;
    private String name;
    private long lastAccessDate; // Store date as a timestamp (milliseconds)

    private int streak;
    private UserSetting userSetting;
    private List<Tree> ownTrees;
    private List<Tag> ownTags;
    private List<Task> ownTasks;

    public User() {}

    // Use for adding a new user (when signing up)
    public User(long id, String email, String password, String name) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;

        // default
        this.lastAccessDate = System.currentTimeMillis();
        this.streak = 1;
        this.userSetting = new UserSetting(); // get default settings
        ownTrees = new ArrayList<>();
        ownTags = new ArrayList<>();
        ownTasks = new ArrayList<>();
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

    public long getLastAccessDate() {
        return lastAccessDate;
    }

    public void setLastAccessDate(long lastAccessDate) {
        this.lastAccessDate = lastAccessDate;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public UserSetting getUserSetting() {
        return userSetting;
    }

    public void setUserSetting(UserSetting userSetting) {
        this.userSetting = userSetting;
    }

    public List<Tree> getOwnTrees() {
        return ownTrees;
    }

    public void setOwnTrees(List<Tree> ownTrees) {
        this.ownTrees = ownTrees;
    }

    public List<Tag> getOwnTags() {
        return ownTags;
    }

    public void setOwnTags(List<Tag> ownTags) {
        this.ownTags = ownTags;
    }

    public List<Task> getOwnTasks() {
        return ownTasks;
    }

    public void setOwnTasks(List<Task> ownTasks) {
        this.ownTasks = ownTasks;
    }
}
