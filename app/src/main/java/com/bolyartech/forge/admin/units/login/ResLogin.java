package com.bolyartech.forge.admin.units.login;


import com.bolyartech.forge.android.app_unit.OperationResidentComponent;
import com.bolyartech.forge.base.exchange.forge.ForgeExchangeManagerListener;


/**
 * Created by ogre on 2016-01-05 13:59
 */
public interface ResLogin extends OperationResidentComponent, ForgeExchangeManagerListener {
    enum LoginError {
        INVALID_LOGIN,
        FAILED,
        UPGRADE_NEEDED
    }

    void login(String username, String password);
    void abortLogin();
    LoginError getLoginError();
}
