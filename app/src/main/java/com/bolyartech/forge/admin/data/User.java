package com.bolyartech.forge.admin.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


public class User implements Parcelable {
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

    protected User(Parcel in) {
        mId = in.readLong();
        mUsername = in.readString();
        mIsDisabled = in.readByte() != 0x00;
        mScreenName = in.readString();
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
        dest.writeString(mScreenName);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}