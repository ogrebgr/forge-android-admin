package com.bolyartech.forge.admin.units.admin_user.admin_user_create;

import com.bolyartech.forge.android.app_unit.SideEffectOperationResidentComponent;
import com.bolyartech.forge.base.exchange.forge.ForgeExchangeManagerListener;


public interface Res_AdminUserCreate extends SideEffectOperationResidentComponent<Void, Integer>, ForgeExchangeManagerListener {
    void save(String username,
              String name,
              String password,
              boolean superAdmin);
}
