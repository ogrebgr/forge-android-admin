package com.bolyartech.forge.admin.units.main;

import com.bolyartech.forge.android.app_unit.MultiOperationResidentComponent;
import com.bolyartech.forge.android.app_unit.OperationResidentComponent;
import com.bolyartech.forge.base.exchange.forge.ForgeExchangeManagerListener;


/**
 * Created by ogre on 2015-10-05
 */
public interface ResMain extends MultiOperationResidentComponent<ResMain.Operation> {
    void login();

    void abortLogin();

    void logout();

    int getLoginError();

    enum Operation {
        AUTO_REGISTERING,
        LOGIN,
        LOGOUT
    }
}
