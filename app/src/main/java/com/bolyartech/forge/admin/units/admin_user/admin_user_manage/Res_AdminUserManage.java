package com.bolyartech.forge.admin.units.admin_user.admin_user_manage;

import com.bolyartech.forge.admin.data.AdminUser;
import com.bolyartech.forge.android.app_unit.StatefulResidentComponent;


public interface Res_AdminUserManage extends StatefulResidentComponent<Res_AdminUserManage.State> {
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
