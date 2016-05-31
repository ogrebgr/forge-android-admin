package com.bolyartech.forge.admin.units.admin_user_create;

public interface Res_AdminUserCreate {
    State getState();
    void resetState();
    void save(String username,
              String name,
              String password,
              boolean superAdmin);

    int getLastError();

    enum State {
        IDLE,
        SAVING,
        SAVE_OK,
        SAVE_FAIL
    }
}
