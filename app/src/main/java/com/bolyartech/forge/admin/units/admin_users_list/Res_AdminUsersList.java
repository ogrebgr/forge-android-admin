package com.bolyartech.forge.admin.units.admin_users_list;

import com.bolyartech.forge.admin.data.AdminUser;

import java.util.List;


public interface Res_AdminUsersList {
    State getState();

    void loadAdminUsers();

    List<AdminUser> getData();

    void reset();

    enum State {
        IDLE,
        WAITING_DATA,
        DATA_OK,
        DATA_FAIL
    }
}
