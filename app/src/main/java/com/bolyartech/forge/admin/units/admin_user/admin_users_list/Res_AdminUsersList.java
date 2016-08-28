package com.bolyartech.forge.admin.units.admin_user.admin_users_list;

import com.bolyartech.forge.admin.data.AdminUser;
import com.bolyartech.forge.android.app_unit.OperationResidentComponent;
import com.bolyartech.forge.base.exchange.forge.ForgeExchangeManagerListener;

import java.util.List;


public interface Res_AdminUsersList extends OperationResidentComponent, ForgeExchangeManagerListener {
    void loadAdminUsers();

    List<AdminUser> getData();

    enum State {
        IDLE,
        WAITING_DATA,
        DATA_OK,
        DATA_FAIL
    }
}
