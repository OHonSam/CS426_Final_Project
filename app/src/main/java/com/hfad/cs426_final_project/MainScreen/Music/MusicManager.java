package com.hfad.cs426_final_project.MainScreen.Music;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.R;
import com.hfad.cs426_final_project.User;

public class MusicManager {
    private MediaPlayer mediaPlayer;
    private final Context context;
    private static final String PREFS_NAME = "MusicPrefs";
    private static final String KEY_SELECTED_MUSIC = "SelectedMusic";
    private final ClickableImageView musicImage;
    private int curSavedMusicResId;
    private Uri curSavedMusicUri;

    private final AppContext appContext;

    public MusicManager(Context context, ClickableImageView musicImage) {
        this.context = context;
        this.musicImage = musicImage;
        this.curSavedMusicResId = -1;
        this.curSavedMusicUri = null;
        appContext = AppContext.getInstance();
    }

    public void playMusic(int musicResId) {
        mediaPlayer = MediaPlayer.create(context, musicResId);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        musicImage.setImageResource(R.drawable.ic_music_on);
    }

    public void playMusic(Uri musicUri) {
        mediaPlayer = MediaPlayer.create(context, musicUri);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        musicImage.setImageResource(R.drawable.ic_music_on);
    }

    public void playMusicFromFirebase(String fileName) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://focus-da00f.appspot.com/Musics").child(fileName);

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Play music with the downloaded URI
            playMusic(uri);
        }).addOnFailureListener(exception -> {
            // Handle any errors
            Log.e("MusicManager", "Failed to get download URL: " + exception.getMessage());
        });
    }


    public void releaseMusic() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        musicImage.setImageResource(R.drawable.ic_music_off);
    }

    public void switchMusic(Uri musicUri) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            releaseMusic();
        }
        playMusic(musicUri);
        curSavedMusicUri = musicUri;
    }

    public void switchMusic(int fileResourceId) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            releaseMusic();
        }
        playMusic(fileResourceId);
        curSavedMusicResId = fileResourceId;
    }

    // Save the selected music in SharedPreferences
    public void saveMusicSelection() {
        if (curSavedMusicResId == -1) return; // No music was selected, so don't save
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_SELECTED_MUSIC, curSavedMusicResId);
        editor.apply();

        // set music_selected_id in Users in firebase to curSavedMusicResId
//        AppContext appContext = AppContext.getInstance();
//        String userId = "User" + appContext.getCurrentUser().getId(); // Get the current user's UID
//        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
//        userRef.child("musicSelectedID").setValue(curSavedMusicResId); // curSavedMusicResId is the problem!!
    }

    // Save the selected music Uri in SharedPreferences
    public void saveMusicUriSelection() {
        if (curSavedMusicUri == null) return; // No music was selected, so don't save

        // set music_selected_uri in Users in Firebase to curSavedMusicUri
        User currentUser = appContext.getCurrentUser();
        if (currentUser != null) {
            String userId = "User" + currentUser.getId(); // Get the current user's UID
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
            userRef.child("musicSelectedUri").setValue(curSavedMusicUri.toString());
        }
    }



//    // Load the saved music selection from SharedPreferences
//    public int loadSavedMusicSelection() {
//        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//        return prefs.getInt(KEY_SELECTED_MUSIC, -1);  // Return -1 if no music was saved
//    }

    // Load the saved music Uri from SharedPreferences
    public Uri loadSavedMusicSelection() {
//        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//        String uriString = prefs.getString(KEY_SELECTED_MUSIC_URI, null);
//        return uriString != null ? Uri.parse(uriString) : null;
        return Uri.parse("gs://focus-da00f.appspot.com/Musics/forest_rain.mp3");
    }

    public void toggleOnOff() {
        if (isPlaying()) {
            releaseMusic();
        } else {
//            Uri savedMusicUri = loadSavedMusicSelection();
//            if (savedMusicUri != null) {
//                playMusic(savedMusicUri);
//            } else {
                // Play default music if no saved music selection
                playMusicFromFirebase("forest_rain.mp3");
//            }
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }
}
