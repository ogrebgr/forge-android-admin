package com.bolyartech.forge.admin.app;

public class CurrentUser {
    private final long mId;
    private final boolean mIsSuperadmin;


    public CurrentUser(long id, boolean isSuperadmin) {
        mId = id;
        mIsSuperadmin = isSuperadmin;
    }


    public long getId() {
        return mId;
    }


    public boolean isSuperadmin() {
        return mIsSuperadmin;
    }
}
