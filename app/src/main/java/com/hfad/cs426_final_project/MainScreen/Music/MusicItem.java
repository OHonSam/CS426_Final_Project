package com.hfad.cs426_final_project.MainScreen.Music;

import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.R;

import java.util.ArrayList;
import java.util.List;

public class MusicItem {
    private int id;
    private final String title;
    private String audioUri; // URI for the audio file

    public MusicItem() {
        this.id = 0;
        this.title = "Forest Rain";
        this.audioUri = "gs://focus-da00f.appspot.com/forest_rain.mp3";
    }

    public MusicItem(int id, String title, String audioUri) {
        this.id = id;
        this.title = title;
        this.audioUri = audioUri;
    }

    public static List<MusicItem> getMusicList() {
        return AppContext.getInstance().getMusicList();
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAudioUri() {
        return audioUri;
    }

    public void setAudioUri(String audioUri) {
        this.audioUri = audioUri;
    }
}
