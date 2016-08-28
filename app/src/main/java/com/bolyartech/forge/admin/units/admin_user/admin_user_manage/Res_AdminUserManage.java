package com.bolyartech.forge.admin.units.admin_user.admin_user_manage;

import com.bolyartech.forge.admin.data.AdminUser;
import com.bolyartech.forge.android.app_unit.OperationResidentComponent;
import com.bolyartech.forge.base.exchange.forge.ForgeExchangeManagerListener;


public interface Res_AdminUserManage extends OperationResidentComponent, ForgeExchangeManagerListener {
    void disableUser(AdminUser user);
    void enableUser(AdminUser user);
    void delete(AdminUser user);

    boolean getDisableResult();
}
