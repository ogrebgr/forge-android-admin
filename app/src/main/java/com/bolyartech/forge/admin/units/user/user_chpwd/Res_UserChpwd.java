package com.bolyartech.forge.admin.units.user.user_chpwd;

public interface Res_UserChpwd {
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
