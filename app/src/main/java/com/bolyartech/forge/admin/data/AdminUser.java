package com.bolyartech.forge.admin.data;

import com.google.gson.annotations.SerializedName;


public class AdminUser {
    @SerializedName("id")
    private final long mId;
    @SerializedName("username")
    private final String mUsername;
    @SerializedName("is_disabled")
    private final boolean mIsDisabled;
    @SerializedName("is_super_admin")
    private final boolean mIsSuperAdmin;
    @SerializedName("name")
    private final String mName;


    public AdminUser(long id, String username, boolean isDisabled, boolean isSuperAdmin, String name) {
        mId = id;
        mUsername = username;
        mIsDisabled = isDisabled;
        mIsSuperAdmin = isSuperAdmin;
        mName = name;
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


    public boolean isSuperAdmin() {
        return mIsSuperAdmin;
    }


    public String getName() {
        return mName;
    }
}
