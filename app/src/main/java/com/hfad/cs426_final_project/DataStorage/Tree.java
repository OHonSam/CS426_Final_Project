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
        id = 0;
        cost = 120;
        fetchUri();
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

    public Task<Void> fetchUri() {
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Trees/tree" + id + ".png");

        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            setImgUri(String.valueOf(uri));
            taskCompletionSource.setResult(null);
        }).addOnFailureListener(taskCompletionSource::setException);
        return taskCompletionSource.getTask();
    }

    public boolean sameID(Tree tree) {
        return this.id == tree.id;
    }
}
