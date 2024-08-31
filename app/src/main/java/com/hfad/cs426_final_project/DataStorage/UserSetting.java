package com.hfad.cs426_final_project.DataStorage;

public class UserSetting {
    private int clockMode;
    //private int musicSelectedID;
    private Tree selectedTree;

    public UserSetting() {
        // default
        clockMode = 0;
        selectedTree = new Tree();
    }

    public int getClockMode() {
        return clockMode;
    }

    public void setClockMode(int clockMode) {
        this.clockMode = clockMode;
    }

    public Tree getSelectedTree() {
        return selectedTree;
    }

    public void setSelectedTree(Tree selectedTree) {
        this.selectedTree = selectedTree;
    }
}
