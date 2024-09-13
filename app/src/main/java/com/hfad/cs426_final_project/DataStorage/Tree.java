package com.hfad.cs426_final_project.DataStorage;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Tree {
    private int id;
    private String imgUri;
    private int cost;

    public Tree() {
        // default
        this.id = 0;
        this.cost = 120;
        this.imgUri = "https://firebasestorage.googleapis.com/v0/b/focus-da00f.appspot.com/o/Trees%2Ftree0.png?alt=media&token=7d72dc39-0005-4f8a-a80b-7123274b5ede";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public boolean sameID(Tree tree) {
        return this.id == tree.id;
    }
}
