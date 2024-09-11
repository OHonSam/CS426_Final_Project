package com.hfad.cs426_final_project.DataStorage;

public class Tag {
    private int id;
    private String name;
    private int color;

    public Tag() {
        this.id = 0;
        this.name = "None";
        this.color = 0xFF000000;
    }

    public Tag(int id, String name, int color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Tag(int id) {
        this.id = id;
        if (id == 1) {
            this.name = "Work";
            this.color = 0xFF4285F4;
        } else if (id == 2) {
            this.name = "Study";
            this.color = 0xFFFFA500;
        } else if (id == 3) {
            this.name = "Exercise";
            this.color = 0xFF34A853;
        } else if (id == 4) {
            this.name = "Relax";
            this.color = 0xFFEA4335;
        } else if (id == 5) {
            this.name = "Sleep";
            this.color = 0xFF9C27B0;
        } else {
            this.name = "None";
            this.color = 0xFF000000;
        }
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean sameID(Tag tag) {
        return this.id == tag.id;
    }
}
