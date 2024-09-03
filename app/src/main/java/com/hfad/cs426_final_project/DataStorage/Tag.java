package com.hfad.cs426_final_project.DataStorage;

public class Tag {
    private int id;
    private String name;

    public Tag() {
        this.id = 0;
        this.name = "none";
    }

    public Tag(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
