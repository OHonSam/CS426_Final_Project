package com.hfad.cs426_final_project.MainScreen.Music;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.R;

public class MusicManager {
    private MediaPlayer mediaPlayer;
    private final Context context;
    private static final String PREFS_NAME = "MusicPrefs";
    private static final String KEY_SELECTED_MUSIC = "SelectedMusic";
    private final ClickableImageView musicImage;
    private MusicItem curSavedMusicItem;

    private String cachedAudioUrl;

    private final AppContext appContext;

    public MusicManager(Context context, ClickableImageView musicImage) {
        this.context = context;
        this.musicImage = musicImage;
        this.curSavedMusicItem = null;
        appContext = AppContext.getInstance();
    }

    public void playMusic(Uri musicUri) {
        mediaPlayer = MediaPlayer.create(context, musicUri);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        musicImage.setImageResource(R.drawable.ic_music_on);
    }

    public void playMusicFromFirebase(String musicUri) {
        if (cachedAudioUrl != null && musicUri.equals(cachedAudioUrl)) {
            playMusic(Uri.parse(cachedAudioUrl));
            return;
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(musicUri);

        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                cachedAudioUrl = uri.toString(); // Cache the URL
                // Play music with the downloaded URI
                playMusic(uri);
            }
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

//    public void switchMusic(MusicItem musicItem) {
//        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//            releaseMusic();
//        }
//        playMusicFromFirebase(musicItem);
//        curSavedMusicItem = musicItem;
//    }
//
//    public void saveMusicItemSelection() {
//        if (curSavedMusicItem == null) return; // No music was selected, so don't save
//
//        // set music_selected_uri in Users in Firebase to curSavedMusicUri
//        User currentUser = appContext.getCurrentUser();
//        if (currentUser != null) {
//            String userId = "User" + currentUser.getId(); // Get the current user's UID
//            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("userSetting");
//            userRef.child("selectedMusicItem").setValue(curSavedMusicItem);
//        }
//    }

//    public void loadSavedMusicItemSelection(OnMusicItemLoadedListener listener) {
//        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users/User" + appContext.getCurrentUser().getId() + "/userSetting");
//
//        userRef.child("selectedMusicItem").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    MusicItem musicItem = dataSnapshot.getValue(MusicItem.class);
//                    listener.onMusicItemLoaded(musicItem);
//                } else {
//                    listener.onMusicItemLoaded(null); // No selected music ID found
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.e("MusicManager", "Failed to read selectedMusicItem: " + databaseError.getMessage());
//                listener.onMusicItemLoaded(null);
//            }
//        });
//    }

//    public void toggleOnOff() {
//        if (isPlaying()) {
//            releaseMusic();
//        } else {
//            loadSavedMusicItemSelection(musicItem -> {
//                if (musicItem != null) {
//                    playMusicFromFirebase(musicItem);
//                } else {
//                    // Play default music if no saved music selection
//                    playMusicFromFirebase(new MusicItem());
//                }
//            });
//        }
//    }

    public MusicItem loadSavedMusicItemSelection() {
        return appContext.getCurrentUser().getUserSetting().getSelectedMusicItem();
    }

    public String loadSavedMusicUriSelection() {
        return appContext.getCurrentUser().getUserSetting().getSelectedMusicItem().getAudioUri();
    }

    public void toggleOnOff() {
        if (isPlaying()) {
            releaseMusic();
        }
        else {
            String savedMusicUri = loadSavedMusicUriSelection();
            if (savedMusicUri != null) {
                playMusicFromFirebase(savedMusicUri);
            }
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public interface OnMusicItemLoadedListener {
        void onMusicItemLoaded(MusicItem musicItem);
    }
}
