package com.bolyartech.forge.admin.units.main;

/**
 * Created by ogre on 2015-10-05
 */
public interface Res_Main {
    State getState();

    void login();

    void startSession();

    void abortLogin();

    void logout();

    void internetAvailable();

    void resetState();

    void onConnectivityChange();

    enum State {
        IDLE,
        LOGGING_IN,
        STARTING_SESSION,
        SESSION_STARTED_OK,
        SESSION_START_FAIL,
        LOGIN_INVALID,
        LOGIN_FAIL,
        UPGRADE_NEEDED,
    }
}
