package com.hfad.cs426_final_project.MainScreen.Music;

import android.content.Context;
import android.media.MediaPlayer;

public class MusicManager {
    private MediaPlayer mediaPlayer;
    private Context context;

    public MusicManager(Context context) {
        this.context = context;
    }

    public void playMusic(int musicResId) {
        mediaPlayer = MediaPlayer.create(context, musicResId);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    public void releaseMusic() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public void switchMusic(int fileResourceId) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            releaseMusic();
        }
        playMusic(fileResourceId);
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }
}
