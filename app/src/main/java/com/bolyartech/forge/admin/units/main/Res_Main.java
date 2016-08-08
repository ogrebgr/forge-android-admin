package com.bolyartech.forge.admin.units.main;

import com.bolyartech.forge.android.app_unit.StatefulResidentComponent;


/**
 * Created by ogre on 2015-10-05
 */
public interface Res_Main extends StatefulResidentComponent<Res_Main.State> {
    void login();

    void startSession();

    void abortLogin();

    void logout();

    void internetAvailable();

    void stateHandled();

    void onConnectivityChange();

    enum State{
        IDLE,
        LOGGING_IN,
        STARTING_SESSION,
        SESSION_STARTED_OK,
        SESSION_START_FAIL,
        LOGIN_INVALID,
        LOGIN_FAIL,
        UPGRADE_NEEDED
    }
}
