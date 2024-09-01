package com.hfad.cs426_final_project.MainScreen.Music;

import com.hfad.cs426_final_project.R;

import java.util.ArrayList;
import java.util.List;

public class MusicItem {
    private String title;
    private int fileResourceId; // resource ID of the music file in the raw folder

    public MusicItem(String title, int fileResourceId) {
        this.title = title;
        this.fileResourceId = fileResourceId;
    }

    public static List<MusicItem> getMusicList() {
        List<MusicItem> musicList = new ArrayList<>();
        musicList.add(new MusicItem("Forest Rain", R.raw.forest_rain));
        musicList.add(new MusicItem("Ocean Waves", R.raw.ocean_waves));
        return musicList;
    }

    public String getTitle() {
        return title;
    }

    public int getFileResourceId() {
        return fileResourceId;
    }
}
