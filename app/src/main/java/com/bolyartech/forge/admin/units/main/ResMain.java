package com.bolyartech.forge.admin.units.main;

import com.bolyartech.forge.android.app_unit.OperationResidentComponent;
import com.bolyartech.forge.base.exchange.forge.ForgeExchangeManagerListener;


/**
 * Created by ogre on 2015-10-05
 */
public interface ResMain extends OperationResidentComponent, ForgeExchangeManagerListener {
    void login();

    void abortLogin();

    void logout();

    void internetAvailable();

    void onConnectivityChange();

    LoginError getLoginError();

    enum LoginError {
        INVALID_LOGIN,
        FAILED,
        UPGRADE_NEEDED
    }


    enum AutoregisteringError {
        FAILED,
        UPGRADE_NEEDED
    }
}
