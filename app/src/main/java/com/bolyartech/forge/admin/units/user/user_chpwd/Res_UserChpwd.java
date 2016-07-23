package com.bolyartech.forge.admin.units.user.user_chpwd;

import com.bolyartech.forge.android.app_unit.ResidentComponentState;
import com.bolyartech.forge.android.app_unit.StatefulResidentComponent;

import static com.bolyartech.forge.android.app_unit.ResidentComponentState.Type.*;


public interface Res_UserChpwd extends StatefulResidentComponent<Res_UserChpwd.State> {
    void stateHandled();
    void save(long userId, String password);

    int getLastError();

    enum State implements ResidentComponentState {
        IDLE(START),
        SAVING(TRANSIENT),
        SAVE_OK(END),
        SAVE_FAIL(END);

        private final Type mType;


        State(Type type) {
            mType = type;
        }


        public Type getType() {
            return mType;
        }
    }
}
