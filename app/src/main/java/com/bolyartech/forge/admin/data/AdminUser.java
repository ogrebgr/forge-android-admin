package com.bolyartech.forge.admin.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


public class AdminUser implements Parcelable {
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

    protected AdminUser(Parcel in) {
        mId = in.readLong();
        mUsername = in.readString();
        mIsDisabled = in.readByte() != 0x00;
        mIsSuperAdmin = in.readByte() != 0x00;
        mName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mUsername);
        dest.writeByte((byte) (mIsDisabled ? 0x01 : 0x00));
        dest.writeByte((byte) (mIsSuperAdmin ? 0x01 : 0x00));
        dest.writeString(mName);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<AdminUser> CREATOR = new Parcelable.Creator<AdminUser>() {
        @Override
        public AdminUser createFromParcel(Parcel in) {
            return new AdminUser(in);
        }

        @Override
        public AdminUser[] newArray(int size) {
            return new AdminUser[size];
        }
    };
}