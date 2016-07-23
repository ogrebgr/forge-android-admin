package com.bolyartech.forge.admin.units.user.users;

import com.bolyartech.forge.admin.data.User;
import com.bolyartech.forge.android.app_unit.ResidentComponentState;
import com.bolyartech.forge.android.app_unit.StatefulResidentComponent;

import java.util.List;

import static com.bolyartech.forge.android.app_unit.ResidentComponentState.Type.*;


public interface Res_Users extends StatefulResidentComponent<Res_Users.State> {
    void searchForUser(String pattern);

    List<User> getData();

    void stateHandled();

    enum State implements ResidentComponentState {
        IDLE(START),
        WAITING_DATA(TRANSIENT),
        DATA_OK(END),
        DATA_FAIL(END);

        private final ResidentComponentState.Type mType;


        State(ResidentComponentState.Type type) {
            mType = type;
        }


        public ResidentComponentState.Type getType() {
            return mType;
        }
    }
}
