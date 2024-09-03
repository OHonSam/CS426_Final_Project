package com.hfad.cs426_final_project;

import com.hfad.cs426_final_project.DataStorage.Tag;
import com.hfad.cs426_final_project.DataStorage.UserTask;
import com.hfad.cs426_final_project.DataStorage.Tree;
import com.hfad.cs426_final_project.DataStorage.UserSetting;

import java.util.ArrayList;
import java.util.List;

public class User {
    private long id;
    private String email;
    private String password;
    private String name;
    private long lastAccessDate; // Store date as a timestamp (milliseconds)

    private int streak;
    private int sun; // money
    private UserSetting userSetting;
    private List<Tree> ownTrees = new ArrayList<>();
    private List<Tag> ownTags = new ArrayList<>();
    private List<UserTask> ownUserTasks = new ArrayList<>();

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
        this.sun = 0;
        this.userSetting = new UserSetting(); // get default settings

        Tree tree = new Tree(); // default tree
        ownTrees.add(tree);
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

    public int getSun() {
        return sun;
    }

    public void setSun(int sun) {
        this.sun = sun;
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

    public List<UserTask> getOwnTasks() {
        return ownUserTasks;
    }

    public void setOwnTasks(List<UserTask> ownUserTasks) {
        this.ownUserTasks = ownUserTasks;
    }

    public boolean hasTree(Tree tree) {
        if(ownTrees == null)
            return false;
        List<Integer> listOwnTreeID = new ArrayList<>();
        for(Tree t : ownTrees) {
            listOwnTreeID.add(t.getId());
        }
        return listOwnTreeID.contains(tree.getId());
    }

    public boolean hasTag(String tagName) {
        if(ownTags == null)
            return false;
        List<String> listTag = new ArrayList<>();
        for(Tag t : ownTags) {
            listTag.add(t.getName());
        }
        return listTag.contains(tagName);
    }
}
