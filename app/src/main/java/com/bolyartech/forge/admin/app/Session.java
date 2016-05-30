package com.bolyartech.forge.admin.app;


import android.support.annotation.NonNull;

import com.bolyartech.forge.base.misc.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;


public interface Session {
    boolean isLoggedIn();

    /**
     * @param ttl seconds
     */
    void startSession(int ttl, Info info);

    Info getInfo();

    /**
     * mark session as last active current time
     */
    void prolong();

    void logout();


    class Info {
        private final long mUserId;
        private final boolean mSuperAdmin;


        public Info(long userId, boolean superAdmin) {
            mUserId = userId;
            mSuperAdmin = superAdmin;
        }


        public long getUserId() {
            return mUserId;
        }


        public boolean isSuperAdmin() {
            return mSuperAdmin;
        }


        public static Info fromJson(@NonNull JSONObject jobj) throws JSONException {
            return new Info(jobj.getLong("user_id"),
                            jobj.getBoolean("super_admin")
                            );
        }
    }
}
