package com.hfad.cs426_final_project.DataStorage;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
        fetchUri(new OnUriFetchedListener() {
            @Override
            public void onUriFetched() {

            }
        });
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

    public void fetchUri(OnUriFetchedListener listener) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Trees/tree" + id + ".png");

        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            setImgUri(String.valueOf(uri));
            listener.onUriFetched(); // Notify that the URI has been fetched
        }).addOnFailureListener(exception -> {
            // Handle the case where the image is not found or any other error
            listener.onUriFetched(); // Still notify that the URI fetch attempt is complete
        });
    }

    public interface OnUriFetchedListener {
        void onUriFetched();
    }
}
