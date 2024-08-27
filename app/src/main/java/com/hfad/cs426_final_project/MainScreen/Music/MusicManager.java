package com.hfad.cs426_final_project.MainScreen.Music;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.R;

public class MusicManager {
    private MediaPlayer mediaPlayer;
    private final Context context;
    private static final String PREFS_NAME = "MusicPrefs";
    private static final String KEY_SELECTED_MUSIC = "SelectedMusic";
    private final ClickableImageView musicImage;

    public MusicManager(Context context, ClickableImageView musicImage) {
        this.context = context;
        this.musicImage = musicImage;
    }

    public void playMusic(int musicResId) {
        mediaPlayer = MediaPlayer.create(context, musicResId);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        musicImage.setImageResource(R.drawable.ic_music_on);
    }

    public void releaseMusic() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        musicImage.setImageResource(R.drawable.ic_music_off);
    }

    public void switchMusic(int fileResourceId) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            releaseMusic();
        }
        playMusic(fileResourceId);
        saveMusicSelection(fileResourceId);
    }

    // Save the selected music in SharedPreferences
    private void saveMusicSelection(int musicResId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_SELECTED_MUSIC, musicResId);
        editor.apply();
    }

    // Load the saved music selection from SharedPreferences
    public int loadMusicSelection() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_SELECTED_MUSIC, -1);  // Return -1 if no music was saved
    }

    public void toggleOnOff() {
        if (isPlaying()) {
            releaseMusic();
        } else {
            int savedMusicResId = loadMusicSelection();
            playMusic(savedMusicResId == -1 ? R.raw.forest_rain : savedMusicResId);
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }
}
