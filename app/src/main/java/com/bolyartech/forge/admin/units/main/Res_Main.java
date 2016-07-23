package com.bolyartech.forge.admin.units.main;

import com.bolyartech.forge.android.app_unit.ResidentComponentState;
import com.bolyartech.forge.android.app_unit.StatefulResidentComponent;

import static com.bolyartech.forge.android.app_unit.ResidentComponentState.Type.*;


/**
 * Created by ogre on 2015-10-05
 */
public interface Res_Main extends StatefulResidentComponent<Res_Main.State>
{
    ;

    void login();

    void startSession();

    void abortLogin();

    void logout();

    void internetAvailable();

    void stateHandled();

    void onConnectivityChange();

    enum State implements ResidentComponentState {
        IDLE(START),
        LOGGING_IN(TRANSIENT),
        STARTING_SESSION(TRANSIENT),
        SESSION_STARTED_OK(END),
        SESSION_START_FAIL(END),
        LOGIN_INVALID(END),
        LOGIN_FAIL(END),
        UPGRADE_NEEDED(END);

        private final Type mType;


        State(Type type) {
            mType = type;
        }


        public Type getType() {
            return mType;
        }
    }
}
