package com.bolyartech.forge.admin.app;

public class CurrentUserHolder {
    private CurrentUser mCurrentUser;


    public CurrentUser getCurrentUser() {
        return mCurrentUser;
    }


    public void setCurrentUser(CurrentUser currentUser) {
        mCurrentUser = currentUser;
    }
}
