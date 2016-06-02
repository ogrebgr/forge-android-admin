package com.bolyartech.forge.admin.data;

import com.google.gson.annotations.SerializedName;


public class User {
    @SerializedName("id")
    private final long mId;
    @SerializedName("username")
    private final String mUsername;
    @SerializedName("disabled")
    private final boolean mIsDisabled;
    @SerializedName("screen_name")
    private final String mScreenName;


    public User(long id, String username, boolean isDisabled, String screenName) {
        mId = id;
        mUsername = username;
        mIsDisabled = isDisabled;
        mScreenName = screenName;
    }


    public long getId() {
        return mId;
    }


    public String getUsername() {
        return mUsername;
    }


    public boolean isDisabled() {
        return mIsDisabled;
    }


    public String getScreenName() {
        return mScreenName;
    }
}