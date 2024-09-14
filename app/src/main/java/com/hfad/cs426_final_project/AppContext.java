package com.hfad.cs426_final_project;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import com.hfad.cs426_final_project.DataStorage.Block;
import com.hfad.cs426_final_project.DataStorage.User;
import com.hfad.cs426_final_project.MainScreen.Clock.Clock;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hfad.cs426_final_project.DataStorage.Tree;

import java.util.ArrayList;
import java.util.List;

public class AppContext {
    private static AppContext instance;
    private User currentUser;
    private List<Tree> treeList;
    private List<Block> grassList;

    private Clock currentClock;

    private AppContext() {}

    public static synchronized AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

    public Task<Void> loadData() {
        treeList = new ArrayList<>();
        grassList = new ArrayList<>();

        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();

        // Load both lists, wait for both tasks to complete
        Task<Void> treeListTask = loadTreeListFromDB();
        Task<Void> grassListTask = loadGrassListFromDB();

        // Wait for both tasks to complete before setting the instance as ready
        Tasks.whenAll(treeListTask, grassListTask).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    taskCompletionSource.setResult(null); // Mark the task as complete
                } else {
                    // Handle error
                    Exception e = task.getException();
                    taskCompletionSource.setException(e); // Pass the error if any task fails
                }
            }
        });
        return taskCompletionSource.getTask();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public List<Tree> getTreeList() {
        return treeList;
    }

    private Task<Void> loadTreeListFromDB() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Trees");
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                treeList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Tree tree = dataSnapshot.getValue(Tree.class);
                    treeList.add(tree);
                }
                taskCompletionSource.setResult(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                taskCompletionSource.setException(error.toException());
            }
        });
        return taskCompletionSource.getTask();
    }

    public void saveUserInfo() {
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child("User" + currentUser.getId());
            userRef.setValue(currentUser);
        }
    }

    public List<Block> getGrassList() {
        return grassList;
    }

    private Task<Void> loadGrassListFromDB() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Blocks/Grass");
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                grassList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Block block = dataSnapshot.getValue(Block.class);
                    grassList.add(block);
                }
                taskCompletionSource.setResult(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                taskCompletionSource.setException(error.toException());
            }
        });
        return taskCompletionSource.getTask();
    }

    public Clock getCurrentClock() {
        return currentClock;
    }
    public void setCurrentClock(Clock currentClock) {
        this.currentClock = currentClock;
    }

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View currentFocus = activity.getCurrentFocus();
        if (currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            currentFocus.clearFocus();
        }
    }
}