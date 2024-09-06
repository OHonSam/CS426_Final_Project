package com.hfad.cs426_final_project.DataStorage;

public class Tag {
    private int id;
    private String name;
    private String colorHex;

    public Tag() {
        this.id = 0;
        this.name = "none";
        this.colorHex = "#FFFFFFFF";
    }

    public Tag(int id, String name, String colorHex) {
        this.id = id;
        this.name = name;
        this.colorHex = colorHex;
    }

    public Tag(int id) {
        this.id = id;
        if (id == 1) {
            this.name = "Work";
        } else if (id == 2) {
            this.name = "Study";
        } else if (id == 3) {
            this.name = "Exercise";
        } else if (id == 4) {
            this.name = "Relax";
        } else if (id == 5) {
            this.name = "Sleep";
        } else {
            this.name = "Other";
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

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    public boolean sameID(Tag tag) {
        return this.id == tag.id;
    }
}