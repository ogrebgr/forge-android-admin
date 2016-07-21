package com.bolyartech.forge.admin.units.admin_user.admin_user_manage;

import com.bolyartech.forge.admin.data.AdminUser;


public interface Res_AdminUserManage {
    State getState();

    void disableUser(AdminUser user);
    void enableUser(AdminUser user);
    void delete(AdminUser user);

    void stateHandled();

    boolean getDisableResult();

    enum State {
        IDLE,
        DISABLING,
        DISABLE_OK,
        DISABLE_FAIL
    }
}
