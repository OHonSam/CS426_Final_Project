package com.hfad.cs426_final_project.DataStorage;

public class UserSetting {
    private Tree selectedTree;

    public UserSetting() {
        // default
        selectedTree = new Tree();
    }

    public Tree getSelectedTree() {
        return selectedTree;
    }

    public void setSelectedTree(Tree selectedTree) {
        this.selectedTree = selectedTree;
    }
}
