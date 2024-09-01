package com.hfad.cs426_final_project.MainScreen.Music;

import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.R;

import java.util.ArrayList;
import java.util.List;

public class MusicItem {
    private int id;
    private final String title;
    private final int fileResourceId; // resource ID of the music file in the raw folder
    private String audioUri; // URI for the audio file

    public MusicItem() {
        this.id = 0;
        this.title = "Forest Rain";
        this.fileResourceId = R.raw.forest_rain;
        this.audioUri = "gs://focus-da00f.appspot.com/forest_rain.mp3";
    }

    public MusicItem(String title, int fileResourceId) {
        this.title = title;
        this.fileResourceId = fileResourceId;
    }

    public MusicItem(int id, String title, int fileResourceId) {
        this.id = id;
        this.title = title;
        this.fileResourceId = fileResourceId;
    }

    public MusicItem(int id, String title, int fileResourceId, String audioUri) {
        this.id = id;
        this.title = title;
        this.fileResourceId = fileResourceId;
        this.audioUri = audioUri;
    }

    public static List<MusicItem> getMusicList() {
//        List<MusicItem> musicList = new ArrayList<>();
//        musicList.add(new MusicItem("Forest Rain", R.raw.forest_rain));
//        musicList.add(new MusicItem("Ocean Waves", R.raw.ocean_waves));
//        return musicList;

        return AppContext.getInstance().getMusicList();
    }

    public String getTitle() {
        return title;
    }

    public int getFileResourceId() {
        return fileResourceId;
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
