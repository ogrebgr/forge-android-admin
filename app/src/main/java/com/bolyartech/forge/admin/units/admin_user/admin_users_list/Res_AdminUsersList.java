package com.bolyartech.forge.admin.units.admin_user.admin_users_list;

import com.bolyartech.forge.admin.data.AdminUser;
import com.bolyartech.forge.android.app_unit.StatefulResidentComponent;

import java.util.List;


public interface Res_AdminUsersList extends StatefulResidentComponent<Res_AdminUsersList.State> {
    void loadAdminUsers();

    List<AdminUser> getData();

    void stateHandled();

    enum State {
        IDLE,
        WAITING_DATA,
        DATA_OK,
        DATA_FAIL
    }
}
