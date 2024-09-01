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

import java.util.ArrayList;
import java.util.List;

public class AppContext {
    private static AppContext instance;
    private User currentUser;
    private List<Tree> treeList;

    private AppContext() {
        treeList = new ArrayList<Tree>();
        loadTreeListFromDB();
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
