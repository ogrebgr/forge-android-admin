package com.bolyartech.forge.admin.units.user.user_manage;

import com.bolyartech.forge.admin.data.AdminUser;
import com.bolyartech.forge.admin.data.User;


public interface Res_UserManage {
        State getState();

    void disableUser(User user);
    void enableUser(User user);


    boolean getDisableResult();

    void resetState();

    enum State {
        IDLE,
        DISABLING,
        DISABLE_OK,
        DISABLE_FAIL
    }
}