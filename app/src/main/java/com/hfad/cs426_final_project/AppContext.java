package com.hfad.cs426_final_project;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hfad.cs426_final_project.DataStorage.Tree;
import com.hfad.cs426_final_project.MainScreen.Music.MusicItem;

import java.util.ArrayList;
import java.util.List;

public class AppContext {
    private static AppContext instance;
    private User currentUser;
    private List<Tree> treeList;
    private List<MusicItem> musicList;

    private AppContext() {
        treeList = new ArrayList<Tree>();
        loadTreeListFromDB();

        musicList = new ArrayList<MusicItem>();
        loadMusicListFromDB();
    }

    public static synchronized AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    private void loadMusicListFromDB() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Musics");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                musicList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MusicItem musicItem = dataSnapshot.getValue(MusicItem.class);
                    if (musicItem != null) {
                        // Fetch URI for the tree image from Firebase Storage
                        fetchMusicItemsAudioUri(musicItem);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void fetchMusicItemsAudioUri(MusicItem musicItem) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Musics/music" + musicItem.getTitle() + ".mp3");

        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            // Set the audio URI for the music item
            musicItem.setAudioUri(String.valueOf(uri));
            musicList.add(musicItem); // Add the music item to the list after setting the URI
        }).addOnFailureListener(exception -> {
            // Handle the case where the audio file is not found or any other error occurs
            // You may still want to add the music item without the audio URI or log the error
            musicList.add(musicItem); // Add the music item without the URI
        });
    }

    public List<MusicItem> getMusicList() {
        return musicList;
    }

    private void loadTreeListFromDB() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Trees");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                treeList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Tree tree = dataSnapshot.getValue(Tree.class);
                    if (tree != null) {
                        // Fetch URI for the tree image from Firebase Storage
                        fetchTreeImageUri(tree);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void fetchTreeImageUri(Tree tree) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Trees/tree" + tree.getId() + ".png");

        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            tree.setImgUri(String.valueOf(uri));
            treeList.add(tree); // Add tree to the list after setting the URI
        }).addOnFailureListener(exception -> {
            // Handle the case where the image is not found or any other error
            // You may still want to add the tree without the image or log the error
            treeList.add(tree); // Add tree without the URI
        });
    }
}
