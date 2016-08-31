package com.bolyartech.forge.admin.units.admin_user.admin_user_chpwd;

import com.bolyartech.forge.android.app_unit.SideEffectOperationResidentComponent;
import com.bolyartech.forge.base.exchange.forge.ForgeExchangeManagerListener;


public interface ResAdminUserChpwd extends SideEffectOperationResidentComponent<Void, Integer>, ForgeExchangeManagerListener {
    void save(long userId, String password);
}
