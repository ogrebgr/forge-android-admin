package com.bolyartech.forge.admin.units.login;


import com.bolyartech.forge.admin.app.BasicResponseCodes;
import com.bolyartech.forge.android.app_unit.ResidentComponentState;
import com.bolyartech.forge.android.app_unit.StatefulResidentComponent;

import static com.bolyartech.forge.android.app_unit.ResidentComponentState.Type.*;


/**
 * Created by ogre on 2016-01-05 13:59
 */
public interface Res_Login extends StatefulResidentComponent<Res_Login.State> {
    enum State implements ResidentComponentState {
        IDLE(START),
        LOGGING_IN(TRANSIENT),
        LOGIN_FAIL(END),
        STARTING_SESSION(TRANSIENT),
        SESSION_STARTED_OK(END),
        SESSION_START_FAIL(END);

        private final Type mType;


        State(Type type) {
            mType = type;
        }


        public Type getType() {
            return mType;
        }
    }


    void login(String username, String password);
    void abortLogin();
    BasicResponseCodes.Errors getLastError();
}
