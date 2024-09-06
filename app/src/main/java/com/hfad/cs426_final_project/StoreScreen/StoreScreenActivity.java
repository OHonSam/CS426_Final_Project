package com.hfad.cs426_final_project.StoreScreen;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.DataStorage.Tree;
import com.hfad.cs426_final_project.MainScreen.MainScreenActivity;
import com.hfad.cs426_final_project.R;
import com.hfad.cs426_final_project.User;
import com.hfad.cs426_final_project.WelcomeScreenActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StoreScreenActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private AppContext appContext;
    private User currentUser;
    private List<Tree> treeList;

    private RecyclerView rcvTreeList;
    private TreeAdapter treeAdapter;
    private CheckBox ownTreeCheckBox;
    private TextView tvSunDisplay;

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_screen);
        initUI();

        setupToolbar();
        setupNavigationDrawer();
        setupBackPressed();

        appContext = AppContext.getInstance();
        currentUser = appContext.getCurrentUser();
        treeList = appContext.getTreeList();

        updateSunDisplay();
        initTreeRecylerView();
        initOwnCheckBox();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
    }

    private void setupNavigationDrawer() {
        toggle = new ActionBarDrawerToggle(this,
                drawer,
                toolbar,
                R.string.nav_open_drawer,
                R.string.nav_close_drawer);

        // Set the color of the drawer toggle icon
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black));

        // Add the toggle to the DrawerLayout
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        ViewGroup.LayoutParams params = navigationView.getLayoutParams();
        params.width = (int) (getResources().getDisplayMetrics().widthPixels / 2);

        navigationView.setLayoutParams(params);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_store_screen); // change
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        Intent intent = new Intent(this, MainScreenActivity.class);

        if (id == R.id.nav_store_screen)
            intent = new Intent(this, StoreScreenActivity.class);
        else if (id == R.id.nav_sign_out) {
            showSignOutDialog();
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        item.setChecked(true);
        drawer.closeDrawer(GravityCompat.START);

        startActivity(intent);
        finish();

        return true;
    }


    private void showSignOutDialog() {
        // Create an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the dialog title and message
        builder.setTitle("Sign Out");
        builder.setMessage("Are you sure you want to sign out?");

        // Set the "OK" button with a click listener
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(StoreScreenActivity.this, WelcomeScreenActivity.class); // change
                startActivity(intent);
                finish();

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
            }
        });

        // Set the "Cancel" button with a click listener
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void setupBackPressed() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    // If drawer is not open, proceed with default back action
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void initUI() {
        rcvTreeList = findViewById(R.id.rcvTreeList);
        ownTreeCheckBox = findViewById(R.id.ownTreeCheckBox);
        tvSunDisplay = findViewById(R.id.sunDisplay_storeScreen);
        navigationView = findViewById(R.id.nav_view_screen_choices);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view_screen_choices);
        drawer = findViewById(R.id.drawer_layout_main_screen);
    }

    public void updateSunDisplay() {
        tvSunDisplay.setText(String.valueOf(currentUser.getSun()));
    }

    private void initTreeRecylerView() {
        rcvTreeList.setLayoutManager(new GridLayoutManager(this, 3));
        treeAdapter = new TreeAdapter(this, filterTrees(treeList, ownTreeCheckBox.isChecked()), currentUser);
        rcvTreeList.setAdapter(treeAdapter);
    }

    private void initOwnCheckBox() {
        ownTreeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            treeAdapter.updateTreeList(filterTrees(treeList, isChecked));
        });
    }

    private List<Tree> filterTrees(List<Tree> treeList, boolean excludeOwned) {
        List<Tree> filteredList = new ArrayList<>();
        for (Tree tree : treeList) {
            if (excludeOwned && currentUser.hasTree(tree)) {
                continue;
            }
            filteredList.add(tree);
        }
        return filteredList;
    }


}
