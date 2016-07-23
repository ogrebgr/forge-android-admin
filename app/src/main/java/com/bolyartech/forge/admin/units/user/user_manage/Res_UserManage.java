package com.bolyartech.forge.admin.units.user.user_manage;

import com.bolyartech.forge.admin.data.User;
import com.bolyartech.forge.android.app_unit.ResidentComponentState;
import com.bolyartech.forge.android.app_unit.StatefulResidentComponent;

import static com.bolyartech.forge.android.app_unit.ResidentComponentState.Type.*;


public interface Res_UserManage extends StatefulResidentComponent<Res_UserManage.State> {
    void disableUser(User user);
    void enableUser(User user);


    boolean getDisableResult();

    void stateHandled();

    enum State implements ResidentComponentState {
        IDLE(START),
        DISABLING(TRANSIENT),
        DISABLE_OK(END),
        DISABLE_FAIL(END);

        private final Type mType;


        State(Type type) {
            mType = type;
        }


        public Type getType() {
            return mType;
        }
    }
}
