package com.bolyartech.forge.admin.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.bolyartech.forge.admin.dagger.ForApplication;
import com.bolyartech.forge.base.misc.StringUtils;

import javax.inject.Inject;


public class LoginPrefsImpl implements LoginPrefs {
    public static final String KEY_USERNAME = "Username";
    public static final String KEY_PASSWORD = "Password";

    private static final String PREFERENCES_FILE = "Login prefs";
    private final SharedPreferences mPrefs;

    private boolean mNeedSave = false;

    private String mUsername;
    private String mPassword;


    @Inject
    public LoginPrefsImpl(@ForApplication Context ctx) {
        mPrefs = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);

        mUsername = mPrefs.getString(KEY_USERNAME, null);
        mPassword = mPrefs.getString(KEY_PASSWORD, null);
    }


    @Override
    public String getUsername() {
        return mUsername;
    }


    @Override
    public void setUsername(String username) {
        if (mUsername == null || (username != null && !mUsername.equals(username))) {
            mUsername = username;
            mNeedSave = true;
        }
    }


    @Override
    public String getPassword() {
        return mPassword;
    }


    @Override
    public void setPassword(String password) {
        if (mPassword == null || (password != null && !mPassword.equals(password))) {
            mPassword = password;
            mNeedSave = true;
        }
    }


    @Override
    public void save() {
        if (mNeedSave) {
            Editor ed = mPrefs.edit();
            ed.putString(KEY_USERNAME, mUsername);
            ed.putString(KEY_PASSWORD, mPassword);
            ed.apply();
        }
    }


    public boolean hasLoginCredentials() {
        return StringUtils.isNotEmpty(mUsername) && StringUtils.isNotEmpty(mPassword);
    }

}
