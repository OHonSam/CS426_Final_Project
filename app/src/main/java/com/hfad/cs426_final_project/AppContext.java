package com.hfad.cs426_final_project;

public class AppContext {
    private static AppContext instance;
    private User currentUser;

    private AppContext() {
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
}
