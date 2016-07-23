package com.bolyartech.forge.admin.units.admin_user.admin_user_chpwd;

import com.bolyartech.forge.android.app_unit.StatefulResidentComponent;


public interface Res_AdminUserChpwd extends StatefulResidentComponent<Res_AdminUserChpwd.State> {
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
