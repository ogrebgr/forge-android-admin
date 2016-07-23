package com.bolyartech.forge.admin.units.admin_user.admin_user_create;

import com.bolyartech.forge.android.app_unit.StatefulResidentComponent;


public interface Res_AdminUserCreate extends StatefulResidentComponent<Res_AdminUserCreate.State> {
    void stateHandled();
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
