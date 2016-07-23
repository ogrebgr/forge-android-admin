package com.bolyartech.forge.admin.units.user.user_chpwd;

import com.bolyartech.forge.android.app_unit.StatefulResidentComponent;


public interface Res_UserChpwd extends StatefulResidentComponent<Res_UserChpwd.State> {
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
