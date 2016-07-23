package com.bolyartech.forge.admin.units.user.user_manage;

import com.bolyartech.forge.admin.data.User;
import com.bolyartech.forge.android.app_unit.StatefulResidentComponent;


public interface Res_UserManage extends StatefulResidentComponent<Res_UserManage.State> {
    void disableUser(User user);
    void enableUser(User user);


    boolean getDisableResult();

    void stateHandled();

    enum State {
        IDLE,
        DISABLING,
        DISABLE_OK,
        DISABLE_FAIL
    }
}
