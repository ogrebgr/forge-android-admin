package com.bolyartech.forge.admin.units.login;


import com.bolyartech.forge.admin.app.BasicResponseCodes;
import com.bolyartech.forge.android.app_unit.StatefulResidentComponent;


/**
 * Created by ogre on 2016-01-05 13:59
 */
public interface Res_Login extends StatefulResidentComponent<Res_Login.State> {
    enum State {
        IDLE,
        LOGGING_IN,
        LOGIN_FAIL,
        STARTING_SESSION,
        SESSION_STARTED_OK,
        SESSION_START_FAIL
    }


    void login(String username, String password);
    void abortLogin();
    BasicResponseCodes.Errors getLastError();
}
