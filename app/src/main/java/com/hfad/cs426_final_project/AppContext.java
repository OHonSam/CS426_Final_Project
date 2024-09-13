package com.hfad.cs426_final_project;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.hfad.cs426_final_project.DataStorage.Block;
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

    private AppContext() {
        treeList = new ArrayList<Tree>();
        loadTreeListFromDB().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    fetchTrees();
                }
                else {
                    // Handle the error
                    Exception e = task.getException();
                    // Show error or take appropriate action
                }
            }
        });
        grassList = new ArrayList<>();
        loadGrassListFromDB();
    }

    private void fetchTrees() {
        fetchTreeUriSequentially(0); // Start fetching URIs from the first tree
    }

    private void fetchTreeUriSequentially(int index) {
        if (index >= treeList.size()) {
            // All trees have been processed
            return;
        }

        Tree tree = treeList.get(index);
        tree.fetchUri().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                fetchTreeUriSequentially(index + 1);
            }
        });
    }

    private void fetchOwnTrees() {
        fetchOwnTreeUriSequentially(0); // Start fetching URIs from the first tree
    }

    private void fetchOwnTreeUriSequentially(int index) {
        if (index >= currentUser.getOwnTrees().size()) {
            // All trees have been processed
            return;
        }

        Tree tree = currentUser.getOwnTrees().get(index);
        tree.fetchUri().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                fetchOwnTreeUriSequentially(index + 1);
            }
        });
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
        fetchOwnTrees();
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
                List<Task<Void>> addTreeTasks = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Tree tree = dataSnapshot.getValue(Tree.class);
                    if (tree != null) {
                        addTreeTasks.add(addNewtree(tree));
                    }
                }
                // Once all fetchUri tasks are completed, we complete the main task
                Tasks.whenAll(addTreeTasks).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        taskCompletionSource.setResult(null);
                    } else {
                        taskCompletionSource.setException(task.getException());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                taskCompletionSource.setException(error.toException());
            }
        });
        return taskCompletionSource.getTask();
    }

    private Task<Void> addNewtree(Tree tree) {
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();
        treeList.add(tree);
        taskCompletionSource.setResult(null);
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

    private void loadGrassListFromDB() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Blocks/Grass");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                grassList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Block block = dataSnapshot.getValue(Block.class);
                    grassList.add(block);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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