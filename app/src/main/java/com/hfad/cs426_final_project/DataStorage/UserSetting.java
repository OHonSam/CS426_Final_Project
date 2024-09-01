package com.hfad.cs426_final_project.DataStorage;

import com.hfad.cs426_final_project.MainScreen.Music.MusicItem;

public class UserSetting {
    private int clockMode;
    private Tree selectedTree;

    public MusicItem getSelectedMusicItem() {
        return selectedMusicItem;
    }

    public void setSelectedMusicItem(MusicItem selectedMusicItem) {
        this.selectedMusicItem = selectedMusicItem;
    }

    private MusicItem selectedMusicItem;

    public UserSetting() {
        // default
        clockMode = 0;
        selectedTree = new Tree();
        selectedMusicItem = new MusicItem();
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
