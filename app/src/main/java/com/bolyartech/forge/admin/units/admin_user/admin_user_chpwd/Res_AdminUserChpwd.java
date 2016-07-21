package com.bolyartech.forge.admin.units.admin_user.admin_user_chpwd;

public interface Res_AdminUserChpwd {
    State getState();
    void stateHandled();
    void save(long userId, String password);

    int getLastError();

    enum State {
        IDLE,
        SAVING,
        SAVE_OK,
        SAVE_FAIL
    }
}
