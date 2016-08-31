package com.bolyartech.forge.admin.units.user.users;

import com.bolyartech.forge.admin.data.User;
import com.bolyartech.forge.android.app_unit.OperationResidentComponent;
import com.bolyartech.forge.base.exchange.forge.ForgeExchangeManagerListener;

import java.util.List;


public interface ResUsers extends OperationResidentComponent, ForgeExchangeManagerListener {
    void searchForUser(String pattern);

    List<User> getData();
}
