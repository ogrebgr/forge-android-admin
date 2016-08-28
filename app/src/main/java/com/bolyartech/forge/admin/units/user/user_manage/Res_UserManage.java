package com.bolyartech.forge.admin.units.user.user_manage;

import com.bolyartech.forge.admin.data.User;
import com.bolyartech.forge.android.app_unit.OperationResidentComponent;
import com.bolyartech.forge.base.exchange.forge.ForgeExchangeManagerListener;


public interface Res_UserManage extends OperationResidentComponent, ForgeExchangeManagerListener {
    void disableUser(User user);
    void enableUser(User user);

    boolean getDisableResult();
}
